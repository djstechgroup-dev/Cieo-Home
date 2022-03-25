package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public abstract class AbstractCompareFunction extends AbstractFunction {
    public AbstractCompareFunction(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    public int compare(){
        String value1 = mFunctionDataDesc.getAttributes()[0].getStringValue();
        String value2 = mFunctionDataDesc.getAttributes()[1].getStringValue();

        return value1.compareTo(value2);
    }
}
