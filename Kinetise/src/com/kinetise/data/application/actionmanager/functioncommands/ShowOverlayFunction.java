package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class ShowOverlayFunction extends AbstractFunction {
    public ShowOverlayFunction(AbstractFunctionDataDesc functionDesc, AGApplicationState agApplicationState) {
        super(functionDesc, agApplicationState);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String overlayId = mFunctionDataDesc.getAttributes()[0].getStringValue();
        ActionManager.getInstance().showOverlay(overlayId);
        return null;
    }
}
