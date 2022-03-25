package com.kinetise.data.descriptors.actions;

import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Core class for all function related classes
 **/
public abstract class AbstractFunctionDataDesc<T extends AbstractFunction> implements Serializable {

    private ArrayList<VariableDataDesc> mAttributes = new ArrayList<VariableDataDesc>();
    private ActionDataDesc mActionDescriptor;

    public AbstractFunctionDataDesc(ActionDataDesc actionDataDesc) {
        mActionDescriptor = actionDataDesc;
        if (mActionDescriptor != null)
            mActionDescriptor.addFunction(this);
    }

    public void addAttribute(VariableDataDesc attr) {
        mAttributes.add(attr);
    }

    public VariableDataDesc[] getAttributes() {
        VariableDataDesc[] array = new VariableDataDesc[mAttributes.size()];
        mAttributes.toArray(array);

        return array;
    }

    public AbstractAGElementDataDesc getContextDataDesc() {
        if (mActionDescriptor == null)
            return null;
        return mActionDescriptor.getContextDataDesc();
    }

    public ActionDataDesc getActionDescriptor() {
        return mActionDescriptor;
    }

    public AbstractFunctionDataDesc copy(ActionDataDesc copyDesc) {
        AbstractFunctionDataDesc copied = this.copyInstance(copyDesc);
        int size = mAttributes.size();
        for (int i = 0; i < size; i++) {
            copied.mAttributes.add(mAttributes.get(i).copy(copyDesc.getContextDataDesc()));
        }
        return copied;
    }

    protected abstract AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc);

    public void resolveVariableParameters() {
        int size = mAttributes.size();
        for (int i = 0; i < size; i++) {
            mAttributes.get(i).resolveVariable();
        }
    }

    abstract public T getFunction();
}
