package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSaveFormData;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionSaveFormDataDesc extends AbstractFunctionDataDesc<FunctionSaveFormData>{
    public FunctionSaveFormDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected FunctionSaveFormDataDesc copyInstance(ActionDataDesc copyDesc) {
       return new FunctionSaveFormDataDesc(copyDesc);
    }

    @Override
    public FunctionSaveFormData getFunction() {
        return new FunctionSaveFormData(this, AGApplicationState.getInstance());
    }
}
