package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionOpenMapCurrentLocation;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionOpenMapCurrentLocationDataDesc extends AbstractFunctionDataDesc<FunctionOpenMapCurrentLocation> {

    public FunctionOpenMapCurrentLocationDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionOpenMapCurrentLocationDataDesc(copyDesc);
    }

    @Override
    public FunctionOpenMapCurrentLocation getFunction() {
        return new FunctionOpenMapCurrentLocation(this, AGApplicationState.getInstance());
    }
}
