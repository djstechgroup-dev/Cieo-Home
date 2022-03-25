package com.kinetise.data.application.feedmanager;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.types.AGFeedCachePolicyType;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.helpers.http.CacheControlOptions;
import com.kinetise.helpers.http.NetworkUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class LoadFeedsTask {

    private final AbstractAGElementDataDesc mViewDataDesc;
    private final boolean mResetFeeds;
    private final boolean mOnRefresh;

    public LoadFeedsTask(AbstractAGElementDataDesc viewDataDesc, boolean resetFeeds, boolean onRefresh) {
        mViewDataDesc = viewDataDesc;
        mResetFeeds = resetFeeds;
        mOnRefresh = onRefresh;
    }

    public void execute() {
        ArrayList<IFeedClient> feedClients = FeedManager.getFeedClientsInsideDataDesc(mViewDataDesc);
        if (feedClients != null) {
            boolean isInternetConnection = NetworkUtils.isNetworkAvailable(AGApplicationState.getInstance().getContext());

            //Iterator instead of for-each loop due to ConcurrentModificationException
            Iterator<IFeedClient> iter = feedClients.iterator();
            while (iter.hasNext()) {
                IFeedClient feedClient = iter.next();
                feedClient.setShouldRecreate(true);
                if (!mOnRefresh || !((String) feedClient.getSource().getValue()).startsWith(AssetsManager.PREFIX_CONTROL))
                    //when back from background (onRefresh) load feed only for non-context feeds (to exclude Gallery preview)
                    loadFeed(feedClient, isInternetConnection);
            }
        }
    }

    private void loadFeed(final IFeedClient feedClient, boolean isInternetConnection) {
        resolveUri(feedClient);

        feedClient.setScreenHashCode(mViewDataDesc.hashCode());
        if (mResetFeeds) {
            feedClient.setLastFeedItemCount(0);
            feedClient.resetScrolls();
        }

        if (feedClient.getCachePolicyType() == AGFeedCachePolicyType.FRESH_DATA
                || feedClient.getCachePolicyType() == AGFeedCachePolicyType.LIVE
                || feedClient.getCachePolicyType() == AGFeedCachePolicyType.REFRESH_EVERY
                || feedClient.getCachePolicyType() == AGFeedCachePolicyType.NO_STORE) {
            FeedManager.displayFreshData(feedClient, isInternetConnection);
        } else if (feedClient.getCachePolicyType() == AGFeedCachePolicyType.CACHE_DATA
                || feedClient.getCachePolicyType() == AGFeedCachePolicyType.CACHE_DATA_REFRESH_EVERY
                || feedClient.getCachePolicyType() == AGFeedCachePolicyType.CACHE_DATA_AND_REFRESH) {

            boolean displayResult = feedClient.getCachePolicyType() == AGFeedCachePolicyType.CACHE_DATA ? false : true;
            feedClient.setShouldRecreate(true);

            if (FeedManager.tryRestoreFromCache(feedClient)) {
                FeedManager.prepareAndExecuteDownloadFeedCommand(feedClient, CacheControlOptions.USE_CACHE, displayResult, false);
            } else {
                FeedManager.displayFromInternetOrSetError(feedClient);
            }
        } else if (feedClient.getCachePolicyType() == AGFeedCachePolicyType.MAX_AGE) {
            if (new Date().getTime() - DataFeedsMap.getInstance().getDataFeedTimestamp(feedClient) <= feedClient.getCachePolicyAttribute()) {
                //data in cache still valid (not expired)
                if (!FeedManager.tryRestoreFromCache(feedClient)) {
                    FeedManager.displayFromInternetOrSetError(feedClient);
                }
            } else {
                FeedManager.displayFreshData(feedClient, isInternetConnection);
            }
        }
    }

    private void resolveUri(IFeedClient feedClient) {
        AbstractAGViewDataDesc feedView = (AbstractAGViewDataDesc) feedClient;
        String baseAdress = feedView.getFeedBaseAdress();
        String resolvedUri = AssetsManager.resolveURI(feedClient.getStringSource(), baseAdress);
        feedClient.setResolvedURL(resolvedUri);
    }
}

