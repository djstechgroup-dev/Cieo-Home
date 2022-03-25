package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionEndGPSTracking;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionEndGPSTrackingDataDesc extends AbstractFunctionDataDesc<FunctionEndGPSTracking> {

    public FunctionEndGPSTrackingDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionEndGPSTrackingDataDesc(copyDesc);
	}

    @Override
    public FunctionEndGPSTracking getFunction() {
        return new FunctionEndGPSTracking(this, AGApplicationState.getInstance());
    }
}
