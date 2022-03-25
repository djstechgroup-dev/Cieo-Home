package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionOpenMap;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionOpenMapDataDesc extends AbstractFunctionDataDesc<FunctionOpenMap> {

    public FunctionOpenMapDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionOpenMapDataDesc(copyDesc);
    }

    @Override
    public FunctionOpenMap getFunction() {
        return new FunctionOpenMap(this, AGApplicationState.getInstance());
    }
}
