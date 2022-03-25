package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionPlaySound;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class PlaySoundDataDesc extends AbstractFunctionDataDesc<FunctionPlaySound> {
    public PlaySoundDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new PlaySoundDataDesc(copyDesc);
    }

    @Override
    public FunctionPlaySound getFunction() {
        return new FunctionPlaySound(this, AGApplicationState.getInstance());
    }
}
