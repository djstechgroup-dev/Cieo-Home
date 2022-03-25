package com.kinetise.data.sourcemanager.propertymanager;

public class Property {

    String mValue;

    long mTimestamp;

    public Property(String value, long timestamp) {
        this.mValue = value;
        this.mTimestamp = timestamp;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String mValue) {
        this.mValue = mValue;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long mTimestamp) {
        this.mTimestamp = mTimestamp;
    }
}
