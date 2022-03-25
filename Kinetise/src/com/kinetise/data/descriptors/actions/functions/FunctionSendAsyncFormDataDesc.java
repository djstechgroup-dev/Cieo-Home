package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSendAsyncForm;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionSendAsyncFormDataDesc extends AbstractFunctionDataDesc<FunctionSendAsyncForm> {

    public FunctionSendAsyncFormDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionSendAsyncFormDataDesc(copyDesc);
    }

    @Override
    public FunctionSendAsyncForm getFunction() {
        return new FunctionSendAsyncForm(this, AGApplicationState.getInstance());
    }
}
