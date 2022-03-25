package com.kinetise.data.descriptors;

import java.util.ArrayList;


public class TablesDescriptionDataDesc {

    private ArrayList<TableDescriptionDataDesc> mTableListDescriptionDataDescs;

    public ArrayList<TableDescriptionDataDesc> getTableListDescriptionDataDescs() {
        return mTableListDescriptionDataDescs;
    }

    public void addTableDescription(TableDescriptionDataDesc desc) {
        if (mTableListDescriptionDataDescs == null)
            mTableListDescriptionDataDescs = new ArrayList<>();
        mTableListDescriptionDataDescs.add(desc);

    }
}
