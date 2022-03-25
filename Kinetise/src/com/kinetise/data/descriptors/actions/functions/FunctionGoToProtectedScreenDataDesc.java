package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGoToProtectedScreen;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionGoToProtectedScreenDataDesc extends AbstractFunctionDataDesc<FunctionGoToProtectedScreen> {

    public FunctionGoToProtectedScreenDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionGoToProtectedScreenDataDesc(copyDesc);
    }

    @Override
    public FunctionGoToProtectedScreen getFunction() {
        return new FunctionGoToProtectedScreen(this, AGApplicationState.getInstance());
    }
}
