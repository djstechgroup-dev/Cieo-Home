package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGoogleLogin;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionGoogleLoginDataDesc extends AbstractFunctionDataDesc<FunctionGoogleLogin> {
    public FunctionGoogleLoginDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionGoogleLoginDataDesc(copyDesc);
    }

    @Override
    public FunctionGoogleLogin getFunction() {
        return new FunctionGoogleLogin(this, AGApplicationState.getInstance());
    }
}
