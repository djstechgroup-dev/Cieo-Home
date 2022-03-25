package com.kinetise.data.application.feedmanager;

import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.helpers.threading.AGAsyncTask;

public class LoadMoreItemsTask extends AGAsyncTask {

    private String mBaseURL;
    private final IFeedClient mFeedClient;
    private final String mUri;
    private final HttpParamsDataDesc mHttpQueryParams;


    public LoadMoreItemsTask(String baseURL, IFeedClient feedClient, String uri, HttpParamsDataDesc httpParams) {
        mBaseURL = baseURL;
        mFeedClient = feedClient;
        mUri = uri;
        mHttpQueryParams = httpParams;
    }

    @Override
    public void run() {
        DownloadMoreFeedItemsCommand downloadFeedCommand = new DownloadMoreFeedItemsCommand(mBaseURL, mUri, mFeedClient);
        mFeedClient.setDownloadCommand(downloadFeedCommand);
        HttpParamsDataDesc headers = downloadFeedCommand.getFeedClient().getHeaders();
        FeedManager.downloadFeed(downloadFeedCommand, headers, mHttpQueryParams,null);
    }
}
