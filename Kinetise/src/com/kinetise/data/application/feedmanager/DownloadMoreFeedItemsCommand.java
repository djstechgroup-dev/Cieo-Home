package com.kinetise.data.application.feedmanager;

import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.sourcemanager.DataFeedResponse;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.helpers.asynccaller.AsyncCaller;
import com.kinetise.helpers.http.CacheControlOptions;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.InterruptedIOException;

public class DownloadMoreFeedItemsCommand extends DownloadFeedCommand {

    public DownloadMoreFeedItemsCommand(String baseUrl, String source, IFeedClient feedClient) {
        super(baseUrl, source, feedClient);
    }

    /**
     * This method is executed after obtaining source from uri.
     */
    public void postGetSource(final Object result) {
        clearAssociatedTask();
        if (mInterrupted)
            return;
        if (result == null) {
            throw new IllegalArgumentException("Cannot handle null pointer in result parameter of postGetSource -" +
                    "probably cannot find source");
        }
        handleStreamResult((DataFeedResponse) result);
    }

    protected void handleStreamResult(DataFeedResponse result) {
        BufferedInputStream bis = getBufferedInputStream(result.dataStream);
        if (mInterrupted || bis == null) {
            IOUtils.closeQuietly(bis);
            return;
        }
        DataFeed dataFeed;
        try {
            dataFeed = parseFeed(bis);
            dataFeed.setTimestamp(result.timestamp);
            if (!mInterrupted) {
                FeedManager.addMoreItemsToDataFeedsMap(mFeedClient, dataFeed);
                AsyncCaller.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FeedManager.showMoreItems(mFeedClient, true);
                    }
                });
            }
        } catch (InterruptedIOException e) {
            //do nothing
            onError();
        } catch (Exception e) {
            e.printStackTrace();
            PopupManager.showToast(LanguageManager.getInstance().getString(LanguageManager.ERROR_DATA));
            onError();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            onError();
        } finally {
            IOUtils.closeQuietly(bis);
        }
    }

    @Override
    public void onError() {
        mFeedClient.clearTemplateControls();
        FeedManager.loadNextPage(mFeedClient, true);
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FeedManager.reloadFeed(mFeedClient);
            }
        });
    }

    @Override
    public void onError(final int status, final PopupMessage... messages) {
        if (mInterrupted)
            return;
        onError();
        if (mDisplayError)
            handleFeedResponse(status, messages);
    }

    @Override
    public CacheControlOptions getCacheOption() {
        return CacheControlOptions.USE_CACHE;
    }
}



