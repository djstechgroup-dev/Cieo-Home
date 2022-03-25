package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionFacebookLogin;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

/**
 * Created by Kuba Komorowski on 2014-08-04.
 */
public class FunctionFacebookLoginDataDesc extends AbstractFunctionDataDesc<FunctionFacebookLogin> {
    public FunctionFacebookLoginDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionFacebookLoginDataDesc(copyDesc);
    }

    @Override
    public FunctionFacebookLogin getFunction() {
        return new FunctionFacebookLogin(this, AGApplicationState.getInstance());
    }
}
