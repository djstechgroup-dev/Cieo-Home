package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;

public class FunctionSetLocalization extends AbstractFunction {
    public FunctionSetLocalization(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        VariableDataDesc variableDataDesc = getFunctionDataDesc().getAttributes()[0];
        variableDataDesc.resolveVariable();
        String languageName = variableDataDesc.getStringValue();
        ActionManager.getInstance().setLocalization(languageName);
        return null;
    }
}
