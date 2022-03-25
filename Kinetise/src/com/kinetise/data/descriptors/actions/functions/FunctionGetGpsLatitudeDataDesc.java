package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetGpsLatitude;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGetGpsLatitudeDataDesc extends AbstractFunctionDataDesc<FunctionGetGpsLatitude> {

	public FunctionGetGpsLatitudeDataDesc(ActionDataDesc actionDataDesc) {
		super(actionDataDesc);
	}

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionGetGpsLatitudeDataDesc(copyDesc);
	}

	@Override
	public FunctionGetGpsLatitude getFunction() {
		return new FunctionGetGpsLatitude(this, AGApplicationState.getInstance());
	}

}
