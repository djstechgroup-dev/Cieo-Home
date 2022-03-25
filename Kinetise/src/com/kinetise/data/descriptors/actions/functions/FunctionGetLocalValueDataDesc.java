package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetLocalValue;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionGetLocalValueDataDesc extends AbstractFunctionDataDesc {
    public FunctionGetLocalValueDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionGetLocalValueDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionGetLocalValue(this, AGApplicationState.getInstance());
    }
}
