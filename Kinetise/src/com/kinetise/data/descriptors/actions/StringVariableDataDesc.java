package com.kinetise.data.descriptors.actions;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

public class StringVariableDataDesc extends VariableDataDesc {

    public StringVariableDataDesc(String value) {
        setResolvedValue(value);
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public void resolveVariable() {

    }

    public StringVariableDataDesc copy(AbstractAGElementDataDesc copyDesc) {
        return new StringVariableDataDesc(getStringValue());
    }

}
