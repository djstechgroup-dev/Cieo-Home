package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSynchronizeLocalDB;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSynchronizeLocalTable;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionSynchronizeLocalDBDataDesc extends AbstractFunctionDataDesc<FunctionSynchronizeLocalDB>{
    public FunctionSynchronizeLocalDBDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected FunctionSynchronizeLocalDBDataDesc copyInstance(ActionDataDesc copyDesc) {
       return new FunctionSynchronizeLocalDBDataDesc(copyDesc);
    }

    @Override
    public FunctionSynchronizeLocalDB getFunction() {
        return new FunctionSynchronizeLocalDB(this, AGApplicationState.getInstance());
    }
}
