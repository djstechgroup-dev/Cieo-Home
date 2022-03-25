package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetContext;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGetContextDataDesc extends AbstractFunctionDataDesc<FunctionGetContext> {

    public FunctionGetContextDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionGetContextDataDesc(copyDesc);
	}

    @Override
    public FunctionGetContext getFunction() {
        return new FunctionGetContext(this, AGApplicationState.getInstance());
    }
}
