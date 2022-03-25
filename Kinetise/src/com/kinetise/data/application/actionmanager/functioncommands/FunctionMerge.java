package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;


/**
 * Created by Kuba on 21.05.2014.
 *
 * Class describing function used for merging strings together.
 */
public class FunctionMerge extends AbstractFunction {

    public FunctionMerge(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc,application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);

        StringBuffer sb = new StringBuffer();
        for(VariableDataDesc var : mFunctionDataDesc.getAttributes()){
            sb.append(var.getStringValue());
        }
        return sb.toString();
    }
}
