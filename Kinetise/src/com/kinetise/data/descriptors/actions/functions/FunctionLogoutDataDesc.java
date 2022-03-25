package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionLogout;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionLogoutDataDesc extends AbstractFunctionDataDesc<FunctionLogout> {

    public FunctionLogoutDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public FunctionLogoutDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionLogoutDataDesc(copyDesc);
	}

    @Override
    public FunctionLogout getFunction() {
        return new FunctionLogout(this, AGApplicationState.getInstance());
    }
}
