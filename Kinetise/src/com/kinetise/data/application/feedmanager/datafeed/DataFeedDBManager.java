package com.kinetise.data.application.feedmanager.datafeed;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;

import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.data.application.actionmanager.JSEvaluator;
import com.kinetise.data.application.actionmanager.JSEvaluatorFactory;
import com.kinetise.data.application.feedmanager.DataFeedDatabase;
import com.kinetise.data.descriptors.LocalStorageDescriptionDataDesc;
import com.kinetise.data.descriptors.TableDescriptionDataDesc;
import com.kinetise.data.descriptors.TablesDescriptionDataDesc;
import com.kinetise.data.descriptors.actions.functions.FunctionGetCurrentTimeDataDesc;
import com.kinetise.data.descriptors.actions.functions.FunctionGetGuidDataDesc;
import com.kinetise.data.packagemanager.AppPackageManager;
import com.kinetise.data.sourcemanager.AssetsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static com.kinetise.helpers.BitmapHelper.IMAGE_DIR;

public class DataFeedDBManager {

    public static final String DATA = "data";
    private static final String _UPDATED_TS = "_updated_ts";
    private static final String _CREATED_TS = "_created_ts";
    private static final String _ID = "_id";
    public static final String METADATA = "metadata";
    public static final String TYPE = "type";
    public static final String LOCAL_URI = "localUri";
    public static final String TEXT = "text";
    public static final String IMAGE = "image";
    private static final String BOLEAN = "boolean";

    public static DataFeed get(String uriString, DataFeedItem item) {
        Uri uri = Uri.parse(uriString);
        String tableName = uri.getHost();

        String filterString = uri.getQueryParameter("filter");

        String sortExpression = uri.getQueryParameter("sort");

        String pageSize = uri.getQueryParameter("pageSize");

        int maxItems = Integer.MAX_VALUE;
        try {
            maxItems = Integer.parseInt(pageSize);
        } catch (Exception e) {
        }

        DataFeed dataFeed = DataFeedDatabase.getInstance().get(tableName);
        if (filterString != null) {
            dataFeed = filterDataFeedByJS(dataFeed, item, filterString);
        } else {
            dataFeed = copyFeed(dataFeed);
        }
        if (sortExpression != null) {
            dataFeed = sort(dataFeed, sortExpression);
        }
        if (maxItems != Integer.MAX_VALUE) {
            truncate(dataFeed, maxItems);
        }
        return dataFeed;
    }

    private static void truncate(DataFeed dataFeed, int maxItems) {
        if (maxItems < 0) {
            return;
        }
        ArrayList<DataFeedItem> items = dataFeed.getItems();
        while (items.size() > maxItems) {
            items.remove(items.size() - 1);
        }
    }

    public static DataFeed filterDataFeedByJS(DataFeed dataFeed, DataFeedItem compare, String jsFilter) {
        DataFeed result = new DataFeed();
        for (DataFeedItem item : dataFeed.getItems()) {
            if (DataFeedDatabase.dataFeedItemsMatchByJS(item, compare, jsFilter)) {
                result.addItem(item);
            }
        }
        return result;
    }


    public static boolean matchByJS(DataFeedItem item, String jsFunctionBodyCode) {
        JSEvaluator evaluator = JSEvaluatorFactory.getInstance().getEvaluator();
        String dataFeedItemInJS = "itemToMatch";
        String jsItemInitialization = "var " + dataFeedItemInJS + "={}; \n" + generateJavaScriptObject(item, dataFeedItemInJS);
        evaluator.appendCode(jsItemInitialization);
        try {
            Object result = evaluator.evaluate(jsFunctionBodyCode, new String[]{"item"}, new String[]{dataFeedItemInJS},null);

            if (result instanceof Boolean) {
                if (result.equals(Boolean.TRUE))
                    return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        } finally {
            evaluator.close();
        }
    }


    public static String generateJavaScriptObject(DataFeedItem feedItem, String jsObjectName) {
        String result;
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> object : feedItem.getNameValuePairs().entrySet()) {
            if (object.getValue() instanceof Boolean) {
                sb.append(jsObjectName + "[\"" + object.getKey() + "\"]=" + object.getValue() + ";\n");
            } else if (object.getValue() instanceof String) {
                sb.append(jsObjectName + "[\"" + object.getKey() + "\"]=\"" + object.getValue() + "\";\n");
            }
        }
        result = sb.toString();
        return result;
    }


    public static DataFeed copyFeed(DataFeed source) {
        DataFeed result = new DataFeed();
        for (DataFeedItem item : source.getItems()) {
            result.addItem(item);
        }
        return result;
    }

    public static DataFeed sort(DataFeed source, final String sortQuery) {
        if (sortQuery.equals(""))
            return source;
        boolean descentOrder = false;
        final String sortField;
        char orderChar = sortQuery.charAt(0);
        if (orderChar == '-') {
            descentOrder = true;
            sortField = sortQuery.replaceFirst("-", "");
        } else {
            sortField = sortQuery;
        }

        final boolean finalDescentOrder = descentOrder;
        Collections.sort(source.getItems(), new Comparator<DataFeedItem>() {
            @Override
            public int compare(DataFeedItem item1, DataFeedItem item2) {
                int order = item1.compareByKey(item2, sortField);
                if (finalDescentOrder) {
                    order *= -1;
                }
                return order;
            }
        });

        return source;
    }

    public static void insertItemIntoTable(String tableName, DataFeedItem item) {
        DataFeedDatabase.getInstance().addItem(tableName, item);
    }

    public static void updateItemInTable(String tableName, DataFeedItem item, String matcherJS) {
        DataFeedDatabase.getInstance().updateJS(tableName, item, matcherJS);
    }


    public static void delete(String tableName, DataFeedItem dataFeedItemFromForm, String matcherJS) {
        DataFeedDatabase.getInstance().deleteItem(tableName, dataFeedItemFromForm, matcherJS);
    }


    private static void initLocalDB(String jsonFile, String tableName) {
        String fileName;
        boolean isTableExist = DataFeedDatabase.getInstance().isTableExist(tableName);
        fileName = jsonFile.replace("assets://", "");

        String jsonAsString = AppPackageManager.getInstance().getPackage().getStringFromFile(fileName);

        if (jsonAsString == null || jsonAsString == "" || isTableExist || tableName == "")
            return;

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonAsString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonArray != null) ;
        try {
            for (int i = 0; i < jsonArray.length(); i++) {

                DataFeedItem dataFeedItem = new DataFeedItem("");
                Iterator<String> iterator = jsonArray.getJSONObject(i).keys();

                FunctionGetCurrentTimeDataDesc ct = new FunctionGetCurrentTimeDataDesc(null);
                FunctionGetGuidDataDesc guidDataDesc = new FunctionGetGuidDataDesc(null);

                String currrentTime = (String) ct.getFunction().execute(null);
                UUID uuid = (UUID) guidDataDesc.getFunction().execute(null);

                String uiidAsString = uuid.toString();
                dataFeedItem.put(_UPDATED_TS, currrentTime);
                dataFeedItem.put(_CREATED_TS, currrentTime);
                dataFeedItem.put(_ID, uiidAsString);

                while (iterator.hasNext()) {
                    String key = iterator.next();
                    JSONObject jsonValue = (JSONObject) jsonArray.getJSONObject(i).get(key);
                    String value = null;
                    String type = (String) jsonValue.getJSONObject(METADATA).get(TYPE);
                    if (type.equals(TEXT)) {
                        putStringIntoFeedItem(dataFeedItem, key, jsonValue);
                    } else if (type.equals(IMAGE)) {
                        putImageIntoFeedItem(dataFeedItem, key, jsonValue);
                    } else if (type.equals(BOLEAN)) {
                        putBooleanIntoFeedItem(dataFeedItem, key, jsonValue);
                    }
                }

                if (dataFeedItem != null) {
                    DataFeedDatabase.getInstance().addItem(tableName, dataFeedItem);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void putBooleanIntoFeedItem(DataFeedItem dataFeedItem, String key, JSONObject jsonValue) throws JSONException {
        Boolean booleanValue = (Boolean) jsonValue.get(DATA);
        dataFeedItem.put(key, booleanValue);
    }

    private static void putStringIntoFeedItem(DataFeedItem dataFeedItem, String key, JSONObject jsonValue) throws JSONException {
        String value;
        value = (String) jsonValue.get(DATA);
        if (value != null)
            dataFeedItem.put(key, value);
    }

    private static void putImageIntoFeedItem(DataFeedItem dataFeedItem, String key, JSONObject jsonValue) throws JSONException {
        String value;
        value = (String) jsonValue.getJSONObject(METADATA).get(LOCAL_URI);
        ContextWrapper cw = new ContextWrapper(KinetiseApplication.getInstance());
        File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        File file = new File(directory, value.replace(AssetsManager.PREFIX_LOCAL, ""));
        if (file.exists()) {
            value = AssetsManager.PREFIX_LOCAL + file.getAbsolutePath();
            dataFeedItem.put(key, value);
        }
    }

    public static void initLocalDB(LocalStorageDescriptionDataDesc localStorageDescriptionDataDesc) {
        TablesDescriptionDataDesc tableDescriptionDataDesc = localStorageDescriptionDataDesc.getTablesDescriptionDataDesc();
        if (tableDescriptionDataDesc != null) {
            ArrayList<TableDescriptionDataDesc> tablesDescription = tableDescriptionDataDesc.getTableListDescriptionDataDescs();
            if (tablesDescription != null && tablesDescription.size() > 0) {
                initLocalDB(tablesDescription);
            }
        }
    }

    private static void initLocalDB(ArrayList<TableDescriptionDataDesc> tablesDescription) {
        for (TableDescriptionDataDesc desc : tablesDescription) {
            String tableName = desc.getTableName();
            String jsonSource = null;
            if (desc.getInitDescriptionDataDesc() != null && desc.getInitDescriptionDataDesc().getSource() != null)
                jsonSource = desc.getInitDescriptionDataDesc().getSource();

            if (tableName != null && jsonSource != null)
                initLocalDB(jsonSource, tableName);

        }
    }
}
