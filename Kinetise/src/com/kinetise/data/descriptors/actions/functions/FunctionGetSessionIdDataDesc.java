package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetSessionId;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGetSessionIdDataDesc extends AbstractFunctionDataDesc<FunctionGetSessionId> {

	public FunctionGetSessionIdDataDesc(ActionDataDesc actionDataDesc) {
		super(actionDataDesc);
	}

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionGetSessionIdDataDesc(copyDesc);
	}

	@Override
	public FunctionGetSessionId getFunction() {
		return new FunctionGetSessionId(this, AGApplicationState.getInstance());
	}

}
