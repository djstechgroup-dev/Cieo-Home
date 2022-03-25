package com.kinetise.data.descriptors.actions;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

public class NullVariableDataDesc extends VariableDataDesc {

    public NullVariableDataDesc() {
        setResolvedValue(null);
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public void resolveVariable() {
    }

    public NullVariableDataDesc copy(AbstractAGElementDataDesc copyDesc) {
        return new NullVariableDataDesc();
    }

}
