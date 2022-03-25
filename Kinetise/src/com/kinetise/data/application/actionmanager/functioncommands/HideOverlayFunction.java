package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.overalymanager.OverlayManager;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.functions.HideOverlayFunctionDataDesc;

public class HideOverlayFunction extends AbstractFunction {
    public HideOverlayFunction(HideOverlayFunctionDataDesc hideOverlayFunctionDataDesc, AGApplicationState agApplicationState) {
        super(hideOverlayFunctionDataDesc, agApplicationState);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        ActionManager.getInstance().hideOverlay();
        return null;
    }


}
