package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

import java.security.InvalidParameterException;

public class FunctionLocalizeText extends AbstractFunction {

    String mKey;

    public FunctionLocalizeText(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        readAttributes();
        return ActionManager.getInstance().localizeText(mKey);
    }


    public void readAttributes() {
        int parametersCount = mFunctionDataDesc.getAttributes().length;
        if (parametersCount != 1) {
            throw new InvalidParameterException();
        }
        mKey = mFunctionDataDesc.getAttributes()[0].getStringValue();
        if (mKey.equals(""))
            throw new InvalidParameterException();
    }


}
