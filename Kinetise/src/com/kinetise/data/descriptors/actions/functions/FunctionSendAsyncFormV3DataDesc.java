package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSendAsyncFormV3;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionSendAsyncFormV3DataDesc extends AbstractFunctionDataDesc<FunctionSendAsyncFormV3> {
    public FunctionSendAsyncFormV3DataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionSendAsyncFormV3DataDesc(copyDesc);
    }

    @Override
    public FunctionSendAsyncFormV3 getFunction() {
        return new FunctionSendAsyncFormV3(this, AGApplicationState.getInstance());
    }
}
