package com.kinetise.data.descriptors.actions;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

public abstract class VariableDataDesc {
    private Object mValue;
    private boolean mWasResolved = false;

    public VariableDataDesc(Object value) {
        mValue = value;
    }

    public VariableDataDesc() {
    }

    public abstract boolean isDynamic();

    public abstract void resolveVariable();

    public void setResolvedValue(Object value) {
        mWasResolved = true;
        mValue = value;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public Object getValue() {
        checkIfResolved();
        return mValue;
    }

    public String getStringValue() {
        checkIfResolved();

        if (mValue instanceof String)
            return (String) mValue;
        else {
            if (mValue != null)
                return mValue.toString();
            return null;
        }
    }

    public void copyValuesFrom(VariableDataDesc source) {
        mValue = source.mValue;
    }

    public void checkIfResolved() {
        if (isDynamic() && !mWasResolved) {
            throw new RuntimeException("Variable " + this.toString() + " was not resolved before trying to read it's value.");
        }
    }

    public abstract VariableDataDesc copy(AbstractAGElementDataDesc copyDesc);
}

