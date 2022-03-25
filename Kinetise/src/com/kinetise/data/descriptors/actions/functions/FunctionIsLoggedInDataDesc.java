package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionIsLoggedIn;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionIsLoggedInDataDesc extends AbstractFunctionDataDesc {
    public FunctionIsLoggedInDataDesc(ActionDataDesc action) {
        super(action);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionIsLoggedInDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionIsLoggedIn(this, AGApplicationState.getInstance());
    }
}
