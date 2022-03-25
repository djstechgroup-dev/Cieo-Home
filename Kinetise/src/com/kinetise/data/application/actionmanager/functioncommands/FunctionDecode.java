package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

/**
 * Created by Kuba Komorowski on 2014-12-18.
 */
public class FunctionDecode extends AbstractFunction {

    public FunctionDecode(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String encodingType = mFunctionDataDesc.getAttributes()[0].getStringValue();
        String stringToDecode = mFunctionDataDesc.getAttributes()[1].getStringValue();
        return ActionManager.getInstance().decode(encodingType, stringToDecode);
    }
}
