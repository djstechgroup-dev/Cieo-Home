package com.kinetise.data.application.formdatautils;

import android.util.Pair;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.helpers.http.NetworkUtils;

import java.util.Map;

public class FormFormaterUrlEncoded extends FormFormater {
    @Override
    protected String formatToString(FormData data) {
        return format(data,null);
    }

    public String getFormBody(AbstractAGViewDataDesc desc, Map<String,String> params) {
        return format(FormDataGatherer.getFormData(desc, null, null),params);
    }

    public String format(FormData data, Map<String, String> params) {
        for(Pair<String,Object> item :data.looseItems.formItems){
            params.put(item.first,item.second.toString());
        }
        return NetworkUtils.createPostBody(params);
    }
}
