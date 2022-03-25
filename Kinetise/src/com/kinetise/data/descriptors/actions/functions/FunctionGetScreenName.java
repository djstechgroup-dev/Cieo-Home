package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;


public class FunctionGetScreenName extends AbstractFunction {
    public FunctionGetScreenName(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }


    @Override
    public Object execute(Object desc) {
        return ActionManager.getInstance().getScreenName();
    }
}
