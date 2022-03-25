package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;

public class ItemPath {

    private String mXPath;

    public String getXPath() {
        return mXPath;
    }

    public void setAGXpath(String xpath) {
        mXPath = xpath;
    }

    public ItemPath copy() {
        ItemPath copied = new ItemPath();
        copied.setAGXpath(mXPath);
        return copied;
    }
}
