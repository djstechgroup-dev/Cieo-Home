package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

import java.security.InvalidParameterException;

public class FunctionGetValue extends AbstractFunction{
    public FunctionGetValue(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        if(!(desc instanceof IFormControlDesc))
            throw new InvalidParameterException("Expected Form Control");

        return ((IFormControlDesc)desc).getFormValue();
    }
}
