package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSendFormV2;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;


public class FunctionSendFormDataDesc extends AbstractFunctionDataDesc<FunctionSendFormV2> {

	public FunctionSendFormDataDesc(ActionDataDesc actionDataDesc) {
		super(actionDataDesc);
	}

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionSendFormDataDesc(copyDesc);
	}

	@Override
	public FunctionSendFormV2 getFunction() {
		return new FunctionSendFormV2(this, AGApplicationState.getInstance());
	}

}
