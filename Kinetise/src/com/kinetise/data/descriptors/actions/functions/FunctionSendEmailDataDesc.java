package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionSendEmail;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionSendEmailDataDesc extends AbstractFunctionDataDesc<FunctionSendEmail> {

	public FunctionSendEmailDataDesc(ActionDataDesc actionDataDesc) {
		super(actionDataDesc);
	}

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionSendEmailDataDesc(copyDesc);
	}

	@Override
	public FunctionSendEmail getFunction() {
		return new FunctionSendEmail(this, AGApplicationState.getInstance());
	}

}
