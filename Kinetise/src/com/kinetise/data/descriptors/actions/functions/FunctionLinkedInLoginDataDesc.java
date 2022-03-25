package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionLinkedInLogin;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionLinkedInLoginDataDesc extends AbstractFunctionDataDesc<FunctionLinkedInLogin> {
    public FunctionLinkedInLoginDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionLinkedInLoginDataDesc(copyDesc);
    }

    @Override
    public FunctionLinkedInLogin getFunction() {
        return new FunctionLinkedInLogin(this, AGApplicationState.getInstance());
    }
}
