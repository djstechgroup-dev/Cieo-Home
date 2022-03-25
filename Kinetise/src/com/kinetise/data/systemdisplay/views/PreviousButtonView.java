package com.kinetise.data.systemdisplay.views;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.descriptors.datadescriptors.AGButtonDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGDataFeedDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.helpers.AGViewHelper;

public class PreviousButtonView extends AGButtonView {

    public PreviousButtonView(SystemDisplay display, AGButtonDataDesc desc) {
        super(display, desc);
        if(!canShowPreviousElement()){
            AGViewHelper.setHalftransparentIncludingChildren(this);
            setEnabled(false);
        }

    }

    public boolean canShowPreviousElement() {
        AbstractAGDataFeedDataDesc context = (AbstractAGDataFeedDataDesc) AGApplicationState.getInstance().getApplicationState().getContext();

        DataFeed dataFeed = context.getFeedDescriptor();

        int previousIndex = context.getActiveItemIndex() - 1;

        for (; previousIndex >= 0; --previousIndex) {
            DataFeedItem item = dataFeed.getItem(previousIndex);
            String detailScreenId = context.getMatchingTemplete(item).getDetailScreenId();

            if(detailScreenId != null && !detailScreenId.equals("")) {
                return true;
            }
        }

        return false;
    }

}
