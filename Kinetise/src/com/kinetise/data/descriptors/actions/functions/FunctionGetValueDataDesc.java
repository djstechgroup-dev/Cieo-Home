package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetValue;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionGetValueDataDesc extends AbstractFunctionDataDesc {
    public FunctionGetValueDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionGetValueDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionGetValue(this, AGApplicationState.getInstance());
    }
}
