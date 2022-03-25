package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

import java.security.InvalidParameterException;

public class FunctionSetValue extends AbstractFunction {
    public FunctionSetValue(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String value = mFunctionDataDesc.getAttributes()[0].getStringValue();
        if(!(desc instanceof IFormControlDesc))
            throw new InvalidParameterException("Expected Form Control");
        ((IFormControlDesc) desc).setFormValue(value);

        return null;
    }
}
