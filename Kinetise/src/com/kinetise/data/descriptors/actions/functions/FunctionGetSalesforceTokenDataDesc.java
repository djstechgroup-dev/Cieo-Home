package com.kinetise.data.descriptors.actions.functions;


import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetSalesforceAccessToken;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionGetSalesforceTokenDataDesc extends AbstractFunctionDataDesc<FunctionGetSalesforceAccessToken>{

    public FunctionGetSalesforceTokenDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionGetSalesforceTokenDataDesc(copyDesc);
    }

    @Override
    public FunctionGetSalesforceAccessToken getFunction() {
        return new FunctionGetSalesforceAccessToken(this, AGApplicationState.getInstance());
    }
}
