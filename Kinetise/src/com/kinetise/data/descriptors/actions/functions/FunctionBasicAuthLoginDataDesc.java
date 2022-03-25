package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionBasicAuthLogin;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionBasicAuthLoginDataDesc extends AbstractFunctionDataDesc {
    public FunctionBasicAuthLoginDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionBasicAuthLoginDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionBasicAuthLogin(this, AGApplicationState.getInstance());
    }
}
