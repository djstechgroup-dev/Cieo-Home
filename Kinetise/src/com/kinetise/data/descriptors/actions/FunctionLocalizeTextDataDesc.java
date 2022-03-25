package com.kinetise.data.descriptors.actions;

import com.kinetise.data.application.actionmanager.functioncommands.FunctionLocalizeText;

public class FunctionLocalizeTextDataDesc extends AbstractFunctionDataDesc<FunctionLocalizeText>{

    public FunctionLocalizeTextDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionLocalizeTextDataDesc(copyDesc);
    }

    @Override
    public FunctionLocalizeText getFunction() {
        return new FunctionLocalizeText(this,null);
    }
}
