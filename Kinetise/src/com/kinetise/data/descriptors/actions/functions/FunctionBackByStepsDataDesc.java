package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionBackBySteps;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionBackByStepsDataDesc extends AbstractFunctionDataDesc<FunctionBackBySteps> {

    public FunctionBackByStepsDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionBackByStepsDataDesc(copyDesc);
    }

    @Override
    public FunctionBackBySteps getFunction() {
        return new FunctionBackBySteps(this, AGApplicationState.getInstance());
    }
}
