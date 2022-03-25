package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionOr;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionOrDataDesc extends AbstractFunctionDataDesc {
    public FunctionOrDataDesc(ActionDataDesc action) {
        super(action);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionOrDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionOr(this, AGApplicationState.getInstance());
    }
}
