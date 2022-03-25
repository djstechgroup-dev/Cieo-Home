package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;

import java.security.InvalidParameterException;

public class FunctionReload extends AbstractFunction {

    public FunctionReload(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    /**
     * Reloads current screen.
     * @param desc Descriptor on which action should be called
     * @return null
     * */
    @Override
    public Object execute(Object desc) {
        super.execute(desc);

        if (desc != null) {
            throw new InvalidParameterException(
                    "FunctionRefresh function is not execute on descriptor! The descriptor should be null!");
        }

        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();

        if (attributes.length != 0) {
            throw new InvalidParameterException(
                    "FunctionRefresh function should have no parameters");
        }

        ActionManager.getInstance().reload();
        return null;
    }
}
