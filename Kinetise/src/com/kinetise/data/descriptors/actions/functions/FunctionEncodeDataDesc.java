package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionEncode;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

/**
 * Created by Kuba Komorowski on 2014-12-10.
 */
public class FunctionEncodeDataDesc extends AbstractFunctionDataDesc<FunctionEncode> {
    public FunctionEncodeDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionEncodeDataDesc(copyDesc);
    }

    @Override
    public FunctionEncode getFunction() {
        return new FunctionEncode(this, AGApplicationState.getInstance());
    }
}
