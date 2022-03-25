package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionPlaySound;
import com.kinetise.data.application.sdk.ActionManager;


public class StopAllSounds extends FunctionPlaySound {
    public StopAllSounds(StopAllSoundsDataDesc stopAllSoundsDataDesc, AGApplicationState instance) {
        super(stopAllSoundsDataDesc, instance);
    }

    @Override
    public Object execute(Object desc) {
        ActionManager.getInstance().stopAllSounds();
        return null;
    }
}
