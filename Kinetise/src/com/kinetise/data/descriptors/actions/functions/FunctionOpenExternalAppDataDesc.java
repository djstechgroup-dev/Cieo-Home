package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionOpenExternalApp;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionOpenExternalAppDataDesc extends AbstractFunctionDataDesc<FunctionOpenExternalApp> {
    public FunctionOpenExternalAppDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionOpenExternalAppDataDesc(copyDesc);
    }

    @Override
    public FunctionOpenExternalApp getFunction() {
        return new FunctionOpenExternalApp(this, AGApplicationState.getInstance());
    }
}
