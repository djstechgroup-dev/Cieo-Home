package com.kinetise.data.application.feedmanager.datafeed;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataFeed implements Serializable {
    private ArrayList<DataFeedItem> mItemsList = new ArrayList<DataFeedItem>();
    private long mTimestamp = 0;
    private String mNextPageAddress;

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void addItem(DataFeedItem item) {
        mItemsList.add(item);
    }

    public DataFeedItem getItem(int index) {
        return mItemsList.get(index);
    }

    public DataFeedItem getLastItem() {
        return mItemsList.get(mItemsList.size() - 1);
    }

    public ArrayList<DataFeedItem> getItems() {
        return mItemsList;
    }

    public int getItemsCount() {
        return mItemsList.size();
    }

    public DataFeedItem removeItem(int i) {
        return mItemsList.remove(i);
    }

    public String getNextPageAddress() {
        return mNextPageAddress;
    }

    public boolean isEmpty(){
        return getItemsCount() == 0;
    }

    /**
     * Can be both url address (NextPageUrl) or just token (NextPageToken) so here we cannot use RegexHelper to parse url
     *
     * @param nextPageAddress url or token
     */
    public void setNextPageAddress(String nextPageAddress) {
        if (nextPageAddress != null)
            nextPageAddress = nextPageAddress.trim();
        mNextPageAddress = nextPageAddress;
    }

    public void trimValues() {
        HashMap<String, Object> nameValuePairs;
        for (DataFeedItem feedItem : mItemsList) {
            nameValuePairs = feedItem.getNameValuePairs();
            for (Map.Entry<String, Object> entry : nameValuePairs.entrySet())
                if (entry.getValue() instanceof String)
                    nameValuePairs.put(entry.getKey(), ((String) entry.getValue()).trim());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DataFeed))
            return false;
        DataFeed oldDataFeed = (DataFeed) o;

        if (oldDataFeed.getItemsCount() != getItemsCount())
            return false;
        for (int i = 0; i < getItemsCount(); i++) {
            DataFeedItem newItem = getItem(i);
            DataFeedItem oldItem = oldDataFeed.getItem(i);
            if (!newItem.equals(oldItem))
                return false;
        }
        return true;
    }

    public void setGUID(String GUIDNodePath) {
        for (DataFeedItem item : mItemsList) {
            item.setGUID(GUIDNodePath);
        }
    }
}
