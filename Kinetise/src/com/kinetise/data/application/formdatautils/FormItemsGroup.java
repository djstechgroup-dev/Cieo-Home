package com.kinetise.data.application.formdatautils;

import android.util.Pair;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FormItemsGroup {

    public String mAlterApiContext;
    public List<Pair<String,Object>> formItems;
    public String mFeedItemGUID;

    public FormItemsGroup(){
        formItems = new ArrayList<>();
    }
    public void addElement(String key, Object value){
        formItems.add(new Pair<>(key,value));
    }

    public List<Pair<String,Object>> getFormItems(){
        return formItems;
    }

    public Object getValue(String key){
        for(Pair<String,Object> pair:getFormItems()){
            if(pair.first.equals(key))
                return pair.second;
        }
        return null;
    }

    public void setAlterApiContext(String alterApiContext) {
        mAlterApiContext = alterApiContext;
    }

    public String getAlterApiContext() {
        return mAlterApiContext;
    }

    public void setFeedItemGUID(String guid){
        mFeedItemGUID = guid;
    }

    public String getFeedItemGUID(){
        return mFeedItemGUID;
    }

    public boolean isEmpty() {
        return formItems == null || formItems.isEmpty();
    }

    public boolean matches(String guid){
        return (StringUtils.equals(guid,mFeedItemGUID) );
    }
}
