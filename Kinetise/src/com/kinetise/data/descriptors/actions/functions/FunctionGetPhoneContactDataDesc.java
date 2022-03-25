package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetPhoneContact;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionGetPhoneContactDataDesc extends AbstractFunctionDataDesc {
    public FunctionGetPhoneContactDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionGetPhoneContactDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionGetPhoneContact(this, AGApplicationState.getInstance());
    }
}
