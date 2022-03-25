package com.kinetise.data.application.formdatautils;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.types.FormBoolean;

public abstract class FormFormater {
    JsonObject jsonObject = new JsonObject();

    public String getFormBody(AbstractAGViewDataDesc desc) {
        return getFormBody(desc, null, null);
    }

    public String getFormBody(AbstractAGViewDataDesc desc, String detailAlterApiContext, String detailGuid) {
        FormDataGatherer gatherer = new FormDataGatherer();
        return formatToString(gatherer.getFormData(desc, detailAlterApiContext, detailGuid));
    }

    protected abstract String formatToString(FormData data);

    protected void addProperty(String key, Object value, JsonObject jsonObject) {
        if (value == null) {
            jsonObject.add(key, new JsonNull());
        } else if (value instanceof FormBoolean) {
            jsonObject.addProperty(key, ((FormBoolean) value).getOriginalValue());
        } else {
            jsonObject.addProperty(key, value.toString());
        }
    }
}
