package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;

public class FunctionBasicAuthLogout extends AbstractFunction {
    public FunctionBasicAuthLogout(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);

        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        String postLogoutScreenId = null;
        if (attributes.length > 0) {
            postLogoutScreenId = attributes[0].getStringValue();
        }
        ActionManager.getInstance().basicAuthLogout(postLogoutScreenId);
        return null;
    }
}
