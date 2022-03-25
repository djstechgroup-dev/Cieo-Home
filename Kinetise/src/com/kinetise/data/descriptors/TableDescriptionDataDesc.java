package com.kinetise.data.descriptors;

public class TableDescriptionDataDesc {

    private SynchronizationDescriptionDataDesc mSynchronizationDescriptionDataDesc;
    private InitDescriptionDataDesc mInitDescriptionDataDesc;
    private String mTableName;
    private TableIdentifiersDataDesc mTableIdentifiersDataDesc;

    public String getTableName() {
        return mTableName;
    }

    public void setTableName(String tableName) {
        this.mTableName = tableName;
    }

    public SynchronizationDescriptionDataDesc getSynchronizationDescriptionDataDesc() {
        return mSynchronizationDescriptionDataDesc;
    }

    public void setSynchronizationDescriptionDataDesc(SynchronizationDescriptionDataDesc synchronizationDescriptionDataDesc) {
        this.mSynchronizationDescriptionDataDesc = synchronizationDescriptionDataDesc;
    }

    public InitDescriptionDataDesc getInitDescriptionDataDesc() {
        return mInitDescriptionDataDesc;
    }

    public void setInitDescriptionDataDesc(InitDescriptionDataDesc initDescriptionDataDesc) {
        this.mInitDescriptionDataDesc = initDescriptionDataDesc;
    }

    public TableIdentifiersDataDesc getTableIdentifiersDataDesc() {
        return mTableIdentifiersDataDesc;
    }

    public void setTableIdentifiersDataDesc(TableIdentifiersDataDesc tableIdentifiersDataDesc) {
        mTableIdentifiersDataDesc = tableIdentifiersDataDesc;
    }

}
