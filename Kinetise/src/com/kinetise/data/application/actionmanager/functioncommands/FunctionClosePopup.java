package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionClosePopup extends AbstractFunction {

    public FunctionClosePopup(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    /**
     * Calls to close currently visible popup
     *
     * @param desc Descriptor on which action should be called
     * @return desc from param
     */
    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        ActionManager.getInstance().closePopup();
        return desc;
    }

}
