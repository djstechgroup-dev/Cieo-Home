package com.kinetise.data.application.formdatautils;


import android.util.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;

import java.util.Map;

public class FormFormaterV3 extends FormFormater {

    public static final String FORM_SECTION_NAME = "form";
    public static final String PARAMS_SECTION_NAME = "params";
    public static final String GUID_PROPERTY_NAME = "id";

    @Override
    public String formatToString(FormData data) {
        return format(data, null).toString();
    }

    protected JsonObject format(FormData data) {
        return format(data, null);
    }

    public String getFormBody(AbstractAGViewDataDesc desc, String detailAlterApiContext, String detailGuid, Map<String, String> params) {
        return format(FormDataGatherer.getFormData(desc, detailAlterApiContext, detailGuid), params).toString();
    }

    public JsonObject format(FormData data, Map<String, String> params) {
        JsonObject root = new JsonObject();
        JsonObject formObject = new JsonObject();
        JsonObject paramsObject = formatParams(params);
        root.add(FORM_SECTION_NAME, formObject);
        root.add(PARAMS_SECTION_NAME, paramsObject);

        FormItemsGroup looseItems = data.looseItems;
        formatItemsGroup(looseItems, formObject);
        for (FeedFormData feedData : data.feeds) {
            formObject.add(feedData.mFeedFormId, formatFeedData(feedData));
        }
        return root;
    }

    private void formatItemsGroup(FormItemsGroup looseItems, JsonObject formObject) {
        if (looseItems != null && ((!looseItems.isEmpty()) || looseItems.getFeedItemGUID() != null)) {
            String guid = looseItems.getFeedItemGUID();
            if (guid != null) {
                formObject.addProperty(GUID_PROPERTY_NAME, guid);
            }
            for (Pair<String, Object> pair : looseItems.getFormItems())
                addProperty(pair.first, pair.second, formObject);
        }
    }

    private JsonArray formatFeedData(FeedFormData feedData) {
        JsonArray items = new JsonArray();
        JsonObject object;
        for (FormItemsGroup group : feedData.items) {
            if (!group.isEmpty()) {
                object = new JsonObject();
                formatItemsGroup(group, object);
                items.add(object);
            }
        }
        return items;
    }

    public static JsonObject formatParams(Map<String, String> params) {
        JsonObject result = new JsonObject();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                result.addProperty(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}
