package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionIncreaseTextMultiplier extends AbstractFunction {
    public FunctionIncreaseTextMultiplier(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        float delta;
        try {
            delta = Float.valueOf(mFunctionDataDesc.getAttributes()[0].getStringValue());
        } catch (NumberFormatException e) {
            return null;
        }
        ActionManager.getInstance().increaseTextMultiplier(delta);
        return null;
    }
}
