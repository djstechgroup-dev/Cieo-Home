package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionNextElement;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionNextElementDataDesc extends AbstractFunctionDataDesc<FunctionNextElement> {

    public FunctionNextElementDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public FunctionNextElementDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionNextElementDataDesc(copyDesc);
	}

    @Override
    public FunctionNextElement getFunction() {
        return new FunctionNextElement(this, AGApplicationState.getInstance());
    }
}
