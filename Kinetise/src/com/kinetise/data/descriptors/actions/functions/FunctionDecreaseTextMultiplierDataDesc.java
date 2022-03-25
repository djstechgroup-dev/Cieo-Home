package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionDecreaseTextMultiplier;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionDecreaseTextMultiplierDataDesc extends AbstractFunctionDataDesc<FunctionDecreaseTextMultiplier> {


    public FunctionDecreaseTextMultiplierDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionDecreaseTextMultiplierDataDesc(copyDesc);
    }

    @Override
    public FunctionDecreaseTextMultiplier getFunction() {
        return new FunctionDecreaseTextMultiplier(this, AGApplicationState.getInstance());
    }
}
