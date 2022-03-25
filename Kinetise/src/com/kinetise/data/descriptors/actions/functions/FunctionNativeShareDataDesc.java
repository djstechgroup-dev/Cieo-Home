package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionNativeShare;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionNativeShareDataDesc extends AbstractFunctionDataDesc<FunctionNativeShare> {
    public FunctionNativeShareDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionNativeShareDataDesc(copyDesc);
    }

    @Override
    public FunctionNativeShare getFunction() {
        return new FunctionNativeShare(this, AGApplicationState.getInstance());
    }
}
