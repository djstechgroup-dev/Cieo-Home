package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetItemField;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGetItemFieldDataDesc extends AbstractFunctionDataDesc<FunctionGetItemField> {

    public FunctionGetItemFieldDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionGetItemFieldDataDesc(copyDesc);
	}

    @Override
    public FunctionGetItemField getFunction() {
        return new FunctionGetItemField(this, AGApplicationState.getInstance());
    }
}
