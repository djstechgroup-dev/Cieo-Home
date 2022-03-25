package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionRefresh;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionRefreshDataDesc extends AbstractFunctionDataDesc<FunctionRefresh> {

    public FunctionRefreshDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionRefreshDataDesc(copyDesc);
	}

    @Override
    public FunctionRefresh getFunction() {
        return new FunctionRefresh(this, AGApplicationState.getInstance());
    }
}
