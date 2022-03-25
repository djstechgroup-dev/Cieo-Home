package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetActiveItemField;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGetActiveItemFieldDataDesc extends AbstractFunctionDataDesc<FunctionGetActiveItemField> {

    public FunctionGetActiveItemFieldDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionGetActiveItemFieldDataDesc(copyDesc);
	}

    @Override
    public FunctionGetActiveItemField getFunction() {
        return new FunctionGetActiveItemField(this, AGApplicationState.getInstance());
    }
}
