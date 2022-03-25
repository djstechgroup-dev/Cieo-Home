package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionEquals;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionEqualsDataDesc extends AbstractFunctionDataDesc {

    public FunctionEqualsDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionEqualsDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionEquals(this, AGApplicationState.getInstance());
    }
}
