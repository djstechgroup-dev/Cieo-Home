package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.sourcemanager.LanguageManager;

public class FunctionGetItemField extends AbstractFunction {

    public FunctionGetItemField(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    /**
     * Gets item data from feed
     *
     * @param desc Descriptor on which action should be called
     * @return null
     */
    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        Object result;

        VariableDataDesc[] attrs = mFunctionDataDesc.getAttributes();
        String fieldId = attrs[0].getStringValue();

        ActionDataDesc actionDesc = mFunctionDataDesc.getActionDescriptor();
        AbstractAGViewDataDesc contextDataDesc = (AbstractAGViewDataDesc) actionDesc.getContextDataDesc();
        int feedItemIndex = contextDataDesc.getFeedItemIndex();

        if (!(desc instanceof IFeedClient)) {
            return "";
        }

        DataFeed feedDesc = ((IFeedClient) desc).getFeedDescriptor();

        if (feedItemIndex < feedDesc.getItemsCount()) {

            DataFeedItem item = feedDesc.getItem(feedItemIndex);
            if (item != null) {
                if (item.getByKey(fieldId) == null) {
                    return "";
                } else {
                    result = item.getByKey(fieldId);
                }
            } else
                result = LanguageManager.getInstance().

                        getString(LanguageManager.NODE_NOT_FOUND);


        } else {
            throw new IllegalArgumentException(
                    "There is less feed elements that active item index");
        }

        return result.toString();
    }

}
