package com.kinetise.data.descriptors;

import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.ItemPath;


public class TableIdentifierDataDesc {

    private ItemPath mFieldPath;
    private ItemPath mCreateResponsePath;

    public ItemPath getFieldPath() {
        return mFieldPath;
    }

    public void setFieldPath(ItemPath fieldPath) {
        mFieldPath = fieldPath;
    }

    public ItemPath getCreateResponsePath() {
        return mCreateResponsePath;
    }

    public void setCreateResponsePath(ItemPath createResponsePath) {
        mCreateResponsePath = createResponsePath;
    }

}
