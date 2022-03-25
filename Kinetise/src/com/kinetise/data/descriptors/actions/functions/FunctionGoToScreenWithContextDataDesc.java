package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGoToScreenWithContext;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGoToScreenWithContextDataDesc extends AbstractFunctionDataDesc<FunctionGoToScreenWithContext> {

    public FunctionGoToScreenWithContextDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionGoToScreenWithContextDataDesc(copyDesc);
	}

    @Override
    public FunctionGoToScreenWithContext getFunction() {
        return new FunctionGoToScreenWithContext(this, AGApplicationState.getInstance());
    }
}
