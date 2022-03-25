package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionBasicAuthLogout;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionBasicAuthLogoutDataDesc extends AbstractFunctionDataDesc {
    public FunctionBasicAuthLogoutDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionBasicAuthLogoutDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionBasicAuthLogout(this, AGApplicationState.getInstance());
    }
}
