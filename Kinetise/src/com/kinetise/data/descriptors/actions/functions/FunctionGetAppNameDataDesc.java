package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetAppName;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGetAppNameDataDesc extends AbstractFunctionDataDesc<FunctionGetAppName> {

    public FunctionGetAppNameDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionGetAppNameDataDesc(copyDesc);
	}

    @Override
    public FunctionGetAppName getFunction() {
        return new FunctionGetAppName(this, AGApplicationState.getInstance());
    }
}
