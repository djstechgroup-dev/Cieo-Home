package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionDelay;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionDelayDataDesc extends AbstractFunctionDataDesc<FunctionDelay> {

    public FunctionDelayDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
        return new FunctionDelayDataDesc(copyDesc);
    }

    @Override
    public FunctionDelay getFunction() {
        return new FunctionDelay(this, AGApplicationState.getInstance());
    }
}
