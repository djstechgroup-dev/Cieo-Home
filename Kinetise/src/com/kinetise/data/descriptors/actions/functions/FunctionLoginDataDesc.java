package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionLogin;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionLoginDataDesc extends AbstractFunctionDataDesc<FunctionLogin> {

    public FunctionLoginDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionLoginDataDesc(copyDesc);
	}

    @Override
    public FunctionLogin getFunction() {
        return new FunctionLogin(this, AGApplicationState.getInstance());
    }
}
