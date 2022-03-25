package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;

public class NextPageToken {

    private String mParam;
    private String mToken;

    public NextPageToken() {
    }

    public NextPageToken(String param, String token) {
        mParam = param;
        mToken = token;
    }

    public String getParam() {
        return mParam;
    }

    public void setParam(String param) {
        mParam = param;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public NextPageToken copy() {
        return new NextPageToken(mParam, mToken);
    }

}
