package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.ShowOverlayFunction;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class ShowOverlayFunctionDataDesc extends AbstractFunctionDataDesc {
    public ShowOverlayFunctionDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new ShowOverlayFunctionDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new ShowOverlayFunction(this, AGApplicationState.getInstance());
    }
}
