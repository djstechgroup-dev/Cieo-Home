package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionHideOverlayAndRefresh;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class HideOverlayAndRefreshFunctionDataDesc extends AbstractFunctionDataDesc<FunctionHideOverlayAndRefresh> {
    public HideOverlayAndRefreshFunctionDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
        return new FunctionRefreshDataDesc(copyDesc);
    }

    @Override
    public FunctionHideOverlayAndRefresh getFunction() {
        return new FunctionHideOverlayAndRefresh(this, AGApplicationState.getInstance());
    }

}
