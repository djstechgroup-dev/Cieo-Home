package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.functions.FunctionGetLocalizationDataDesc;

public class FunctionGetLocalization extends AbstractFunction {
    public FunctionGetLocalization(FunctionGetLocalizationDataDesc functionGetLocalizationDataDesc, AGApplicationState agApplicationState) {
        super(functionGetLocalizationDataDesc, agApplicationState);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        return ActionManager.getInstance().getLocalization();
    }
}
