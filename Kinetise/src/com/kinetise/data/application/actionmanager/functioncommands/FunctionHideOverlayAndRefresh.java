package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.functions.HideOverlayAndRefreshFunctionDataDesc;

public class FunctionHideOverlayAndRefresh extends AbstractFunction {
    public FunctionHideOverlayAndRefresh(HideOverlayAndRefreshFunctionDataDesc functionDataDesc, AGApplicationState agApplicationState) {
        super(functionDataDesc, agApplicationState);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        ActionManager.getInstance().hideOverlayAndRefresh();
        return null;
    }
}
