package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetGuid;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionGetGuidDataDesc extends AbstractFunctionDataDesc<FunctionGetGuid> {

    public FunctionGetGuidDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionGetGuidDataDesc(copyDesc);
    }

    @Override
    public FunctionGetGuid getFunction() {
        return new FunctionGetGuid(this, AGApplicationState.getInstance());
    }
}
