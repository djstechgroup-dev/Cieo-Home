package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionPlaySound;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class StopAllSoundsDataDesc extends AbstractFunctionDataDesc<FunctionPlaySound> {
    public StopAllSoundsDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new StopAllSoundsDataDesc(copyDesc);
    }

    @Override
    public FunctionPlaySound getFunction() {
        return new StopAllSounds(this, AGApplicationState.getInstance());
    }
}
