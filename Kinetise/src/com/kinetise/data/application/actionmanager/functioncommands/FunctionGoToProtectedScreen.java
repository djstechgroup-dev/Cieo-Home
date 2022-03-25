package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.screenhistory.ApplicationState;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.types.AGScreenTransition;

public class FunctionGoToProtectedScreen extends AbstractFunction {

    private static ApplicationState mApplicationState;

    public FunctionGoToProtectedScreen(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        AGApplicationState.getInstance().getScreenLoader().loadApplicationState(mApplicationState, AGScreenTransition.NONE);
        return null;
    }

    public static void setDestinationAplicationState(ApplicationState applicationState) {
        mApplicationState = applicationState;
    }
}
