package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionSetLocalValue extends AbstractFunction {
    public FunctionSetLocalValue(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String key = mFunctionDataDesc.getAttributes()[0].getStringValue();
        String value = mFunctionDataDesc.getAttributes()[1].getStringValue();
        return ActionManager.getInstance().setLocalValue(key, value);
    }

}
