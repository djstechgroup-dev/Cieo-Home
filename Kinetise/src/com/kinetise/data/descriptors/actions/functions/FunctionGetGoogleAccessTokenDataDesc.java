package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetGoogleUserAccessToken;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionGetGoogleAccessTokenDataDesc extends AbstractFunctionDataDesc<FunctionGetGoogleUserAccessToken> {

    public FunctionGetGoogleAccessTokenDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public FunctionGetGoogleAccessTokenDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionGetGoogleAccessTokenDataDesc(copyDesc);
    }

    @Override
    public FunctionGetGoogleUserAccessToken getFunction() {
        return new FunctionGetGoogleUserAccessToken(this, AGApplicationState.getInstance());
    }

}
