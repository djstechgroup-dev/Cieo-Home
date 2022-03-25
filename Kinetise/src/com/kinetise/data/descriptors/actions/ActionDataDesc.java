package com.kinetise.data.descriptors.actions;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.helpers.DescriptorCompiler.EqualsUtil;
import com.kinetise.helpers.HashCodeBuilder;

import java.util.ArrayList;

public class ActionDataDesc {

    private ArrayList<AbstractFunctionDataDesc> mFunctions = new ArrayList<AbstractFunctionDataDesc>();
    private MultiActionDataDesc mMultiActionDataDest;

    public ActionDataDesc(MultiActionDataDesc multiActionDataDesc) {
        mMultiActionDataDest = multiActionDataDesc;
    }

    /**
     * @return Descriptor that action is connected to
     */
    public AbstractAGElementDataDesc getContextDataDesc() {
        if (mMultiActionDataDest != null)
            return mMultiActionDataDest.getContextDataDesc();
        else
            return null;
    }

    /**
     * Adds next function to current action
     */
    public void addFunction(AbstractFunctionDataDesc functionDataDesc) {
        mFunctions.add(functionDataDesc);
    }

    /**
     * @return Array of function basing of internal list of functions assigned to action
     */
    public AbstractFunctionDataDesc[] getFunctions() {
        AbstractFunctionDataDesc[] array = new AbstractFunctionDataDesc[mFunctions.size()];
        mFunctions.toArray(array);

        return array;
    }

    public ActionDataDesc copy(MultiActionDataDesc copyDesc) {
        ActionDataDesc copied = new ActionDataDesc(copyDesc);
        for (AbstractFunctionDataDesc functionDataDesc : getFunctions()) {
            functionDataDesc.copy(copied);
        }
        return copied;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(50);
        for (AbstractFunctionDataDesc func : mFunctions) {
            if (builder.length() > 0)
                builder.append(".");
            builder.append(func.getClass().getSimpleName()).append(": (");

            for (VariableDataDesc attr : func.getAttributes()) {
                builder.append(attr.getStringValue());
            }

            builder.append(")");
        }

        return builder.toString();
    }

    @Override
    public boolean equals(java.lang.Object obj) {
        EqualsUtil.LogClassName("ActionDataDesc");
        if (this == obj) return true;
        if (!(obj instanceof ActionDataDesc)) return false;
        ActionDataDesc thatObject = (ActionDataDesc) obj;

        if (!EqualsUtil.areEqual(this.mFunctions, thatObject.mFunctions)) {
            return EqualsUtil.LogAndReturnFalse(this, "mFunctions");
        }

        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(mFunctions.hashCode())
                .toHashCode();
    }


    public void resolveVariablesInParamtres() {
        for (AbstractFunctionDataDesc functionDataDesc : mFunctions) {
            functionDataDesc.resolveVariableParameters();
        }
    }

    public <T extends AbstractFunctionDataDesc> boolean hasFunction(Class<T> clazz) {
        for (AbstractFunctionDataDesc functionDataDesc : mFunctions) {
            if (clazz.isAssignableFrom(functionDataDesc.getClass())) {
                return true;
            }
        }
        return false;
    }
}
