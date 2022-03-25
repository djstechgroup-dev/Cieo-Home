package com.kinetise.data.application.feedmanager;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedDBManager;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedModification;
import com.kinetise.helpers.preferences.SecurePreferencesHelper;
import com.squareup.duktape.Duktape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFeedDatabase {

    public static final String CREATE = "CREATE";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";

    private static final String PREFERENCE_DATA = "DATA";

    protected static DataFeedDatabase mInstance;

    protected Map<String, DataFeed> mData;
    protected List<DataFeedModification> mModificationsLog;

    public static DataFeedDatabase getInstance() {
        if (mInstance == null) {
            synchronized (DataFeedDatabase.class) {
                if (mInstance == null) {
                    mInstance = new DataFeedDatabase();
                    mInstance.restoreDataFeedsMap();
                    mInstance.restoreModificationsLog();
                }
            }
        }
        return mInstance;
    }

    protected DataFeedDatabase() {
        mData = new HashMap<>();
        mModificationsLog = new ArrayList<>();
    }

    private void restoreDataFeedsMap() {
        synchronized (mData) {
            SharedPreferences preferences = SecurePreferencesHelper.getDataFeedDatabase();

            String json = preferences.getString(PREFERENCE_DATA, null);

            if (json != null) {
                mData = new Gson().fromJson(json, new TypeToken<HashMap<String, DataFeed>>() {
                }.getType());
            }
        }
    }

    private void restoreModificationsLog() {
        synchronized (mModificationsLog) {
            SharedPreferences preferences = SecurePreferencesHelper.getDataFeedDatabaseLog();

            String json = preferences.getString(PREFERENCE_DATA, null);

            if (json != null) {
                mModificationsLog = new Gson().fromJson(json, new TypeToken<List<DataFeedModification>>() {
                }.getType());
            }
        }
    }

    public static void serialize() {
        if (mInstance != null) {
            serializeDB();
            serializeModificationsLog();
        }
    }

    static void serializeDB() {
        synchronized (mInstance.mData) {
            SharedPreferences.Editor editor = SecurePreferencesHelper.getDataFeedDatabase().edit();
            editor.clear();
            editor.putString(PREFERENCE_DATA, new Gson().toJson(mInstance.mData));
            editor.apply();
        }
    }

    static void serializeModificationsLog() {
        synchronized (mInstance.mModificationsLog) {
            SharedPreferences.Editor editor = SecurePreferencesHelper.getDataFeedDatabaseLog().edit();
            editor.clear();
            editor.putString(PREFERENCE_DATA, new Gson().toJson(mInstance.mModificationsLog));
            editor.apply();
        }
    }

    public DataFeed get(String tableName) {
        if (mData.containsKey(tableName)) {
            return mData.get(tableName);
        }
        return null;
    }

    public List<DataFeedModification> getModificationsLog() {
        return mModificationsLog;
    }

    public List<DataFeedModification> getModificationsLogForTable(String tableName) {
        List<DataFeedModification> tableModifications = new ArrayList<>();
        for (DataFeedModification modification : mModificationsLog) {
            if (modification.getTableName().equals(tableName))
                tableModifications.add(modification);
        }
        return tableModifications;
    }

    public void removeModification(DataFeedModification modification) {
        mModificationsLog.remove(modification);
        serializeModificationsLog();
    }

    public boolean isTableExist(String tableName) {
        if (mData.containsKey(tableName))
            return true;
        else return false;
    }

    public void addItem(String tableName, DataFeedItem item) {
        if (!mData.containsKey(tableName)) {
            mData.put(tableName, new DataFeed());
        }
        mData.get(tableName).addItem(item);
        mModificationsLog.add(new DataFeedModification(tableName, CREATE, item.copy()));
    }


    public void updateJS(String tableName, DataFeedItem updateContent, String matcherJS) {
        DataFeed dataFeed = mData.get(tableName);
        if (dataFeed != null) {
            for (DataFeedItem item : dataFeed.getItems()) {
                if (dataFeedItemsMatchByJS(item, updateContent, matcherJS)) {
                    updateItem(item, updateContent);
                    DataFeedItem modifiedItem = updateContent.copy();
                    modifiedItem.setId(item.getId());
                    addIdentifiers(item, modifiedItem);
                    mModificationsLog.add(new DataFeedModification(tableName, UPDATE, modifiedItem));
                }
            }
        }
    }

    private void addIdentifiers(DataFeedItem item, DataFeedItem updateContent) {
        updateContent.putAll(item.getIdentifiers());
    }

    public void overrideFeed(String tableName, DataFeed dataFeed) {
        mData.put(tableName, dataFeed);
    }

    public static boolean dataFeedItemsMatchByKeys(DataFeedItem item, DataFeedItem itemToCompare, ArrayList<String> keys) {
        for (String key : keys) {
            if (!item.containsFieldByKey(key) || !itemToCompare.containsFieldByKey(key)) {
                return false;
            }
            if (!item.getByKey(key).toString().equals(itemToCompare.getByKey(key).toString())) {
                return false;
            }
        }
        return true;
    }


    public static boolean dataFeedItemsMatchByJS(DataFeedItem dataFeedItem, DataFeedItem updateItem, String jsFunctionBodyCode) {
        String dataFeedItemInJS = "dataFeedItem";
        String updateItemItemInJS = "updateItem";

        String actual_attributeName = "item";
        String extraParams_attributeName = "input";

        String functionStart = " function match(" + actual_attributeName + ", " + extraParams_attributeName + "){ \n ";
        String functionCloser = " \n}";
        String functionInvoke = "match(" + dataFeedItemInJS + ", " + updateItemItemInJS + ");";

        String updateItemTable = "var " + dataFeedItemInJS + "={}; \n" + DataFeedDBManager.generateJavaScriptObject(dataFeedItem, dataFeedItemInJS);
        String dataFeedItemTable = "var " + updateItemItemInJS + "={}; \n" + DataFeedDBManager.generateJavaScriptObject(updateItem, updateItemItemInJS);

        Duktape duktape = Duktape.create();
        try {
            String codeToEvaluate = updateItemTable + dataFeedItemTable + functionStart + jsFunctionBodyCode + functionCloser + functionInvoke;

            Object result = duktape.evaluate(
                    codeToEvaluate
            );

            if (result instanceof Boolean) {
                if (result.equals(Boolean.TRUE))
                    return true;
            }

            return false;
        } catch (Exception e) {
            return false;
        } finally {
            duktape.close();
        }
    }


    public void updateItem(DataFeedItem item, DataFeedItem updateContent) {
        for (Map.Entry<String, Object> updateItemEntry : updateContent.getNameValuePairs().entrySet()) {
            item.put(updateItemEntry.getKey(), updateItemEntry.getValue());
        }
    }

    public void deleteItem(String tableName, DataFeedItem dataFeedItem, String matcherJS) {
        DataFeed dataFeed = mData.get(tableName);
        if (dataFeed == null)
            return;
        ArrayList<DataFeedItem> items = dataFeed.getItems();
        int i = 0;
        while (i < items.size()) {
            if (dataFeedItemsMatchByJS(items.get(i), dataFeedItem, matcherJS)) {
                DataFeedItem deletedItem = dataFeed.removeItem(i);
                mModificationsLog.add(new DataFeedModification(tableName, DELETE, deletedItem.copy()));
            } else {
                i++;
            }
        }
    }
}
