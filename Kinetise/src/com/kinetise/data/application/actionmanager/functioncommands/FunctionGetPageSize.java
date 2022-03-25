package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.functions.FunctionGetPageSizeDataDesc;

public class FunctionGetPageSize extends AbstractFunction {
    public FunctionGetPageSize(FunctionGetPageSizeDataDesc functionGetPageSizeDataDesc, IAGApplication application) {
        super(functionGetPageSizeDataDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        return ActionManager.getInstance().getPageSize(desc);
    }
}
