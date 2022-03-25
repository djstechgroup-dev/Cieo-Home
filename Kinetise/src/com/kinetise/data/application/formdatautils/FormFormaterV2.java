package com.kinetise.data.application.formdatautils;

import android.util.Pair;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class FormFormaterV2 extends FormFormater {
    public static final String ALTERAPICONTEXT = "alterapicontext";
    public static final String ITEMS_ARRAY_NAME = "items";
    public static final String FORM_SECTION_NAME = "form";

    @Override
    protected String formatToString(FormData data) {
        JsonObject item;
        JsonArray itemsArray = new JsonArray();
        jsonObject.add(FormFormaterV2.ITEMS_ARRAY_NAME,itemsArray);
        if(data.isInDataFeed){
            item = createEmptyItem(data.screenAlterApiContext);
            itemsArray.add(item);

            item = createItemFromGroup(data.looseItems);
            itemsArray.add(item);
        } else {
            item = createItemFromGroup(data.looseItems);
            itemsArray.add(item);
        }
        for(FeedFormData feedData:data.feeds) {
            for(FormItemsGroup group:feedData.items) {
                item = createItemFromGroup(group);
                itemsArray.add(item);
            }
        }
        return jsonObject.toString();
    }

    protected JsonObject createItemFromGroup(FormItemsGroup group){
        JsonObject result = createItem(group.getAlterApiContext());
        JsonObject formDataDictionary = new JsonObject();
        result.add(FORM_SECTION_NAME,formDataDictionary);
        for(Pair<String,Object> formItem:group.getFormItems()) {
            addProperty(formItem.first, formItem.second, formDataDictionary);
        }
        return result;
    }

    protected JsonObject createItem(String alterApiContext){
        JsonObject result = new JsonObject();
        result.addProperty(ALTERAPICONTEXT, alterApiContext);
        return result;
    }

    protected JsonObject createEmptyItem(String alterApiContext){
        JsonObject result = createItem(alterApiContext);
        JsonObject formDataDictionary = new JsonObject();
        result.add(FORM_SECTION_NAME,formDataDictionary);
        return result;
    }

}
