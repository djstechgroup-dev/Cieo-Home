package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionOfflineReading;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionOfflineReadingDataDesc extends AbstractFunctionDataDesc<FunctionOfflineReading> {

    public FunctionOfflineReadingDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionOfflineReadingDataDesc(copyDesc);
    }

    @Override
    public FunctionOfflineReading getFunction() {
        return new FunctionOfflineReading(this, AGApplicationState.getInstance());
    }
}
