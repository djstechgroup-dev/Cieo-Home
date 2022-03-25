package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionPayment;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionPaymentDataDesc extends AbstractFunctionDataDesc<FunctionPayment> {

    public FunctionPaymentDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionPaymentDataDesc(copyDesc);
    }

    @Override
    public FunctionPayment getFunction() {
        return new FunctionPayment(this, AGApplicationState.getInstance());
    }
}
