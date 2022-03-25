package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionShowInWebBrowser;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionShowInWebBrowserDataDesc extends AbstractFunctionDataDesc<FunctionShowInWebBrowser> {

    public FunctionShowInWebBrowserDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionShowInWebBrowserDataDesc(copyDesc);
	}

    @Override
    public FunctionShowInWebBrowser getFunction() {
        return new FunctionShowInWebBrowser(this, AGApplicationState.getInstance());
    }
}
