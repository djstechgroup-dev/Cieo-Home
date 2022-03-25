package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;

public class FunctionGetControl extends AbstractFunction {

    public FunctionGetControl(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    /**
     * Traverse descriptors hierarchy and descriptor with given id
     *
     * @param desc Descriptor on which action should be called
     * @return found descriptor
     */
    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        VariableDataDesc attr = mFunctionDataDesc.getAttributes()[0];
        String value = attr.getStringValue();

        return ActionManager.getInstance().getControl((AbstractAGElementDataDesc)desc, value);
    }
}
