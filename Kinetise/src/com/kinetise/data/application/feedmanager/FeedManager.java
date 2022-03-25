package com.kinetise.data.application.feedmanager;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.descriptors.AGChartDataDesc;
import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGTemplateDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.AGItemTemplateDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.LoadMoreDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.NoDataDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Pagination;
import com.kinetise.data.descriptors.desctriptorvisitors.ClearFormValuesVisitor;
import com.kinetise.data.descriptors.desctriptorvisitors.FindDescendantsByTypeVisitor;
import com.kinetise.data.descriptors.desctriptorvisitors.SetDescendantIdsAndIndexesVisitor;
import com.kinetise.data.descriptors.types.AGFeedCachePolicyType;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.helpers.asynccaller.AsyncCaller;
import com.kinetise.helpers.http.CacheControlOptions;
import com.kinetise.helpers.http.NetworkUtils;
import com.kinetise.helpers.threading.ThreadPool;
import com.kinetise.support.scrolls.scrollManager.ScrollManager;

import java.util.ArrayList;
import java.util.List;

public class FeedManager {

    public static void startLoadingFeeds(final AbstractAGElementDataDesc viewDataDesc, final boolean resetFeeds, final boolean onRefresh) {
        LoadFeedsTask loadFeedsTask = new LoadFeedsTask(viewDataDesc, resetFeeds, onRefresh);
        loadFeedsTask.execute();
    }

    public static void stopLoadingFeeds(final AbstractAGElementDataDesc viewDataDesc) {
        ArrayList<IFeedClient> feedClients = getFeedClientsInsideDataDesc(viewDataDesc);
        if (feedClients != null) {
            for (IFeedClient feedClient : feedClients) {
                DownloadFeedCommand command = feedClient.getDownloadCommad();
                if (command != null)
                    command.cancel();
            }
        }
    }

    public static ArrayList<IFeedClient> getFeedClientsInsideDataDesc(AbstractAGElementDataDesc viewDataDesc) {
        if (viewDataDesc instanceof AGScreenDataDesc) {
            // optimization to not search again for feeds
            return ((AGScreenDataDesc) viewDataDesc).getFeedClients();
        } else {
            FindDescendantsByTypeVisitor findByTypeVisitor = new FindDescendantsByTypeVisitor<IFeedClient>(IFeedClient.class);
            viewDataDesc.accept(findByTypeVisitor);
            return findByTypeVisitor.getFoundDataDescriptors();
        }
    }


    public static void clearControlsOnFeeds(AbstractAGElementDataDesc viewDataDesc) {
        ArrayList<IFeedClient> descs = getFeedClientsInsideDataDesc(viewDataDesc);
        clearControlsOnFeeds(descs);
    }

    public static void clearControlsOnFeeds(final ArrayList<IFeedClient> elementsDataDesc) {
        for (IFeedClient dataFeedDataDesc : elementsDataDesc) {
            clearControlsOnFeed(dataFeedDataDesc);
        }
    }

    private static void clearControlsOnFeed(final IFeedClient feedClient) {
        feedClient.saveFormData();
        ((AbstractAGElementDataDesc) feedClient).resolveVariables();

        feedClient.resetFeed();
        feedClient.setFeedDescriptor(null);
    }

    public static void prepareAndExecuteDownloadFeedCommand(IFeedClient feedClient, CacheControlOptions cacheOption, boolean displayResult, boolean displayError) {
        prepareAndExecuteDownloadFeedCommand(feedClient, cacheOption, displayResult, displayError, 0, false, 0);
    }

    public static void prepareAndExecuteDownloadFeedCommand(IFeedClient feedClient, CacheControlOptions cacheOption, boolean displayResult, boolean displayError, long delay,
                                                            boolean startLongPooling, long longPoolingResponseTimestamp) {
        AbstractAGViewDataDesc feedView = (AbstractAGViewDataDesc) feedClient;
        String baseAdress = feedView.getFeedBaseAdress();
        String uri = feedClient.getStringSource();
        DownloadFeedCommand downloadFeedCommand = new DownloadFeedCommand(baseAdress, uri, feedClient, cacheOption, displayResult, displayError, delay, startLongPooling, longPoolingResponseTimestamp);
        feedClient.setDownloadCommand(downloadFeedCommand);
        downloadFeed(downloadFeedCommand);
    }

    private static void initAndShowLoadingOnFeed(final IFeedClient feedClient, boolean reload) {
        feedClient.saveFormData();
        ((AbstractAGElementDataDesc) feedClient).resolveVariables();

        feedClient.resetFeed();
        feedClient.setFeedDescriptor(null);
        addTemplate(feedClient, feedClient.getLoadingTemplate());
        if (reload) {
            AsyncCaller.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    reloadFeed(feedClient);
                }
            });
        }
    }

    public static void showMoreItems(IFeedClient feedClient, boolean onNextPageDownloaded) {
        if (feedClient.isLoadingMore())
            return;
        feedClient.setIsLoadingMore(true);
        feedClient.clearTemplateControls();
        loadNextPage(feedClient, onNextPageDownloaded);
        reloadFeed(feedClient);
    }

    /**
     * Method called when pull to loadAssets action was executed. Checks screen hierarchy for views implementing IFeedClient,
     * creates new DownloadFeedCommand for each of them, and executes downloadFeed method.
     *
     * @param overlayDataDesc data descriptor of a current screen
     */
    public static void executePullToRefreshForOverlay(AbstractAGViewDataDesc overlayDataDesc) {
        ClearFormValuesVisitor visitor = new ClearFormValuesVisitor();
        overlayDataDesc.accept(visitor);
        ArrayList<IFeedClient> feedClients = getFeedClientsInsideDataDesc(overlayDataDesc);
        boolean isInternetConnection = NetworkUtils.isNetworkAvailable(AGApplicationState.getInstance().getContext());
        for (IFeedClient feedClient : feedClients) {
            executePullToRefresh(feedClient, isInternetConnection);
        }
    }

    public static void executePullToRefreshForScreen(AGScreenDataDesc screenDataDesc) {
        ClearFormValuesVisitor visitor = new ClearFormValuesVisitor();
        screenDataDesc.accept(visitor);
        ArrayList<IFeedClient> feedClients = screenDataDesc.getFeedClients();
        boolean isInternetConnection = NetworkUtils.isNetworkAvailable(AGApplicationState.getInstance().getContext());
        for (IFeedClient feedClient : feedClients) {
            executePullToRefresh(feedClient, isInternetConnection);
        }
    }

    private static void executePullToRefresh(IFeedClient feedClient, boolean isInternetConnection) {
        feedClient.setScreenHashCode(AGApplicationState.getInstance().getCurrentScreenDesc().hashCode());
        feedClient.setShouldRecreate(false);
        stopPreviousDownloadIfActive(feedClient);

        if (isInternetConnection) {
            feedClient.resetScrolls();
            initAndShowLoadingOnFeed(feedClient, true);
            CacheControlOptions cacheOption = CacheControlOptions.NO_CACHE;
            if (feedClient.getCachePolicyType().equals(AGFeedCachePolicyType.NO_STORE))
                cacheOption = CacheControlOptions.NO_STORE;

            prepareAndExecuteDownloadFeedCommand(feedClient, cacheOption, true, true);
        } else {
            PopupManager.showToast(LanguageManager.getInstance().getString(LanguageManager.ERROR_NO_CONNECTION));
            restoreFromCacheOrSetError(feedClient);
        }
    }

    private static void stopPreviousDownloadIfActive(IFeedClient datadesc) {
        if (datadesc.getDownloadCommad() != null)
            datadesc.getDownloadCommad().cancel();
    }

    /**
     * Creates appropriate headers and query params for downloadFeedCommand and downloads appropriate feed
     *
     * @param downloadFeedCommand feed command to be passed to AssetManager
     */
    public static void downloadFeed(DownloadFeedCommand downloadFeedCommand) {
        HttpParamsDataDesc headers = downloadFeedCommand.getFeedClient().getHeaders();
        HttpParamsDataDesc query = downloadFeedCommand.getFeedClient().getHttpParams();
        HttpParamsDataDesc localDBParams = downloadFeedCommand.getFeedClient().getLocalDBParamsDataDesc();
        downloadFeed(downloadFeedCommand, headers, query, localDBParams);
    }

    public static void downloadFeed(DownloadFeedCommand downloadFeedCommand, HttpParamsDataDesc headerParams, HttpParamsDataDesc queryParams, HttpParamsDataDesc localDBParams) {
        AssetsManager.ResultType type = AssetsManager.ResultType.FEEDXML;

        if (downloadFeedCommand.getUri().startsWith(AssetsManager.PREFIX_CONTROL)) {
            type = AssetsManager.ResultType.AGELEMENTDATADESC;
        }

        AssetsManager.getInstance().getAsset(downloadFeedCommand, type, headerParams, queryParams, localDBParams);
    }
    //endregion

    //region STATIC METHODS

    /**
     * After loading data to feedDescriptor, this metod makes feedClient (view descriptor) reflect changes done to
     * feedDescrior (loading new items etc)
     */
    public static void loadNextPage(IFeedClient feedClient, boolean onNextPageDownloaded) {
        DataFeed feedDescriptor = feedClient.getFeedDescriptor();
        if (feedDescriptor == null)
            return;
        int numberOfItemsInFeed = feedDescriptor.getItemsCount();

        if (numberOfItemsInFeed == 0) { //no data in feed - show NoDataTemplate
            showNoDataTemplate(feedClient);
        } else {
            String nextPageAddress = null;

            HttpParamsDataDesc httpParams = null;
            Pagination pagination = feedClient.getPagination();
            if (pagination != null) {
                if (pagination.getNextPageUrl() != null) {
                    nextPageAddress = feedDescriptor.getNextPageAddress();
                } else if (pagination.getNextPageToken() != null) {
                    String baseUrl = feedClient.getResolvedUrl();
                    String token = feedDescriptor.getNextPageAddress();
                    if (token != null) {
                        nextPageAddress = baseUrl;
                        httpParams = feedClient.getHttpParams().copy();
                        httpParams.addHttpParam(pagination.getNextPageToken().getParam(), feedDescriptor.getNextPageAddress());
                    }
                }
            }

            if (onNextPageDownloaded || feedClient.getLastItemIndex() < 0 || feedDescriptor.getItemsCount() - feedClient.getLastFeedItemCount() >= feedClient.getNumberItemsPerPage() || nextPageAddress == null) {
                //if loadNextPage is result of next page download task
                //or
                //feed is reset (on screen loading) or P2R
                //or
                //there is enough items in DataFeed (less then items per page) - !!! but without checking item templates !!! TODO
                //or
                //there is no pagination so we load all left items
                if (!(feedClient instanceof AGChartDataDesc)) {
                    int endIndex = createAndAddFeedControlsUsingTemplates(feedClient, feedDescriptor);
                    feedClient.setLastItemIndex(endIndex);
                    LoadMoreDataDesc loadMore = feedClient.getLoadMoreTemplate();
                    if (loadMore != null && (endIndex < numberOfItemsInFeed - 1 || feedDescriptor.getNextPageAddress() != null))
                        addTemplate(feedClient, loadMore);
                }
            } else {
                //load next page from assets
                feedClient.clearTemplateControls();
                addTemplate(feedClient, feedClient.getLoadingTemplate());
                String baseURL = getBaseAdress(feedClient);
                LoadMoreItemsTask task = new LoadMoreItemsTask(baseURL, feedClient, nextPageAddress, httpParams);
                ThreadPool.getInstance().execute(task);
            }
        }
    }

    private static String getBaseAdress(IFeedClient feedClient) {
        String baseURL = "";
        if (feedClient instanceof AbstractAGViewDataDesc)
            baseURL = ((AbstractAGViewDataDesc) feedClient).getFeedBaseAdress();
        return baseURL;
    }

    private static void showNoDataTemplate(IFeedClient feedClient) {
        NoDataDataDesc noData = feedClient.getNoDataTemplate();
        if (noData != null)
            addTemplate(feedClient, noData);
    }

    public static void addTemplate(IFeedClient feedClient, AbstractAGTemplateDataDesc template) {
        if (template != null) {
            List<AbstractAGElementDataDesc> copiedControls = feedClient.copyTemplateControls(template);
            for (AbstractAGElementDataDesc control : copiedControls) {
                control.resolveVariables();
                feedClient.addFeedClientControl(control);
                feedClient.addTemplateControl(control);
            }
        }
    }

    protected static int createAndAddFeedControlsUsingTemplates(IFeedClient feedClient, DataFeed feedDescriptor) {
        int startIndex = feedClient.getLastItemIndex() + 1;
        int lastFeedItemsCount = feedClient.getLastFeedItemCount();
        int numberOfItemsToAdd = 0;
        if (startIndex == 0 && lastFeedItemsCount > 0) { //when restoring screen's feed
            numberOfItemsToAdd = lastFeedItemsCount;
        }
        numberOfItemsToAdd = Math.max(numberOfItemsToAdd, feedClient.getNumberItemsPerPage());
        int numberOfItemsInFeed = feedDescriptor.getItemsCount();
        SetDescendantIdsAndIndexesVisitor idAndIndexVisitor = new SetDescendantIdsAndIndexesVisitor(feedClient);
        int index = startIndex;
        int itemsAdded = 0;
        while (index < numberOfItemsInFeed && itemsAdded < numberOfItemsToAdd) {
            DataFeedItem item = feedDescriptor.getItem(index);
            AGItemTemplateDataDesc template = feedClient.getMatchingTemplete(item);
            if (template != null) {
                idAndIndexVisitor.setItemData(feedClient.getResolvedUrl(), index, template.getTemplateNumber());
                List<AbstractAGElementDataDesc> copiedFeedControlsElements = feedClient.copyTemplateControls(template);
                for (AbstractAGElementDataDesc dataDesc : copiedFeedControlsElements) {
                    dataDesc.accept(idAndIndexVisitor);
                    dataDesc.resolveVariables();
                    feedClient.addFeedClientControl(dataDesc);
                }
                itemsAdded++;
            }
            ++index;
        }
        feedClient.setLastFeedItemCount(feedClient.getFeedClientControls().size());
        return --index;
    }
    //endregion

    public static void refreshAllFeeds() {
        ScrollManager.getInstance().resetScrollManager();
        AGApplicationState appState = AGApplicationState.getInstance();
        executePullToRefreshForScreen(appState.getCurrentScreenDesc());
    }

    public static void setFeed(final IFeedClient feedClient, final DataFeed result) {
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // process result only if app is running, else ignore
                if (feedClient != null) {
                    feedClient.setFeedDescriptor(result);
                    feedClient.resetFeed();
                    feedClient.clearTemplateControls();
                    FeedManager.loadNextPage(feedClient, false);
                    reloadFeed(feedClient);
                }
            }
        });
    }

    public static boolean isFeedChanged(DataFeed oldFeed, DataFeed newFeed) {
        if (oldFeed == null)
            return true;
        if (newFeed.equals(oldFeed))
            return false;
        else
            return true;
    }

    public static void reloadFeed(IFeedClient feedClient) {
        SystemDisplay systemDisplay = AGApplicationState.getInstance().getSystemDisplay();
        if (systemDisplay != null)
            systemDisplay.reloadFeed(feedClient);
        if (feedClient.shouldRecrete())
            feedClient.recreateFormData();
    }

    public static void addFeedToDataFeedsMap(IFeedClient feedClient, DataFeed result) {
        if (feedClient.getCachePolicyType().equals(AGFeedCachePolicyType.NO_STORE))
            return;
        DataFeedsMap.getInstance().addValue(feedClient.getResolvedUrl(), feedClient.getUsingFields(), feedClient.getHttpParams(), result);
    }

    public static void addMoreItemsToDataFeedsMap(IFeedClient feedClient, DataFeed result) {
        if (feedClient.getCachePolicyType().equals(AGFeedCachePolicyType.NO_STORE))
            return;
        DataFeed storedDataFeed = DataFeedsMap.getInstance().getDataFeed(feedClient);
        storedDataFeed.setNextPageAddress(result.getNextPageAddress());
        storedDataFeed.setTimestamp(result.getTimestamp());
        for (DataFeedItem item : result.getItems()) {
            storedDataFeed.addItem(item);
        }
        feedClient.setFeedDescriptor(storedDataFeed);
        DataFeedsMap.getInstance().addValue(feedClient.getResolvedUrl(), feedClient.getUsingFields(), feedClient.getHttpParams(), storedDataFeed);
    }

    public static void displayFreshData(IFeedClient feedClient, boolean isInternetConnection) {
        if (!isInternetConnection) {
            restoreFromCacheOrSetError(feedClient);
        } else {
            CacheControlOptions cacheOption = CacheControlOptions.NO_CACHE;
            if (feedClient.getCachePolicyType().equals(AGFeedCachePolicyType.NO_STORE))
                cacheOption = CacheControlOptions.NO_STORE;
            displayFromInternet(feedClient, cacheOption);
        }
    }

    private static void displayFromInternet(IFeedClient feedClient, CacheControlOptions cacheOption) {
        initAndShowLoadingOnFeed(feedClient, true);
        feedClient.setShouldRecreate(true);
        prepareAndExecuteDownloadFeedCommand(feedClient, cacheOption, true, true);
    }

    public static void displayFromInternetOrSetError(final IFeedClient feedClient) {
        displayFromInternet(feedClient, CacheControlOptions.NO_CACHE);
    }

    public static boolean tryRestoreFromCache(IFeedClient feedClient) {
        DataFeed dataFeed = DataFeedsMap.getInstance().getDataFeed(feedClient);
        if (dataFeed != null) {
            setFeed(feedClient, dataFeed);
            return true;
        } else {
            return false;
        }
    }

    public static void restoreFromCacheOrSetError(final IFeedClient feedClient) {
        restoreFromCacheOrSetError(200, feedClient);
    }

    public static void restoreFromCacheOrSetError(final int status, final IFeedClient feedClient) {
        if (status != 403 && status != 401 && tryRestoreFromCache(feedClient)) {
            PopupManager.showToast(LanguageManager.getInstance().getString(LanguageManager.ERROR_DATA_FROM_CACHE));
        } else {
            AsyncCaller.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    feedClient.showErrorTemplate();
                }
            });
        }
    }

    public static void saveFeedsDataOfAllFeedsInside(AbstractAGElementDataDesc desc) {
        ArrayList<IFeedClient> feedClients = getFeedClientsInsideDataDesc(desc);
        for (IFeedClient feedClient : feedClients) {
            feedClient.saveFormData();
        }
    }

}
