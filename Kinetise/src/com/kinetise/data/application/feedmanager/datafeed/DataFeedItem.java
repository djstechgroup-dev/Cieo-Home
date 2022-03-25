package com.kinetise.data.application.feedmanager.datafeed;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DataFeedItem implements Serializable, DataFeedMatch {


    /* mNameValuePairs does not contain entries for which value was not found */
    private HashMap<String, Object> mNameValuePairs = new HashMap<>();
    private HashMap<String, Object> mIdentifiers = new HashMap<>();
    private String mAlterApiContext;
    private String mTargetType;
    private String mNodeNotFoundMessage;
    private String mGUID;
    private String mId;

    public DataFeedItem(String nodeNotFoundMessage) {
        mNodeNotFoundMessage = nodeNotFoundMessage;
        mId = UUID.randomUUID().toString();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Object getByKey(String key) {
        if (mNameValuePairs.containsKey(key)) {
            // if value under key was null (in case of JSON) we return NullItem object NULL
            return mNameValuePairs.get(key);
        } else {
            return mNodeNotFoundMessage;
        }
    }

    public boolean hasField(String key) {
        return mNameValuePairs.containsKey(key);
    }

    public void put(String key, Object value) {
        mNameValuePairs.put(key, value);
    }

    public void putAll(Map<String, Object> entries) {
        mNameValuePairs.putAll(entries);
    }

    public void putIfKeyDoesntExist(String key, Object value) {
        if (!mNameValuePairs.containsKey(key))
            mNameValuePairs.put(key, value);
    }

    public void putIdentifier(String key, Object value) {
        mIdentifiers.put(key, value);
    }

    public boolean containsFieldByKey(String key) {
        return mNameValuePairs.containsKey(key);
    }

    public HashMap<String, Object> getNameValuePairs() {
        return mNameValuePairs;
    }

    public HashMap<String, Object> getIdentifiers() {
        return mIdentifiers;
    }

    public DataFeedItem copy() {
        DataFeedItem copied = new DataFeedItem(mNodeNotFoundMessage);
        Set<String> hashMapKeys = mNameValuePairs.keySet();
        for (String s : hashMapKeys) {
            copied.put(String.valueOf(s), String.valueOf(this.getByKey(s)));
        }
        Set<String> identifiersMapKeys = mIdentifiers.keySet();
        for (String s : identifiersMapKeys) {
            copied.putIdentifier(String.valueOf(s), String.valueOf(this.getByKey(s)));
        }
        copied.setId(mId);
        return copied;
    }

    public String getAlterApiContext() {
        return mAlterApiContext;
    }

    public void setAlterApiContext(String alterApiContext) {
        mAlterApiContext = alterApiContext;
    }

    public String getTargetType() {
        return mTargetType;
    }

    public void setTargetType(String targetType) {
        mTargetType = targetType;
    }

    // Representation of null item value. Only single instance ever exists.

    public static final NullItem NULL = new NullItem();

    public void setGUID(String guidNodePath) {
        mGUID = mNameValuePairs.containsKey(guidNodePath) ?
                mNameValuePairs.get(guidNodePath).toString()
                : null;
    }

    public String getGUID() {
        return mGUID;
    }

    @Override
    public String getValue(String key) {
        return getByKey(key).toString();
    }

    public static class NullItem {

        private NullItem() {
        }

        @Override
        public String toString() {
            return "";
        }

        @Override
        public boolean equals(Object o) {
            return (o != null && o instanceof NullItem);
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DataFeedItem))
            return false;

        DataFeedItem oldItem = (DataFeedItem) o;

        if (mTargetType != null && oldItem.getTargetType() != null && !mTargetType.equals(oldItem.getTargetType()))
            return false;
        if (mAlterApiContext != null && oldItem.getAlterApiContext() != null && !mAlterApiContext.equals(oldItem.getAlterApiContext()))
            return false;
        if (mNameValuePairs != null && oldItem.getNameValuePairs() != null && !mNameValuePairs.equals(oldItem.getNameValuePairs()))
            return false;

        return true;
    }

    public boolean equalsById(Object o) {
        if (!(o instanceof DataFeedItem))
            return false;

        DataFeedItem oldItem = (DataFeedItem) o;

        if (!mId.equals(oldItem.getId())) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("PointlessBooleanExpression")
    public int compareByKey(DataFeedItem second, String key) {
        if (containsFieldByKey(key) && second.containsFieldByKey(key)) {
            return getByKey(key).toString().
                    compareTo(second.getByKey(key).toString());
        }
        if (containsFieldByKey(key) == false && second.containsFieldByKey(key) == true) {
            return 1;
        }

        if (containsFieldByKey(key) == true && second.containsFieldByKey(key) == false) {
            return -1;
        }
        return 0;
    }

}
