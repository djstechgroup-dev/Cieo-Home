package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;

public class Pagination {

    private NextPageUrl mNextPageUrl;
    private NextPageToken mNextPageToken;

    public NextPageUrl getNextPageUrl() {
        return mNextPageUrl;
    }

    public void setNextPageUrl(NextPageUrl nextPageUrl) {
        mNextPageUrl = nextPageUrl;
    }

    public NextPageToken getNextPageToken() {
        return mNextPageToken;
    }

    public void setNextPageToken(NextPageToken nextPageToken) {
        mNextPageToken = nextPageToken;
    }

    public Pagination copy() {
        Pagination copied = new Pagination();

        copied.mNextPageUrl = mNextPageUrl.copy();
        copied.mNextPageToken = mNextPageToken.copy();

        return copied;
    }

    public String getNextPagePath() {
        if (mNextPageUrl != null)
            return mNextPageUrl.getNextPageUrl();
        else if (mNextPageToken != null)
            return mNextPageToken.getToken();
        else
            return null;
    }
}
