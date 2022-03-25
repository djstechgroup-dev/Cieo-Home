package com.kinetise.data.application.formdatautils;

import java.util.ArrayList;

public class FormData {
    public String screenAlterApiContext;
    public FormItemsGroup looseItems;
    public boolean isInDataFeed;
    public ArrayList<FeedFormData> feeds;
    public String screenGuid;

    public FormData(){
        feeds = new ArrayList<>();
    }
}
