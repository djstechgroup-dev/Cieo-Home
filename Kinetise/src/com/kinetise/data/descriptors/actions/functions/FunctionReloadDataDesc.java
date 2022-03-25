package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionReload;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionReloadDataDesc extends AbstractFunctionDataDesc<FunctionReload> {
    public FunctionReloadDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
        return new FunctionRefreshDataDesc(copyDesc);
    }

    @Override
    public FunctionReload getFunction() {
        return new FunctionReload(this, AGApplicationState.getInstance());
    }

}