package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.functions.FunctionIsLoggedInDataDesc;

public class FunctionIsLoggedIn extends AbstractFunction {
    public FunctionIsLoggedIn(FunctionIsLoggedInDataDesc functionIsLoggedInDataDesc, AGApplicationState instance) {
        super(functionIsLoggedInDataDesc, instance);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);

        if(ActionManager.getInstance().isLoggedIn())
            return StringLogicValues.TRUE;
        else
            return StringLogicValues.FALSE;
    }


}
