package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionOpenSms;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

/**
 * Created by Kuba Komorowski on 2014-12-10.
 */
public class FunctionOpenSmsDataDesc extends AbstractFunctionDataDesc<FunctionOpenSms> {
    public FunctionOpenSmsDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionOpenSmsDataDesc(copyDesc);
    }

    @Override
    public FunctionOpenSms getFunction() {
        return new FunctionOpenSms(this, AGApplicationState.getInstance());
    }
}
