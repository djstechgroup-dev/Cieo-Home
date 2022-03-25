package com.kinetise.data.application.alterapimanager;

import java.util.ArrayList;

/**
 * Class contains list of messages returned from AlterApiResponses.<br>
 * User: Mateusz
 * Date: 13.03.13
 * Time: 12:55
 */
public class AAMessage {

    private ArrayList<String> mMessageValueList = new ArrayList<String>();

    public void addMessageValue(String msg) {
        mMessageValueList.add(msg);
    }

    public ArrayList<String> getMessageValues() {
        return mMessageValueList;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(String s:mMessageValueList)
            sb.append(s);
        return sb.toString();
    }
}
