package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;

import java.security.InvalidParameterException;

public class FunctionShowInWebBrowser extends AbstractFunction {

    public FunctionShowInWebBrowser(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    /**
     * Starts new WebBrowserApp {@link com.kinetise.data.application.externalapplications.WebBrowserApp}
     * to open url in system Browser
     *
     * @param desc Unneeded descriptor
     * @return null
     */
    @Override
    public Object execute(Object desc) {
        super.execute(desc);

        if (desc != null) {
            throw new InvalidParameterException(
                    "FunctionShowInWebBrowser function does not execute on descriptor! The descriptor should be null!");
        }

        VariableDataDesc attribute = mFunctionDataDesc.getAttributes()[0];
        String url = attribute.getStringValue();
        ActionManager.getInstance().showInWebBrowser(url);
        return null;
    }


}
