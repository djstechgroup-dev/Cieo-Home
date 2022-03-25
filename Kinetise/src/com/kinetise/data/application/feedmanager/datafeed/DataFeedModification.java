package com.kinetise.data.application.feedmanager.datafeed;

public class DataFeedModification {

    private String modificationType;
    private String tableName;
    private DataFeedItem dataFeedItem;

    public DataFeedModification(String tableName, String type, DataFeedItem item) {
        modificationType = type;
        dataFeedItem = item;
        this.tableName = tableName;
    }

    public String getModificationType() {
        return modificationType;
    }

    public void setModificationType(String modificationType) {
        this.modificationType = modificationType;
    }

    public DataFeedItem getDataFeedItem() {
        return dataFeedItem;
    }

    public void setDataFeedItem(DataFeedItem dataFeedItem) {
        this.dataFeedItem = dataFeedItem;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
