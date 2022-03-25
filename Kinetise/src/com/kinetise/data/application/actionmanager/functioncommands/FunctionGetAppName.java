package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGetAppName extends AbstractFunction {

    public FunctionGetAppName(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc,application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        return AGApplicationState.getInstance().getApplicationDescription().getName();
    }
}
