package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSaveFormToLocalDB;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSynchronizeLocalTable;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionSynchronizeLocalTableDataDesc extends AbstractFunctionDataDesc<FunctionSynchronizeLocalTable>{
    public FunctionSynchronizeLocalTableDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected FunctionSynchronizeLocalTableDataDesc copyInstance(ActionDataDesc copyDesc) {
       return new FunctionSynchronizeLocalTableDataDesc(copyDesc);
    }

    @Override
    public FunctionSynchronizeLocalTable getFunction() {
        return new FunctionSynchronizeLocalTable(this, AGApplicationState.getInstance());
    }
}
