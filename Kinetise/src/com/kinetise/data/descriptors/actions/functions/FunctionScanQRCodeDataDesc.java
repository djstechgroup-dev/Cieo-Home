package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionScanQRCode;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionScanQRCodeDataDesc extends AbstractFunctionDataDesc {
    public FunctionScanQRCodeDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionScanQRCodeDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionScanQRCode(this, AGApplicationState.getInstance());
    }
}
