package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetGpsLongitude;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGetGpsLongitudeDataDesc extends AbstractFunctionDataDesc<FunctionGetGpsLongitude> {

	public FunctionGetGpsLongitudeDataDesc(ActionDataDesc actionDataDesc) {
		super(actionDataDesc);
	}

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionGetGpsLongitudeDataDesc(copyDesc);
	}

	@Override
	public FunctionGetGpsLongitude getFunction() {
		return new FunctionGetGpsLongitude(this, AGApplicationState.getInstance());
	}

}
