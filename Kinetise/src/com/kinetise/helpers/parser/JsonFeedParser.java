package com.kinetise.helpers.parser;

import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.UsingFields;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import static com.kinetise.helpers.parser.JsonParser.getItem;
import static com.kinetise.helpers.parser.JsonParser.parseItemPath;
import static com.kinetise.helpers.parser.JsonParser.parseToObjects;

public class JsonFeedParser {

    public static DataFeed parse(InputStream inputStream, String itemPath, UsingFields usingFields, String notFoundMessage, String nextPagePath) throws Exception {
        String json = IOUtils.toString(inputStream);
        return parse(json, itemPath, usingFields, notFoundMessage, nextPagePath);
    }

    public static DataFeed parse(String json, String itemPath, UsingFields usingFields, String notFoundMessage, String nextPagePath) throws Exception {
        DataFeed dataFeed = new DataFeed();
        JSONArray list;

        Object[] result = parseItemsWithPath(json, itemPath, nextPagePath);
        list = (JSONArray) result[0];
        if (result.length == 2) {
            Object nextPageObject = result[1];
            if (nextPageObject instanceof String) {
                dataFeed.setNextPageAddress((String) nextPageObject);
            } else if (nextPageObject instanceof JSONArray) {
                JSONArray nextPageArray = (JSONArray) nextPageObject;
                if (nextPageArray.length() > 0) {
                    dataFeed.setNextPageAddress(nextPageArray.getString(0));
                }
            }
        }

        int maxElements = list.length();

        // add empty items, we will populate them later
        for (int i = 0; i < maxElements; i++) {
            DataFeedItem dataFeedItem = new DataFeedItem(notFoundMessage);
            dataFeed.addItem(dataFeedItem);
        }

        int fieldsCount = usingFields.getFields().size();
        String[][] splitFields = new String[fieldsCount][];

        for (int i = 0; i < fieldsCount; i++) {
            splitFields[i] = parseItemPath(usingFields.getFields().get(i).getXpath());
        }

        for (int i = 0; i < maxElements; i++) {
            DataFeedItem item = dataFeed.getItem(i);
            for (int j = 0; j < fieldsCount; j++) {
                Object value;
                try {
                    value = getItem(list.get(i), splitFields[j], 0);
                } catch (JsonParserException e) {
                    e.printStackTrace();
                    value = null;
                } catch (JSONException e) {
                    //Thrown when item is not found, it would blow up console if logged.
                    value = null;
                }

                if (value != null) {
                    if (value.equals(JSONObject.NULL))
                        value = DataFeedItem.NULL;

                    item.put(usingFields.getFields().get(j).getId(), value);
                }
            }
        }


        return dataFeed;
    }

    public static Object[] parseItemsWithPath(String json, String itemPath, String nextPagePath) throws JSONException, JsonParserException {
        Object jsonObject = parseToObjects(json);
        return parseItemsWithPath(jsonObject, itemPath, nextPagePath);
    }

    public static Object[] parseItemsWithPath(Object jsonObject, String itemPath, String nextPagePath) throws JSONException, JsonParserException {
        Object[] result;

        String[] itemPathAsArray = parseItemPath(itemPath);
        if (nextPagePath != null) {
            result = new Object[2];
            String[] nextPagePathAsArray = parseItemPath(nextPagePath);
            Object nextPageAddress = null;
            try {
                nextPageAddress = getItem(jsonObject, nextPagePathAsArray);
            } catch (JsonParserException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            result[1] = nextPageAddress;
        } else {
            result = new Object[1];
        }

        result[0] = getItem(jsonObject, itemPathAsArray);
        return result;
    }

}
