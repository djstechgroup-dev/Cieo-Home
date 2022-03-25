package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionEqualOrLower;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionEqualOrLowerDataDesc extends AbstractFunctionDataDesc{
    public FunctionEqualOrLowerDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionEqualOrLowerDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionEqualOrLower(this, AGApplicationState.getInstance());
    }
}
