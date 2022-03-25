package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetTwitterToken;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

/**
 * Created by Kuba Komorowski on 2014-09-24.
 */
public class FunctionGetTwitterTokenDataDesc extends AbstractFunctionDataDesc<FunctionGetTwitterToken> {

    public FunctionGetTwitterTokenDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionGetTwitterTokenDataDesc(copyDesc);
    }

    @Override
    public FunctionGetTwitterToken getFunction() {
        return new FunctionGetTwitterToken(this, AGApplicationState.getInstance());
    }
}
