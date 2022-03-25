package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;

public class NextPageUrl {
    private String mNextPageUrl;

    public NextPageUrl() {
    }

    public NextPageUrl(String url) {
        mNextPageUrl = url;
    }

    public String getNextPageUrl() {
        return mNextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        mNextPageUrl = nextPageUrl;
    }

    public NextPageUrl copy() {
        return new NextPageUrl(mNextPageUrl);
    }

}
