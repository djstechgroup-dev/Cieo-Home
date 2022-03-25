package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSaveFormToLocalDB;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionSaveFormToLocalDBDataDesc extends AbstractFunctionDataDesc<FunctionSaveFormToLocalDB>{
    public FunctionSaveFormToLocalDBDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected FunctionSaveFormToLocalDBDataDesc copyInstance(ActionDataDesc copyDesc) {
       return new FunctionSaveFormToLocalDBDataDesc(copyDesc);
    }

    @Override
    public FunctionSaveFormToLocalDB getFunction() {
        return new FunctionSaveFormToLocalDB(this, AGApplicationState.getInstance());
    }
}
