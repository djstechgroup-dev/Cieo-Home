package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.actions.functions.FunctionOrDataDesc;

public class FunctionOr extends AbstractFunction {
    public FunctionOr(FunctionOrDataDesc functionOrDataDesc, AGApplicationState instance) {
        super(functionOrDataDesc, instance);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String firstAttribute = mFunctionDataDesc.getAttributes()[0].getStringValue();
        String secondAttribute = mFunctionDataDesc.getAttributes()[1].getStringValue();

        if (firstAttribute.equals(StringLogicValues.TRUE) || secondAttribute.equals(StringLogicValues.TRUE))
            return StringLogicValues.TRUE;
        else
            return StringLogicValues.FALSE;
    }
}
