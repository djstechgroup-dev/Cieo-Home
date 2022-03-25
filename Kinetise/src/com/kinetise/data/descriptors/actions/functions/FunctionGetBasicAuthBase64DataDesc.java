package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetBasicAuthBase64;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionGetBasicAuthBase64DataDesc extends AbstractFunctionDataDesc {
    public FunctionGetBasicAuthBase64DataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionGetBasicAuthBase64DataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionGetBasicAuthBase64(this, AGApplicationState.getInstance());
    }
}
