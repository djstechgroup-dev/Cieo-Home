package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetLocalization;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionGetLocalizationDataDesc extends AbstractFunctionDataDesc {
    public FunctionGetLocalizationDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionGetLocalizationDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionGetLocalization(this, AGApplicationState.getInstance());
    }
}
