package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionAnd;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionAndDataDesc extends AbstractFunctionDataDesc {
    public FunctionAndDataDesc(ActionDataDesc action) {
        super(action);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionAndDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionAnd(this, AGApplicationState.getInstance());
    }
}
