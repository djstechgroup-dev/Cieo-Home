package com.kinetise.data.descriptors.actions;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.helpers.DescriptorCompiler.EqualsUtil;
import com.kinetise.helpers.HashCodeBuilder;

import java.util.ArrayList;

public class MultiActionDataDesc {

    private ArrayList<ActionDataDesc> mActions;
    private AbstractAGElementDataDesc mDataDescActionContext;

    public MultiActionDataDesc(AbstractAGElementDataDesc actionContextDataDesc) {
        mActions = new ArrayList<>();
        mDataDescActionContext = actionContextDataDesc;
    }

    /**
     * @return Descriptor that action is connected to
     */
    public AbstractAGElementDataDesc getContextDataDesc() {
        return mDataDescActionContext;
    }

    /**
     * Set descriptor that actions is connected to
     */
    public void setContextDataDesc(AbstractAGElementDataDesc elem) {
        mDataDescActionContext = elem;
    }

    /**
     * Adds next function to current action
     */
    public void addAction(ActionDataDesc actionDataDesc) {
        mActions.add(actionDataDesc);
    }

    /**
     * @return Array of function basing of internal list of functions assigned to action
     */
    public ActionDataDesc[] getActions() {
        ActionDataDesc[] array = new ActionDataDesc[mActions.size()];
        mActions.toArray(array);

        return array;
    }

    public <T extends AbstractFunctionDataDesc> boolean hasFunction(Class<T> clazz) {

        for (ActionDataDesc action : mActions) {
            if (action.hasFunction(clazz)) {
                return true;
            }
        }
        return false;
    }

    public MultiActionDataDesc copy(AbstractAGElementDataDesc copyDesc) {
        MultiActionDataDesc copied = new MultiActionDataDesc(copyDesc);
        for (ActionDataDesc actionDataDesc : getActions()) {
            ActionDataDesc action = actionDataDesc.copy(copied);
            copied.addAction(action);
        }
        return copied;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(50);

        for (ActionDataDesc func : mActions) {
            builder.append(func.toString());
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        EqualsUtil.LogClassName("ActionDataDesc");
        if (this == obj) return true;
        if (!(obj instanceof MultiActionDataDesc)) return false;
        MultiActionDataDesc thatObject = (MultiActionDataDesc) obj;

        if (!EqualsUtil.areEqual(this.mActions, thatObject.mActions)) {
            return EqualsUtil.LogAndReturnFalse(this, "mActions");
        }

        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(mActions.hashCode())
                .toHashCode();
    }

    public AbstractFunctionDataDesc[] getAllFunctions() {
        ArrayList<AbstractFunctionDataDesc> functions = new ArrayList<>();
        for (ActionDataDesc action : mActions) {
            for (AbstractFunctionDataDesc function : action.getFunctions())
                functions.add(function);
        }
        return functions.toArray(new AbstractFunctionDataDesc[functions.size()]);
    }

    public void resolveVariablesInParameters() {
        for (ActionDataDesc actionDataDesc : mActions) {
            actionDataDesc.resolveVariablesInParamtres();
        }
    }
}
