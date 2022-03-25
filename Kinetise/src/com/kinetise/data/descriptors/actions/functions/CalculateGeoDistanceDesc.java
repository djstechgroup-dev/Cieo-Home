package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.CalculateGeoDistance;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class CalculateGeoDistanceDesc extends AbstractFunctionDataDesc<CalculateGeoDistance> {
    public CalculateGeoDistanceDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new CalculateGeoDistanceDesc(copyDesc);
    }

    @Override
    public CalculateGeoDistance getFunction() {
        return new CalculateGeoDistance(this, AGApplicationState.getInstance());
    }
}
