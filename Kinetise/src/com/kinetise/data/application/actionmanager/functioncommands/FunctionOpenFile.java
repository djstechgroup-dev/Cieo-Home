package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionOpenFile extends AbstractFunction {
    private String url;

    public FunctionOpenFile(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        url = mFunctionDataDesc.getAttributes()[0].getStringValue();
        ActionManager.getInstance().openFile(url);
        return null;
    }

}
