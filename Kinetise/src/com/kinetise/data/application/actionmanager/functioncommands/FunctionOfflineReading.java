package com.kinetise.data.application.actionmanager.functioncommands;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.alterapimanager.AAResponse;
import com.kinetise.data.application.alterapimanager.AGOkHttpConfigurator;
import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.application.feedmanager.DataFeedsMap;
import com.kinetise.data.application.feedmanager.DownloadFeedCommand;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Field;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.NamespaceElement;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Namespaces;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.UsingFields;
import com.kinetise.data.descriptors.types.AGFeedFormatType;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGHttpXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.helpers.AndroidBitmapDecoder;
import com.kinetise.helpers.asynccaller.AsyncCaller;
import com.kinetise.helpers.http.HttpRequestManager;
import com.kinetise.helpers.http.NetworkUtils;
import com.kinetise.helpers.http.OkHttpClientManager;
import com.kinetise.helpers.http.RedirectMap;
import com.kinetise.helpers.http.exceptions.DownloadException;
import com.kinetise.helpers.regexp.RegexpHelper;
import com.kinetise.helpers.threading.AGAsyncTask;
import com.kinetise.helpers.threading.ThreadPool;
import com.squareup.okhttp.Request;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FunctionOfflineReading extends AbstractFunction {

    public static final String TYPE_IMAGE = "IMAGE";

    private int mProgress;
    private String mJSON;
    List<DownloadImageItem> mImages;
    Set<DownloadedItem> mDownloadedItems;
    boolean mSuccess;

    public FunctionOfflineReading(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        parseAttributes();

        SystemDisplay display = AGApplicationState.getInstance().getSystemDisplay();
        if (display == null)
            return null;

        if (NetworkUtils.isNetworkAvailable(AGApplicationState.getInstance().getContext())) {
            PopupManager.showProgressDialog(LanguageManager.getInstance().getString(LanguageManager.DOWNLOADING_FILE));

            OfflineReadingTask offlineReadingTask = new OfflineReadingTask();
            ThreadPool.getInstance().execute(offlineReadingTask);
        } else {
            PopupManager.showErrorPopup(LanguageManager.getInstance().getString(LanguageManager.ERROR_NO_CONNECTION));
        }


        return null;
    }

    private void parseAttributes() {
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        attributes[0].resolveVariable();
        mJSON = AGXmlActionParser.unescape(attributes[0].getStringValue());
    }

    private class OfflineReadingTask extends AGAsyncTask {
        @Override
        public void run() {
            //init queue (arraylist) for images links
            mImages = new ArrayList<>();
            mDownloadedItems = new HashSet<>();
            mSuccess = true;
            //parse JSON
            Gson gson = new Gson();
            List<OfflineReadingItem> items = gson.fromJson(mJSON, new TypeToken<List<OfflineReadingItem>>() {
            }.getType());

            //for each entry create HttpTask, download feed and add all images to images queue
            if (items != null)
                downloadItems(items, null);

            //calculate images count for progress dialog max value
            int imagesCount = calculateImagesCount(mImages);
            if (imagesCount > 0) {
                PopupManager.showProgressDialog(LanguageManager.getInstance().getString(LanguageManager.DOWNLOADING_FILE), imagesCount);
            }

            //download images (updating progress dialog after each image download)
            downloadImages(mImages);

            //at the end dismiss progress dialog from screen
            PopupManager.dismissDialog();
            showPopupMessage();
        }

    }

    private void downloadItems(List<OfflineReadingItem> items, DataFeed dataFeed) {
        for (OfflineReadingItem item : items) {
            downloadItems(item, dataFeed);
        }
    }

    private void downloadItems(OfflineReadingItem item, DataFeed dataFeed) {
        //use set to confirm uniqueness of urls (do not download the same url with same headers and params multiple time)
        Set<String> feedUrls = new HashSet<>();
        Set<String> imageUrls = new HashSet<>();

        HttpParamsDataDesc headerParams = HttpParamsDataDesc.getHttpParams(item.headerParams, null);
        HttpParamsDataDesc httpParams = HttpParamsDataDesc.getHttpParams(item.httpParams, null);
        headerParams.resolveVariables();
        httpParams.resolveVariables();

        if (item.httpUrl != null) { //root nodes with explicit url
            feedUrls.add(item.httpUrl);
        } else {
            for (DataFeedItem dataFeedItem : dataFeed.getItems()) {
                String url = dataFeedItem.getByKey(item.usingFieldInParent).toString();
                if (item.dataType.equals(TYPE_IMAGE)) {
                    url = RegexpHelper.parseValue(RegexpHelper.OPTIMIZED_RULE_CONTROLIMAGE, url);
                    imageUrls.add(url);
                } else {
                    url = RegexpHelper.parseValue(RegexpHelper.OPTIMIZED_RULE_URL, url);
                    feedUrls.add(url);
                }
            }
        }

        //add images to global images queue (in fact keep reference to item and resolved headers/params)
        if (imageUrls.size() > 0)
            addImagesToDownloadQueue(item, imageUrls, headerParams, httpParams);
        //download feeds
        downloadItems(item, feedUrls, headerParams, httpParams);
    }

    private void downloadItems(OfflineReadingItem item, Set<String> urls, HttpParamsDataDesc headerParams, HttpParamsDataDesc httpParams) {
        for (String url : urls) {
            VariableDataDesc urlVariable = AGXmlActionParser.createVariable(url, mFunctionDataDesc.getContextDataDesc());
            urlVariable.resolveVariable();

            String urlString = urlVariable.getStringValue();
            if (isItemAlreadyDownloaded(headerParams, httpParams, urlString))
                continue;
            downloadItem(item, headerParams, httpParams, urlString);
        }
    }

    private boolean isItemAlreadyDownloaded(HttpParamsDataDesc headerParams, HttpParamsDataDesc httpParams, String url) {
        return mDownloadedItems.contains(new DownloadedItem(url, headerParams, httpParams));
    }

    private void downloadItem(OfflineReadingItem item, HttpParamsDataDesc headerParams, HttpParamsDataDesc httpParams, String url) {
        String urlWithParams = AssetsManager.addHttpQueryParams(url, httpParams);

        if (url.equals(""))
            return;

        HttpRequestManager requestManager = new HttpRequestManager();

        DataFeed feed = null;

        try {
            Request.Builder builder;
            if (item.httpMethod.equals(AGHttpXmlAttributes.HTTP_METHOD_GET)) {
                builder = AGOkHttpConfigurator.configureOkHttpRequestForGet(urlWithParams, headerParams.getHttpParamsAsHashMap());
            } else {
                builder = AGOkHttpConfigurator.configureOkHttpRequestForPost(urlWithParams, headerParams.getHttpParamsAsHashMap());
                AGHttpMethodType method;
                switch (item.httpMethod) {
                    case AGHttpXmlAttributes.HTTP_METHOD_PUT:
                        method = AGHttpMethodType.PUT;
                        break;
                    case AGHttpXmlAttributes.HTTP_METHOD_DELETE:
                        method = AGHttpMethodType.DELETE;
                        break;
                    case AGHttpXmlAttributes.HTTP_METHOD_PATCH:
                        method = AGHttpMethodType.PATCH;
                        break;
                    case AGHttpXmlAttributes.HTTP_METHOD_POST:
                    default:
                        method = AGHttpMethodType.POST;
                        break;
                }
                AGOkHttpConfigurator.setPost(builder, method, HttpRequestManager.RequestType.SILENT, item.httpBody);
            }
            requestManager.executeRequest(OkHttpClientManager.getInstance().getClient(), builder);

            int statusCode = requestManager.getStatusCode();

            String redirectedUrl = requestManager.getResponseUrl();
            if (redirectedUrl != null)
                RedirectMap.getInstance().addRedirect(url, redirectedUrl);

            if (HttpRequestManager.httpStatusToResultCode(statusCode) == HttpRequestManager.HttpResponseResultCode.OK) {
                if (item.dataType.equals(TYPE_IMAGE)) {
                    //for now for bitmaps its only http cache so we have to read stream
                    //IOUtils.readFully(requestManager.getContent(), new byte[AndroidBitmapDecoder.BUFFER_SIZE]);
                    InputStream is = requestManager.getContent();
                    byte[] contents = new byte[AndroidBitmapDecoder.BUFFER_SIZE];
                    while (is.read(contents) != -1) {
                    }
                    is.close();
                } else { //XML|JSON
                    UsingFields usingFields = getUsingFields(item.usingFields);
                    UsingFields cachedFields = DataFeedsMap.getInstance().getCachedUsingFields(url, httpParams);
                    UsingFields combinedFields = UsingFields.combine(usingFields, cachedFields);

                    feed = parseFeed(requestManager, item, combinedFields, null);
                    addFeedToDataFeedMap(httpParams, url, feed, combinedFields);
                }
                mDownloadedItems.add(new DownloadedItem(url, headerParams, httpParams));
            } else {
                mSuccess = false;
                //expired urls handling
                AAResponse response = requestManager.getAlterApiResponse();
                AlterApiManager.handleAAResponse(response);
                return;
            }
        } catch (DownloadException | IOException e) {
            e.printStackTrace();
            mSuccess = false;
        }

        //if correctly parsed download child nodes
        if (feed != null)
            downloadItems(item.nodes, feed);
    }

    private void addFeedToDataFeedMap(final HttpParamsDataDesc httpParams, final String url, final DataFeed feed, final UsingFields usingFields) {
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataFeedsMap.getInstance().addValue(url, usingFields, httpParams, feed);
            }
        });
    }

    private void downloadImages(List<DownloadImageItem> items) {
        for (DownloadImageItem item : items) {
            for (String url : item.imageUrls) {
                VariableDataDesc urlVariable = AGXmlActionParser.createVariable(url, mFunctionDataDesc.getContextDataDesc());
                urlVariable.resolveVariable();
                downloadItem(item.item, item.headerParams, item.headerParams, urlVariable.getStringValue());
                PopupManager.updateProgressDialog(++mProgress);
            }
        }
    }

    private void addImagesToDownloadQueue(OfflineReadingItem item, Set<String> imageUrls, HttpParamsDataDesc headerParams, HttpParamsDataDesc httpParams) {
        DownloadImageItem downloadImageItem = new DownloadImageItem(item, imageUrls, headerParams, httpParams);
        mImages.add(downloadImageItem);
    }

    private void showPopupMessage() {
        if (mSuccess)
            PopupManager.showAlert(LanguageManager.getInstance().getString(LanguageManager.OFFLINE_READING_SUCCEED_DESCRIPTION),
                    LanguageManager.getInstance().getString(LanguageManager.OFFLINE_READING_SUCCEED_TITLE));
        else
            PopupManager.showAlert(LanguageManager.getInstance().getString(LanguageManager.OFFLINE_READING_FAILED_DESCRIPTION),
                    LanguageManager.getInstance().getString(LanguageManager.OFFLINE_READING_FAILED_TITLE));
    }

    private DataFeed parseFeed(HttpRequestManager requestManager, OfflineReadingItem item, UsingFields usingFields, String nextPagePath) {
        BufferedInputStream bis = DownloadFeedCommand.getBufferedInputStream(requestManager.getContent());
        if (bis == null) {
            return null;
        }

        AGFeedFormatType formatType = AGFeedFormatType.getFormatType(item.dataType);
        Namespaces namespaces = getNamespaces(item.xmlns);

        DataFeed feed = null;
        try {
            feed = DownloadFeedCommand.parseFeed(bis, formatType, namespaces, item.itemPath, usingFields, nextPagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return feed;
    }

    @NonNull
    private UsingFields getUsingFields(Map<String, String> itemUsingFields) {
        UsingFields usingFields = new UsingFields();
        Map.Entry<String, String> entry;
        Iterator<Map.Entry<String, String>> iter = itemUsingFields.entrySet().iterator();
        while (iter.hasNext()) {
            entry = iter.next();
            usingFields.addField(new Field(entry.getKey(), entry.getValue()));
        }
        return usingFields;
    }

    @NonNull
    private Namespaces getNamespaces(Map<String, String> itemNamespaces) {
        Namespaces namespaces = new Namespaces();
        Map.Entry<String, String> entry;
        Iterator<Map.Entry<String, String>> iter = itemNamespaces.entrySet().iterator();
        while (iter.hasNext()) {
            entry = iter.next();
            namespaces.add(new NamespaceElement(entry.getKey(), entry.getValue()));
        }
        return namespaces;
    }

    private static int calculateImagesCount(List<DownloadImageItem> items) {
        int count = 0;
        for (DownloadImageItem item : items) {
            count += item.imageUrls.size();
        }
        return count;
    }

    private class DownloadedItem {
        String url;
        HttpParamsDataDesc headerParams;
        HttpParamsDataDesc httpParams;

        DownloadedItem(String url, HttpParamsDataDesc headerParams, HttpParamsDataDesc httpParams) {
            this.url = url;
            this.headerParams = headerParams;
            this.httpParams = httpParams;
        }

        @Override
        public boolean equals(Object o) {
            DownloadedItem compared = (DownloadedItem) o;
            return compared.url.equals(this.url)
                    && compared.httpParams.equals(this.httpParams)
                    && compared.headerParams.equals(this.headerParams);
        }

        @Override
        public int hashCode() {
            return url.hashCode();
        }
    }

    private class DownloadImageItem {
        OfflineReadingItem item;
        Set<String> imageUrls;
        HttpParamsDataDesc headerParams;
        HttpParamsDataDesc httpParams;

        DownloadImageItem(OfflineReadingItem item, Set<String> imageUrls, HttpParamsDataDesc headerParams, HttpParamsDataDesc httpParams) {
            this.item = item;
            this.imageUrls = imageUrls;
            this.headerParams = headerParams;
            this.httpParams = httpParams;
        }
    }

    private class OfflineReadingItem implements Serializable {
        String usingFieldInParent;
        String httpUrl;
        Map<String, String> xmlns;
        String itemPath;
        Map<String, String> usingFields;
        Map<String, String> httpParams;
        Map<String, String> headerParams;
        String httpMethod;
        String httpBody;
        String dataType;
        List<OfflineReadingItem> nodes;
    }
}

