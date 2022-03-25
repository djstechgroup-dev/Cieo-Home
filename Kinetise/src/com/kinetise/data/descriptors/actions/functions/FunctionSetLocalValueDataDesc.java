package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSetLocalValue;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionSetLocalValueDataDesc extends AbstractFunctionDataDesc {
    public FunctionSetLocalValueDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionSetLocalValueDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionSetLocalValue(this, AGApplicationState.getInstance());
    }
}
