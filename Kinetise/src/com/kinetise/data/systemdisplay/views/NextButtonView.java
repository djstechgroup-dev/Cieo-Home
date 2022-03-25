package com.kinetise.data.systemdisplay.views;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.descriptors.datadescriptors.AGButtonDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGDataFeedDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.AGItemTemplateDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.helpers.AGViewHelper;

public class NextButtonView extends AGButtonView {
    public NextButtonView(SystemDisplay display, AGButtonDataDesc desc) {
        super(display, desc);
        if(!canShowNextElement()){
            AGViewHelper.setHalftransparentIncludingChildren(this);
            setEnabled(false);
        }
    }



    public boolean canShowNextElement() {
        AbstractAGDataFeedDataDesc context = (AbstractAGDataFeedDataDesc) AGApplicationState.getInstance().getApplicationState().getContext();
        if (context == null) {
            return false;
        }
        DataFeed dataFeed = context.getFeedDescriptor();
        if(dataFeed == null){
            return false;
        }
        int activeIndex = context.getActiveItemIndex();
        int nextIndex = activeIndex + 1;
        int itemsCount = dataFeed.getItemsCount();

        for (; hasMoreElements(context, itemsCount, nextIndex); ++nextIndex) {
            DataFeedItem item = dataFeed.getItem(nextIndex);
            AGItemTemplateDataDesc matchingTempleteDataDesc = context.getMatchingTemplete(item);

            if (matchingTempleteDataDesc != null) {
                String detailScreenId = matchingTempleteDataDesc.getDetailScreenId();

                if (detailScreenId != null && !detailScreenId.equals("")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasMoreElements(AbstractAGDataFeedDataDesc context, int itemsCount, int nextIndex) {
        return (nextIndex < itemsCount) && ((nextIndex <= context.getLastItemIndex()) || (context.getLoadMoreTemplate() != null));
    }

}
