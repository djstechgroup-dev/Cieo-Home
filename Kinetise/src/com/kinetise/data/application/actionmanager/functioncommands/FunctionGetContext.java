package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGetContext extends AbstractFunction {

    public FunctionGetContext(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    /**
     * Gets appContext (Feed) from last application state
     *
     * @param desc Descriptor on which action should be called
     * @return IFeedClient descriptor
     */
    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        return ActionManager.getInstance().getContext();
    }

}
