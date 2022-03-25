package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.functions.FunctionGetItemDataDesc;

public class FunctionGetItem extends AbstractFunction {
    public FunctionGetItem(FunctionGetItemDataDesc functionGetItemDataDesc, IAGApplication application) {
        super(functionGetItemDataDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        ActionDataDesc actionDesc = mFunctionDataDesc.getActionDescriptor();
        return ActionManager.getInstance().getItem(actionDesc);
    }
}
