package com.kinetise.data;

import android.content.Context;

import com.kinetise.helpers.preferences.SecurePreferencesHelper;

public class VariableStorage extends KeyValueStorage<String>{
    private static VariableStorage mInstance;

    private VariableStorage(){
    }

    public static VariableStorage getInstance() {
        if (mInstance == null) {
            synchronized (VariableStorage.class){
                if (mInstance == null) {
                    mInstance = new VariableStorage();
                    mInstance.restore(SecurePreferencesHelper.getVariables());
                }
            }
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    public static void serialize(){
        if (mInstance!=null) {
            mInstance.serialize(SecurePreferencesHelper.getVariables());
        }
    }

    @Override
    protected String serialize(String element) {
        return element;
    }

    @Override
    protected String deserialize(String data) {
        return data;
    }
}
