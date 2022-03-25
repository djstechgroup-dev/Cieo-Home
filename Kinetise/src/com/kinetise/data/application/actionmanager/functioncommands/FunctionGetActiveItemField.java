package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.support.logger.Logger;

public class FunctionGetActiveItemField extends AbstractFunction {

    public FunctionGetActiveItemField(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc,application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        VariableDataDesc[] attrs = mFunctionDataDesc.getAttributes();
        String fieldId = attrs[0].getStringValue();
        return ActionManager.getInstance().getActiveItemField(desc, fieldId);
    }

    @Override
    public String toString() {
        return "FunctionGetActiveField";
    }
}
