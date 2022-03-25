package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionPreviousElement;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionPreviousElementDataDesc extends AbstractFunctionDataDesc<FunctionPreviousElement> {

    public FunctionPreviousElementDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionPreviousElementDataDesc(copyDesc);
	}

    @Override
    public FunctionPreviousElement getFunction() {
        return new FunctionPreviousElement(this, AGApplicationState.getInstance());
    }
}
