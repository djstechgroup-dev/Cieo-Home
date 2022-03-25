package com.kinetise.helpers.parser;

import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.ItemPath;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.UsingFields;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class JsonParser {

    public static Object getItem(Object object, String[] path, int sectionIndex) throws JsonParserException, JSONException {
        if (object instanceof JSONObject) {
            if (path.length == 0) {
                JSONArray array = new JSONArray();
                array.put(object);
                return array;
            }

            JSONObject map = (JSONObject) object;

            String elementPath = path[sectionIndex];
            String key;
            int arrayIndex;
            if (elementPath.charAt(elementPath.length() - 1) == ']') {
                // accessing array object
                key = elementPath.substring(0, elementPath.lastIndexOf('['));
                String arrayIndexString = elementPath.substring(elementPath.lastIndexOf('[') + 1, elementPath.length() - 1);
                arrayIndex = Integer.parseInt(arrayIndexString);
            } else {
                key = elementPath;
                arrayIndex = -1; // N/A
            }

            Object value = map.get(key);
            Object nextObject;

            if (value instanceof JSONArray) {
                if (arrayIndex != -1) {
                    nextObject = ((JSONArray) value).get(arrayIndex);
                } else {
                    nextObject = value;
                }
            } else {
                if (arrayIndex != -1) {
                    throw new JsonParserException(String.format("Can't access index [%d] of JSONObject", arrayIndex));
                }
                nextObject = map.get(key);
            }

            sectionIndex++;
            if (sectionIndex < path.length) {
                return getItem(nextObject, path, sectionIndex);
            } else {
                return nextObject;
            }

        } else if (object instanceof JSONArray) {
            if (path[sectionIndex].equals("*")) {
                return object;
            } else {
                throw new JsonParserException("Reading JSONArray without '*'");
            }
        } else {
            if (object == JSONObject.NULL) {
                return null;
            } else if (object != null) {
                throw new JsonParserException(String.format("Expected JSONObject or JSONArray got %s", object.getClass()));
            } else {
                return null;
            }
        }

    }

    public static Object getItem(Object object, String[] path) throws JsonParserException, JSONException {
        return getItem(object, path, 0);
    }

    public static Object getItem(Object object, ItemPath itemPath) throws JsonParserException, JSONException {
        String[] itemPathAsArray = parseItemPath(itemPath.getXPath());
        return getItem(object, itemPathAsArray, 0);
    }


    // Separated for easier profiling
    public static Object parseToObjects(String json) throws JSONException {
        if (json.startsWith("{")) {
            return new JSONObject(json);
        } else {
            return new JSONArray(json);
        }
    }

    public static String[] parseItemPath(String itemPath) {
        if(itemPath == null)
            itemPath = "";
        itemPath = itemPath.replace("$.", "");
        if (itemPath.length() == 0)
            return new String[0];
        return itemPath.split("\\.");
    }

}
