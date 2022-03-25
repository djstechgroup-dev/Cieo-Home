package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetHeaderParamValue;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionGetHeaderParamValueDataDesc extends AbstractFunctionDataDesc<FunctionGetHeaderParamValue> {

    public FunctionGetHeaderParamValueDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionGetHeaderParamValueDataDesc(copyDesc);
	}

    @Override
    public FunctionGetHeaderParamValue getFunction() {
        return new FunctionGetHeaderParamValue(this, AGApplicationState.getInstance());
    }
}
