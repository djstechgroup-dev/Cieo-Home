package com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionNot;

public class FunctionNotDataDesc extends AbstractFunctionDataDesc {
    public FunctionNotDataDesc(ActionDataDesc action) {
        super(action);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionNotDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionNot(this, AGApplicationState.getInstance());
    }
}
