package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.types.FormString;

public class AGTextAreaDataDesc extends AGTextInputDataDesc<FormString> {
    private int mRows;

    public AGTextAreaDataDesc(String id) {
        super(id);
    }

    public int getRows() {
        return mRows;
    }

    public void setRows(int mRows) {
        this.mRows = mRows;
    }

    @Override
    public AGTextAreaDataDesc copy() {
        AGTextAreaDataDesc desc = (AGTextAreaDataDesc) super.copy();
        desc.setRows(mRows);

        return desc;

    }

    @Override
    public AGTextAreaDataDesc createInstance() {
        return new AGTextAreaDataDesc(getId());
    }
}
