package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionCall;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

/**
 * Created by Kuba Komorowski on 2014-12-09.
 */
public class FunctionCallDataDesc extends AbstractFunctionDataDesc<FunctionCall> {
    public FunctionCallDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionCallDataDesc(copyDesc);
    }

    @Override
    public FunctionCall getFunction() {
        return new FunctionCall(this, AGApplicationState.getInstance());
    }
}
