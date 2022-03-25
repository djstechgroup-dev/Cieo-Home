package com.kinetise.data.descriptors;

import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;

public class DataFeedContext {
    private int mFeedItemIndex = 0;
    private int mTemplateNumber = 0;
    DataFeed mFeedDescriptor;
    private String mFeedBaseAdress;

    public DataFeedContext(){}

    public DataFeedContext(String feedBaseAdress, DataFeed feedDescriptor, int itemIndex, int templateNumber) {
        mFeedDescriptor = feedDescriptor;
        mFeedItemIndex = itemIndex;
        mTemplateNumber = templateNumber;
        mFeedBaseAdress = feedBaseAdress;
    }

    public boolean isInDataFeed(){
        return mFeedDescriptor!=null;
    }

    public int getFeedItemIndex() {
        return mFeedItemIndex;
    }

    public int getTemplateNumber() {
        return mTemplateNumber;
    }

    public String getAlterApiContext() {
        if (isInDataFeed())
            return getItem().getAlterApiContext();
        else
            return null;
    }

    private DataFeedItem getItem() {
        return mFeedDescriptor.getItem(mFeedItemIndex);
    }

    public long getFeedTimestamp(){
        return mFeedDescriptor.getTimestamp();
    }

    public void setFeedItemIndex(int index) {
        mFeedItemIndex = index;
    }

    public DataFeedContext copy() {
        return new DataFeedContext(mFeedBaseAdress, mFeedDescriptor, mFeedItemIndex, mTemplateNumber);
    }

    public String getGUID() {
        if (isInDataFeed())
            return getItem().getGUID();
        else
            return null;
    }

    public String getFeedBaseAdress() {
        return mFeedBaseAdress;
    }
}
