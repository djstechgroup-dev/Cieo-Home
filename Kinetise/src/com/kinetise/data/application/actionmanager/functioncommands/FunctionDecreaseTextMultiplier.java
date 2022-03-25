package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionDecreaseTextMultiplier extends AbstractFunction{
    public FunctionDecreaseTextMultiplier(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        float delta;
        try {
            delta = Float.valueOf(mFunctionDataDesc.getAttributes()[0].getStringValue());
        } catch (NumberFormatException e){
            return null;
        }
        ActionManager.getInstance().decreaseTextMultiplier(delta);
        return null;
    }


}
