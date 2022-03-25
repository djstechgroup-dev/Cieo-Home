package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSendFormV3;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionSendFormV3DataDesc extends AbstractFunctionDataDesc<FunctionSendFormV3> {
    public FunctionSendFormV3DataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionSendFormV3DataDesc(copyDesc);
    }

    @Override
    public FunctionSendFormV3 getFunction() {
        return new FunctionSendFormV3(this, AGApplicationState.getInstance());
    }
}
