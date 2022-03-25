package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionStartGPSTracking;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionStartGPSTrackingDataDesc extends AbstractFunctionDataDesc<FunctionStartGPSTracking> {

    public FunctionStartGPSTrackingDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionStartGPSTrackingDataDesc(copyDesc);
	}

    @Override
    public FunctionStartGPSTracking getFunction() {
        return new FunctionStartGPSTracking(this, AGApplicationState.getInstance());
    }
}
