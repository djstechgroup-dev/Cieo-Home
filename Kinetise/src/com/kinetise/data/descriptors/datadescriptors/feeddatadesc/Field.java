package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;

public class Field {

    private String mXpath;
    private String mId;

    public Field(String id, String xpath) {
        mId = id;
        mXpath = xpath;
    }

    public Field(String id) {
        mId = id;
    }

    public String getXpath() {
        return mXpath;
    }

    public String getId() {
        return mId;
    }

    public void setAGXpath(String xpath) {
        mXpath = xpath;
    }

    public Field copy() {
        return new Field(mId, mXpath);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Field))
            return false;
        Field compared = (Field) o;
        if (mId.equals(compared.mId) && mXpath.equals(compared.mXpath))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return mId.hashCode() + mXpath.hashCode();
    }
}
