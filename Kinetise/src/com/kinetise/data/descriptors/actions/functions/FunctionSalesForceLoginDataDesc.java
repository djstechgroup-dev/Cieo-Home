package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSalesForceLogin;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionSalesForceLoginDataDesc extends AbstractFunctionDataDesc<FunctionSalesForceLogin> {
    public FunctionSalesForceLoginDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionSalesForceLoginDataDesc(copyDesc);
    }

    @Override
    public FunctionSalesForceLogin getFunction() {
        return new FunctionSalesForceLogin(this, AGApplicationState.getInstance());
    }
}
