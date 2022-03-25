package com.kinetise.data.application.formdatautils;

import android.util.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class FormFormaterForEmail extends FormFormaterV2 {

    protected void addProperty(String key, Object value, JsonArray jsonArray) {
        JsonObject jsonObject = new JsonObject();
        jsonArray.add(jsonObject);

        super.addProperty(key, value, jsonObject);
    }

    @Override
    protected JsonObject createItemFromGroup(FormItemsGroup group) {
        JsonObject result = createItem(group.getAlterApiContext());
        JsonArray formDataDictionary = new JsonArray();
        result.add(FORM_SECTION_NAME, formDataDictionary);
        for (Pair<String, Object> formItem : group.getFormItems()) {
            addProperty(formItem.first, formItem.second, formDataDictionary);
        }
        return result;
    }

    @Override
    protected JsonObject createEmptyItem(String alterApiContext) {
        JsonObject result = createItem(alterApiContext);
        JsonArray formDataDictionary = new JsonArray();
        result.add(FORM_SECTION_NAME, formDataDictionary);
        return result;
    }

}
