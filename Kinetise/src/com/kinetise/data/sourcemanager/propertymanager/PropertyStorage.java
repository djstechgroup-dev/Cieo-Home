package com.kinetise.data.sourcemanager.propertymanager;

import com.google.gson.Gson;
import com.kinetise.data.KeyValueStorage;
import com.kinetise.helpers.preferences.SecurePreferencesHelper;

public class PropertyStorage extends KeyValueStorage<Property> {

    private static PropertyStorage mInstance;

    private PropertyStorage() {
        restorePropertyStorage();
    }

    public static PropertyStorage getInstance() {
        if (mInstance == null) {
            synchronized (PropertyStorage.class){
                if (mInstance == null) {
                    mInstance = new PropertyStorage();
                }
            }
        }
        return mInstance;
    }


    public static void clearInstance(){
        mInstance = null;
    }


    public void restorePropertyStorage() {
        restore(SecurePreferencesHelper.getProperties());
    }

    public static void serialize(){
        if (mInstance!=null)
            mInstance.serialize(SecurePreferencesHelper.getProperties());
    }

    @Override
    protected String serialize(Property element) {
        return new Gson().toJson(element);
    }

    @Override
    protected Property deserialize(String data) {
        return new Gson().fromJson(data,Property.class);
    }
}
