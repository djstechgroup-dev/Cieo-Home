package com.kinetise.data.application.actionmanager.functioncommands;


import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionPlaySound extends AbstractFunction {

    public FunctionPlaySound(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        float volume;
        boolean loop;
        volume = Float.parseFloat(mFunctionDataDesc.getAttributes()[1].getStringValue());
        String loopValue = mFunctionDataDesc.getAttributes()[2].getStringValue();
        final String soundSource = mFunctionDataDesc.getAttributes()[0].getStringValue();
        if (loopValue.equals("yes")) {
            loop = true;
        } else {
            loop = false;
        }
        ActionManager.getInstance().playSound(soundSource, volume, loop);
        return null;
    }
}
