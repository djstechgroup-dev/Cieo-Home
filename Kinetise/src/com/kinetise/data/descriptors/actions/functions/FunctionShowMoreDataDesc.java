package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionShowMore;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionShowMoreDataDesc extends AbstractFunctionDataDesc<FunctionShowMore> {

    public FunctionShowMoreDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionShowMoreDataDesc(copyDesc);
	}

    @Override
    public FunctionShowMore getFunction() {
        return new FunctionShowMore(this, AGApplicationState.getInstance());
    }


}
