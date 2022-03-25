package com.kinetise.data.application.formdatautils;

import java.util.ArrayList;
import java.util.List;

public class FeedFormData {
    String mFeedFormId;
    ArrayList<FormItemsGroup> items;

    public FeedFormData(String formId) {
        mFeedFormId = formId;
        items = new ArrayList<>();
    }

    public List<FormItemsGroup> getItems(){
        return items;
    }

    public void addItem(FormItemsGroup item){
        items.add(item);
    }

    public boolean isEmpty(){
        return items.isEmpty();
    }
}
