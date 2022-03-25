package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.JavaScriptExecute;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class JavaScriptExecuteDesc extends AbstractFunctionDataDesc<JavaScriptExecute> {
    public JavaScriptExecuteDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new JavaScriptExecuteDesc(copyDesc);
    }

    @Override
    public JavaScriptExecute getFunction() {
        return new JavaScriptExecute(this, AGApplicationState.getInstance());
    }
}
