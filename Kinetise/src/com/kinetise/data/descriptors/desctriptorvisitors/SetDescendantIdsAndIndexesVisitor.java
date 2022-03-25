package com.kinetise.data.descriptors.desctriptorvisitors;

import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.descriptors.*;
import com.kinetise.helpers.DescriptorCompiler.GUID;

/**
 * This descriptor traverses descriptors tree and sets ids for all feed items controls
 */
public class SetDescendantIdsAndIndexesVisitor implements IDataDescVisitor {
    private final IAGCollectionDataDesc mSection;
    private DataFeedContext mDataFeedContext;
    private DataFeed feedDescriptor = null;

    public SetDescendantIdsAndIndexesVisitor(IFeedClient feedControlDataDesc){
        mSection = ((AbstractAGViewDataDesc) feedControlDataDesc).getSection();
        feedDescriptor = feedControlDataDesc.getFeedDescriptor();
    }

    public void setItemData(String feedBaseAdress, int itemIndex,int templateNumber){
         mDataFeedContext = new DataFeedContext(feedBaseAdress, feedDescriptor, itemIndex, templateNumber);
    }

    @Override
    public boolean visit(AbstractAGElementDataDesc elemDesc) {
        if ( !(elemDesc instanceof AbstractAGViewDataDesc) ) {
            return false;
        }

        AbstractAGViewDataDesc desc = (AbstractAGViewDataDesc) elemDesc;
        desc.setSection(mSection);
        desc.setDataFeedContext(mDataFeedContext);
        if((desc.getId()==null)||desc.getId()=="")
            desc.setId(GUID.get());

        return false;
    }
}
