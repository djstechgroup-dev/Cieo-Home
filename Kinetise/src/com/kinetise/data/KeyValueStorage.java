package com.kinetise.data;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public abstract class KeyValueStorage<T> {

    protected Map<String, T> mData = new HashMap<String, T>();

    public void addValue(String key, T value){
        mData.put(key,value);
    };

    public void removeValue(String key){
            mData.remove(key);
    }

    public T getValue(String key){
        return mData.get(key);
    }

    protected void restore(SharedPreferences preferences) {
        Map<String,String> data = (Map<String,String>)preferences.getAll();
        if(data!=null)
        for(Map.Entry<String,String> entry:data.entrySet()){
            mData.put(entry.getKey(),deserialize(entry.getValue()));
        }
    }

    /**
     * Saves PropertyStorage map to the persistent storage. It can be then restored on app start.
     */
    protected void serialize(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        for(Map.Entry<String,T> entry:mData.entrySet())
            editor.putString(entry.getKey(),serialize(entry.getValue())).apply();
    }

    protected abstract String serialize(T element);
    protected abstract T deserialize(String data);

    public void clearValues(){
        mData.clear();
    }

}
