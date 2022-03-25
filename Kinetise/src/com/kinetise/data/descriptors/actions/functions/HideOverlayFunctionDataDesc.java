package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.HideOverlayFunction;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class HideOverlayFunctionDataDesc extends AbstractFunctionDataDesc {
    public HideOverlayFunctionDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new HideOverlayFunctionDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new HideOverlayFunction(this, AGApplicationState.getInstance());
    }
}
