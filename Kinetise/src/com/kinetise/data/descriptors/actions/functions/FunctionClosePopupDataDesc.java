package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionClosePopup;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionClosePopupDataDesc extends AbstractFunctionDataDesc<FunctionClosePopup> {

    public FunctionClosePopupDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionClosePopupDataDesc(copyDesc);
	}

    @Override
    public FunctionClosePopup getFunction() {
        return new FunctionClosePopup(this, AGApplicationState.getInstance());
    }
}
