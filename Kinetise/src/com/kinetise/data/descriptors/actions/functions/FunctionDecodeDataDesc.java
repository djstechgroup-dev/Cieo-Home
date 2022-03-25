package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionDecode;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

/**
 * Created by Kuba Komorowski on 2014-12-18.
 */
public class FunctionDecodeDataDesc extends AbstractFunctionDataDesc<FunctionDecode> {
    public FunctionDecodeDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionDecodeDataDesc(copyDesc);
    }

    @Override
    public FunctionDecode getFunction() {
        return new FunctionDecode(this, AGApplicationState.getInstance());
    }
}
