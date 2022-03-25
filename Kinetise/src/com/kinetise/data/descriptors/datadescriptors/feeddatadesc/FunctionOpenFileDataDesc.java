package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionOpenFile;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionOpenFileDataDesc extends AbstractFunctionDataDesc<FunctionOpenFile> {

    public FunctionOpenFileDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionOpenFileDataDesc(copyDesc);
    }

    @Override
    public FunctionOpenFile getFunction() {
        return new FunctionOpenFile(this, AGApplicationState.getInstance());
    }
}
