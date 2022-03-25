package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGoToScreen;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGoToScreenDataDesc extends AbstractFunctionDataDesc<FunctionGoToScreen> {

    public FunctionGoToScreenDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionGoToScreenDataDesc(copyDesc);
	}

    @Override
    public FunctionGoToScreen getFunction() {
        return new FunctionGoToScreen(this, AGApplicationState.getInstance());
    }
}
