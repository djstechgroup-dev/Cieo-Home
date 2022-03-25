package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;


public class FunctionGetGoogleUserAccessToken extends AbstractFunction {
    public FunctionGetGoogleUserAccessToken(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        return ActionManager.getInstance().getGoogleUserAccessToken(); //teraz nie ma sensu trzymać i zwracać access_token od Google bo on jest krótkotrwały i przeznaczony dla weba
    }
}
