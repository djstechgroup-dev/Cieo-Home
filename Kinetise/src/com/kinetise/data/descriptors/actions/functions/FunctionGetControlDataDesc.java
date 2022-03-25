package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetControl;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGetControlDataDesc extends AbstractFunctionDataDesc<FunctionGetControl> {

    public FunctionGetControlDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionGetControlDataDesc(copyDesc);
	}

    @Override
    public FunctionGetControl getFunction() {
        return new FunctionGetControl(this, AGApplicationState.getInstance());
    }
}
