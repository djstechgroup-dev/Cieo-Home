package com.kinetise.data.application.feedmanager;

import android.support.annotation.Nullable;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Namespaces;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Pagination;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.UsingFields;
import com.kinetise.data.descriptors.types.AGFeedCachePolicyType;
import com.kinetise.data.descriptors.types.AGFeedFormatType;
import com.kinetise.data.sourcemanager.AbstractGetSourceCommand;
import com.kinetise.data.sourcemanager.DataFeedResponse;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.helpers.http.CacheControlOptions;
import com.kinetise.helpers.io.BOMInputStream;
import com.kinetise.helpers.io.ByteOrderMark;
import com.kinetise.helpers.parser.JsonFeedParser;
import com.kinetise.helpers.parser.XmlFeedParser;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InterruptedIOException;

public class DownloadFeedCommand extends AbstractGetSourceCommand {

    protected IFeedClient mFeedClient;
    protected boolean mInterrupted;
    protected boolean mDisplayResult;
    protected boolean mDisplayError;
    protected final long mDelay;
    protected final boolean mLongPolling;
    protected final long mPreviousResponseDate;
    protected long mResponseDate;
    private CacheControlOptions mCacheOption;

    public DownloadFeedCommand(String baseUrl, String source, IFeedClient feedClient) {
        super(baseUrl, source);
        mCacheOption = CacheControlOptions.USE_CACHE;
        mFeedClient = feedClient;
        mDisplayResult = true;
        mDisplayError = true;
        mDelay = 0;
        mLongPolling = false;
        if (mLongPolling) {
            mCacheOption = CacheControlOptions.NO_CACHE;
        }
        mPreviousResponseDate = 0;
    }

    public DownloadFeedCommand(String baseUrl, String source, IFeedClient feedClient, CacheControlOptions cacheOption, boolean displayResult, boolean displayError, long delay, boolean longPolling, long longPoolingResponseTimestamp) {
        super(baseUrl, source);
        mCacheOption = cacheOption;
        mFeedClient = feedClient;
        mDisplayResult = displayResult;
        mDisplayError = displayError;
        mDelay = delay;
        mLongPolling = longPolling;
        if (mLongPolling) {
            mCacheOption = CacheControlOptions.NO_CACHE;
        }
        mPreviousResponseDate = longPoolingResponseTimestamp;
    }

    public IFeedClient getFeedClient() {
        return mFeedClient;
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
        if (result instanceof DataFeed){
            FeedManager.setFeed(mFeedClient, (DataFeed)result);
        }else if (result instanceof IFeedClient) {
            handleFeedClientResult((IFeedClient) result);
        } else {
            handleStreamResult((DataFeedResponse) result);
        }
        startNextDownload();
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
                FeedManager.addFeedToDataFeedsMap(mFeedClient, dataFeed);
                if (mDisplayResult && dataFeed != null && FeedManager.isFeedChanged(mFeedClient.getFeedDescriptor(), dataFeed)) {
                    FeedManager.setFeed(mFeedClient, dataFeed);
                }
            }
        } catch (InterruptedIOException e) {
            //do nothing
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

    @Nullable
    public static BufferedInputStream getBufferedInputStream(InputStream stream) {
        BOMInputStream bom = new BOMInputStream(stream, false, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE);
        return new BufferedInputStream(bom);
    }

    protected void handleFeedClientResult(final IFeedClient result) {
        if (mInterrupted || !mDisplayResult)
            return;
        mFeedClient.setActiveItemIndex(result.getActiveItemIndex());
        handleDataFeedResult(result.getFeedDescriptor());
    }

    public void handleDataFeedResult(DataFeed dataFeed) {
        FeedManager.setFeed(mFeedClient, dataFeed);
    }

    protected static SystemDisplay getSystemDisplay() {
        return AGApplicationState.getInstance().getSystemDisplay();
    }

    @Override
    public java.lang.Object[] getParams() {
        return null;
    }

    @Override
    public void onError() {
        if (mInterrupted || !mDisplayError) return;

        FeedManager.restoreFromCacheOrSetError(mFeedClient);
        startNextDownload();
    }

    @Override
    public void onError(final int status, final PopupMessage... messages) {
        if (mInterrupted)
            return;

        startNextDownload();
        if (mDisplayError) {
            FeedManager.restoreFromCacheOrSetError(status, mFeedClient);
            handleFeedResponse(status, messages);
        }
    }

    private void startNextDownload() {
        if (mFeedClient.getCachePolicyType() == AGFeedCachePolicyType.REFRESH_EVERY || mFeedClient.getCachePolicyType() == AGFeedCachePolicyType.CACHE_DATA_REFRESH_EVERY) {
            long delay = mFeedClient.getCachePolicyAttribute();
            if (delay > 0) {
                FeedManager.prepareAndExecuteDownloadFeedCommand(mFeedClient, CacheControlOptions.USE_CACHE, true, false, delay, false, 0);
            }
        } else if (mFeedClient.getCachePolicyType() == AGFeedCachePolicyType.LIVE) {
            FeedManager.prepareAndExecuteDownloadFeedCommand(mFeedClient, CacheControlOptions.USE_CACHE, true, false, 0, true, mResponseDate);
        }
    }

    protected void handleFeedResponse(int statusCode, PopupMessage... messages) {
        if (messages != null) {
            if ((statusCode == 401 || statusCode == 403) && AGApplicationState.hasLoginScreen()) {
                for (PopupMessage message : messages)
                    PopupManager.showPopup(message.getDescription(), message.getTitle());
            } else {
                for (PopupMessage message : messages)
                    PopupManager.showToast(message.getDescription());
            }
        }
    }

    @Override
    public CacheControlOptions getCacheOption() {
        return mCacheOption;
    }

    @Override
    public void setCacheOption(CacheControlOptions cacheOption) {
        mCacheOption = cacheOption;
    }

    public void cancel() {
        super.cancel();
        mInterrupted = true;
    }

    @Override
    public boolean isDisplayResult() {
        return mDisplayResult;
    }

    /**
     * Parses given stream to FeedDescriptor, stores it in cache and refreshes view to
     * reflect changes
     */
    protected synchronized DataFeed parseFeed(BufferedInputStream stream) throws Exception, OutOfMemoryError {
        if (mInterrupted) {
            throw new InterruptedException();
        }

        UsingFields feedClientFields = mFeedClient.getUsingFields();
        UsingFields cachedFields = DataFeedsMap.getInstance().getCachedUsingFields(mFeedClient);
        UsingFields combinedFields = UsingFields.combine(feedClientFields, cachedFields);

        Pagination pagination = mFeedClient.getPagination();
        String nextPagePath = null;
        if (pagination != null)
            nextPagePath = pagination.getNextPagePath();

        return parseFeed(stream, mFeedClient.getFormat(), mFeedClient.getNamespaces(), mFeedClient.getItemPath().getXPath(), combinedFields, nextPagePath);
    }

    public static DataFeed parseFeed(BufferedInputStream stream, AGFeedFormatType format, Namespaces namespaces, String itemPath, UsingFields usingFields, String nextPagePath) throws
            Exception, OutOfMemoryError {
        DataFeed result;

        String keyNotFoundMessage = LanguageManager.getInstance().getString(LanguageManager.NODE_NOT_FOUND);
        keyNotFoundMessage = keyNotFoundMessage == null ? "key: not found" : keyNotFoundMessage;

        //our feed is in XML format
        if (AGFeedFormatType.XML.equals(format)) {
            result = XmlFeedParser.parseFromStream(stream, itemPath, namespaces, keyNotFoundMessage, usingFields, nextPagePath);
        } else {
            //our feed is in JSON format
            result = JsonFeedParser.parse(stream, itemPath, usingFields, keyNotFoundMessage, nextPagePath);
        }

        return result;
    }

    public long getDelay() {
        return mDelay;
    }

    public boolean isLongPolling() {
        return mLongPolling;
    }

    public void setResponseTimestamp(long timestamp) {
        mResponseDate = timestamp;
    }

    public long getPreviousResponseDate() {
        return mPreviousResponseDate;
    }

    @Override
    public void setResolvedUri(String resolvedUri) {
        mFeedClient.setResolvedURL(resolvedUri);
    }

    @Override
    public String resolveURI() {
        String resolvedURI = super.resolveURI();
        setResolvedUri(resolvedURI);
        return resolvedURI;
    }
}



