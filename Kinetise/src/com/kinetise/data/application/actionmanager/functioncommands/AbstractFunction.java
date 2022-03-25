package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.IFunctionCommand;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

import java.io.Serializable;

/**
 * Abstract class for all function that ensures proper arguments in constructor
 */
//Todo: Do not serialize functions
public abstract class AbstractFunction implements IFunctionCommand, Serializable {
    protected AbstractFunctionDataDesc mFunctionDataDesc;
    protected IAGApplication mApplication;

    public AbstractFunction(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        mFunctionDataDesc = functionDesc;
        mApplication = application;
    }

    @Override
    public Object execute(Object desc) {
        mFunctionDataDesc.resolveVariableParameters();
        return null;
    }

    public AbstractFunctionDataDesc getFunctionDataDesc() {
        return mFunctionDataDesc;
    }
}
