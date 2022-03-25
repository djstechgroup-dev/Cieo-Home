package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionShowInVideoPlayer;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionShowInVideoPlayerDataDesc extends AbstractFunctionDataDesc {
    public FunctionShowInVideoPlayerDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionShowInVideoPlayerDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionShowInVideoPlayer(this, AGApplicationState.getInstance());
    }
}
