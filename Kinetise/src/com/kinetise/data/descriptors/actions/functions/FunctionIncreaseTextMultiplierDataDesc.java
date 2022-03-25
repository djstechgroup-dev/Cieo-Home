package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionIncreaseTextMultiplier;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionIncreaseTextMultiplierDataDesc extends AbstractFunctionDataDesc<FunctionIncreaseTextMultiplier> {
    public FunctionIncreaseTextMultiplierDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionIncreaseTextMultiplierDataDesc(copyDesc);
    }

    @Override
    public FunctionIncreaseTextMultiplier getFunction() {
        return new FunctionIncreaseTextMultiplier(this, AGApplicationState.getInstance());
    }
}
