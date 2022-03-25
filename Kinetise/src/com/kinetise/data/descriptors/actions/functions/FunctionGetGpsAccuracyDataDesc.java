package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetGpsAccuracy;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGetGpsAccuracyDataDesc extends AbstractFunctionDataDesc<FunctionGetGpsAccuracy> {

	public FunctionGetGpsAccuracyDataDesc(ActionDataDesc actionDataDesc) {
		super(actionDataDesc);
	}

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionGetGpsAccuracyDataDesc(copyDesc);
	}

	@Override
	public FunctionGetGpsAccuracy getFunction() {
		return new FunctionGetGpsAccuracy(this, AGApplicationState.getInstance());
	}

}
