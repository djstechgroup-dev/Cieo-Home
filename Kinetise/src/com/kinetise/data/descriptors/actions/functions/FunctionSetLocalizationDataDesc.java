package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSetLocalization;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionSetLocalizationDataDesc extends AbstractFunctionDataDesc {
    public FunctionSetLocalizationDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionSetLocalizationDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionSetLocalization(this, AGApplicationState.getInstance());
    }
}
