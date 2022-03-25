package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.FunctionNotDataDesc;

public class FunctionNot extends AbstractFunction {
    public FunctionNot(FunctionNotDataDesc functionNotDataDesc, AGApplicationState instance) {
        super(functionNotDataDesc, instance);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String attribute = mFunctionDataDesc.getAttributes()[0].getStringValue();
        if(attribute.equals(StringLogicValues.TRUE))
            return StringLogicValues.FALSE;
        else
            return StringLogicValues.TRUE;
    }
}
