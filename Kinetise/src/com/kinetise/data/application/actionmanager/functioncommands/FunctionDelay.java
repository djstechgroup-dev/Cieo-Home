package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;

public class FunctionDelay extends AbstractFunction {

    public FunctionDelay(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    /**
     * Execute action after specified period of time in ms
     *
     * @param desc Descriptor on which action should be called
     * @return null
     */
    @Override
    public Object execute(Object desc) {
        super.execute(desc);

        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();

        if (attributes.length != 2) {
            throw new IllegalArgumentException("Delay function should have only two parameters!");
        }

        String actionString = attributes[0].getStringValue();
        final long delay = Long.parseLong(attributes[1].getStringValue());
        AbstractAGElementDataDesc contextDataDesc = mFunctionDataDesc.getContextDataDesc();
        ActionManager.getInstance().delay(actionString, delay, contextDataDesc);

        return null;
    }

}
