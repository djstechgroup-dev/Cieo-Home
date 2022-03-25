package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionReload;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionUpdate;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionUpdateDataDesc extends AbstractFunctionDataDesc<FunctionUpdate> {
    public FunctionUpdateDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
        return new FunctionUpdateDataDesc(copyDesc);
    }

    @Override
    public FunctionUpdate getFunction() {
        return new FunctionUpdate(this, AGApplicationState.getInstance());
    }

}