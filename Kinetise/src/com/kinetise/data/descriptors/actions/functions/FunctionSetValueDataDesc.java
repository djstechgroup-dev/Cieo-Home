package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSetValue;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionSetValueDataDesc extends AbstractFunctionDataDesc {

    public FunctionSetValueDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionSetValueDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionSetValue(this, AGApplicationState.getInstance());
    }
}
