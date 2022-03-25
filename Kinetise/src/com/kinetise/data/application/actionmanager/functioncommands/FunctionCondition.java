package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

public class FunctionCondition extends AbstractFunction {

    public FunctionCondition(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    private VariableDataDesc condition;
    private VariableDataDesc valueForTrue;
    private VariableDataDesc valueForFalse;

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        readParameters();
        if(Boolean.valueOf(condition.getStringValue())){
            valueForTrue.resolveVariable();
            return valueForTrue.getValue();
        }
        else{
            valueForFalse.resolveVariable();
            return valueForFalse.getValue();
        }
    }

    private void readParameters(){
        AbstractAGElementDataDesc actionParent = mFunctionDataDesc.getContextDataDesc();
        condition = mFunctionDataDesc.getAttributes()[0];
        String stringForTrue = mFunctionDataDesc.getAttributes()[1].getStringValue();
        String stringForFalse = mFunctionDataDesc.getAttributes()[2].getStringValue();
        valueForTrue = AGXmlActionParser.createVariable(stringForTrue,actionParent);
        valueForFalse = AGXmlActionParser.createVariable(stringForFalse,actionParent);
    }
}
