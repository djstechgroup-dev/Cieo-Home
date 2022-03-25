package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionOpenEmail;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

/**
 * Created by Kuba Komorowski on 2014-12-10.
 */
public class FunctionOpenEmailDataDesc extends AbstractFunctionDataDesc<FunctionOpenEmail> {
    public FunctionOpenEmailDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionOpenEmailDataDesc(copyDesc);
    }

    @Override
    public FunctionOpenEmail getFunction() {
        return new FunctionOpenEmail(this, AGApplicationState.getInstance());
    }
}
