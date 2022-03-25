package com.kinetise.data.application.actionmanager.functioncommands;


import android.content.Intent;
import android.net.Uri;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.support.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FunctionOpenExternalApp extends AbstractFunction {
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String INTENT_DATA = "data";
    public static final String ACTION_TYPE = "actionType";
    public static final String TYPE = "type";

    public FunctionOpenExternalApp(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    private static String PARAMS = "params";

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String json = mFunctionDataDesc.getAttributes()[0].getStringValue();
        executeIntent(mApplication, json);
        return null;
    }

    private void executeIntent(IAGApplication mApplication, String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            Logger.e(this, "execute", e.toString());
            return;
        }

        Intent intent = new Intent();
        try {
            setIntentData(jsonObject, intent);
            setActionType(jsonObject, intent);
            setType(jsonObject, intent);
            setIntentParams(jsonObject, intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        startIntent(mApplication, intent);
    }

    private void setType(JSONObject jsonObject, Intent intent) throws JSONException {
        if (!jsonObject.has(TYPE)) return;

        String data = jsonObject.getString(TYPE);
        intent.setType(data);
    }

    private void setActionType(JSONObject jsonObject, Intent intent) throws JSONException {
        if (!jsonObject.has(ACTION_TYPE)) return;

        String data = jsonObject.getString(ACTION_TYPE);
        intent.setAction(data);
    }

    private void startIntent(IAGApplication mApplication, Intent intent) {
        try {
            mApplication.getActivity().startActivity(intent);
        } catch (Exception e) {
            Logger.e(this, "execute", e.toString());
        }
    }

    private void setIntentParams(JSONObject jsonObject, Intent intent) throws JSONException {
        int length = jsonObject.getJSONArray(PARAMS).length();
        for (int i = 0; i < length; i++) {
            JSONObject param = jsonObject.getJSONArray(PARAMS).getJSONObject(i);
            setParamFromJSONObject(param, intent);
        }
    }

    private void setIntentData(JSONObject jsonObject, Intent intent) throws JSONException {
        if (!jsonObject.has(INTENT_DATA)) return;
        String data = jsonObject.getString(INTENT_DATA);
        intent.setData(Uri.parse(data));
    }

    private void setParamFromJSONObject(JSONObject param, Intent intent) throws JSONException {
        String key = param.getString(KEY);
        Object value = param.get(VALUE);
        if (value instanceof String) {
            intent.putExtra(key, (String) value);
        }
        if (value instanceof JSONArray) {
            String[] array = getParamsAsStringArray((JSONArray) value);
            intent.putExtra(key, array);

        }
    }


    private String[] getParamsAsStringArray(JSONArray jsonArray) throws JSONException {
        String[] array = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = (String) jsonArray.get(i);
        }
        return array;
    }


}
