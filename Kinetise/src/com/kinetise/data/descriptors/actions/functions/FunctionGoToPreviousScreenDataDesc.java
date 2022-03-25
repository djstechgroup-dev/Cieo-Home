package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGoToPreviousScreen;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGoToPreviousScreenDataDesc extends AbstractFunctionDataDesc<FunctionGoToPreviousScreen> {

	public FunctionGoToPreviousScreenDataDesc(
			ActionDataDesc actionDataDesc) {
		super(actionDataDesc);
	}

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionGoToPreviousScreenDataDesc(copyDesc);
	}

	@Override
	public FunctionGoToPreviousScreen getFunction() {
		return new FunctionGoToPreviousScreen(this, AGApplicationState.getInstance());
	}
}
