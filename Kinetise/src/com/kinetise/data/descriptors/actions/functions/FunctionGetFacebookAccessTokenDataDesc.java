package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetFacebookAccessToken;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

/**
 * Created by Kuba Komorowski on 2014-06-27.
 */
public class FunctionGetFacebookAccessTokenDataDesc extends AbstractFunctionDataDesc<FunctionGetFacebookAccessToken>{

    public FunctionGetFacebookAccessTokenDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public FunctionGetFacebookAccessTokenDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionGetFacebookAccessTokenDataDesc(copyDesc);
    }

    @Override
    public FunctionGetFacebookAccessToken getFunction() {
        return new FunctionGetFacebookAccessToken(this, AGApplicationState.getInstance());
    }

}
