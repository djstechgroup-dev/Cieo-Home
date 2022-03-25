package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionPostToFacebook;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionPostToFacebookDataDesc extends AbstractFunctionDataDesc {

    public FunctionPostToFacebookDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionPostToFacebookDataDesc(copyDesc);
	}

    @Override
    public AbstractFunction getFunction() {
        return new FunctionPostToFacebook(this, AGApplicationState.getInstance());
    }
}
