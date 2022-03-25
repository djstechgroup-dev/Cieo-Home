package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionRegex extends AbstractFunction {

    public FunctionRegex(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String text = mFunctionDataDesc.getAttributes()[0].getStringValue();
        String patternName = mFunctionDataDesc.getAttributes()[1].getStringValue();
        return ActionManager.getInstance().regex(text, patternName);
    }

}
