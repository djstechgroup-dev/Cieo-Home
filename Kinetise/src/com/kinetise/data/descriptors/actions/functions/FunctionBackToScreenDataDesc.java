package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionBackToScreen;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionBackToScreenDataDesc extends AbstractFunctionDataDesc<FunctionBackToScreen> {

    public FunctionBackToScreenDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
        return new FunctionBackToScreenDataDesc(copyDesc);
    }

    @Override
    public FunctionBackToScreen getFunction() {
        return new FunctionBackToScreen(this, AGApplicationState.getInstance());
    }
}
