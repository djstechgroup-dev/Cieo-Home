package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetCurrentTime;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionGetCurrentTimeDataDesc extends AbstractFunctionDataDesc<FunctionGetCurrentTime> {

    public FunctionGetCurrentTimeDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionGetCurrentTimeDataDesc(copyDesc);
    }

    @Override
    public FunctionGetCurrentTime getFunction() {
        return new FunctionGetCurrentTime(this, AGApplicationState.getInstance());
    }
}
