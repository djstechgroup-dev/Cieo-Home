package com.kinetise.data.application.feedmanager;

import android.content.SharedPreferences;
import android.util.Pair;

import com.google.gson.Gson;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.HttpParamsElementDataDesc;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Field;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.UsingFields;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.helpers.http.RedirectMap;
import com.kinetise.helpers.preferences.SecurePreferencesHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataFeedsMap {

    private static DataFeedsMap mInstance;

    protected List<CachedDataFeed> mData = new ArrayList<>();

    private DataFeedsMap() {
        mData = new ArrayList<>();
    }

    public static DataFeedsMap getInstance() {
        if (mInstance == null) {
            synchronized (DataFeedsMap.class) {
                if (mInstance == null) {
                    mInstance = new DataFeedsMap();
                    mInstance.restoreDataFeedsMap();
                }
            }
        }
        return mInstance;
    }

    public static void clearInstance() {
        mInstance = null;
    }

    public void clear() {
        mData = new ArrayList<>();
    }

    public static void serialize() {
        if (mInstance != null) {
            synchronized (mInstance.mData) {
                SharedPreferences.Editor editor = SecurePreferencesHelper.getDataFeeds().edit();
                editor.clear();

                for (CachedDataFeed cachedFeed : mInstance.mData) {
                    String filename = UUID.randomUUID().toString();
                    try {
                        String feedJson = new Gson().toJson(cachedFeed);
                        editor.putString(filename, feedJson);
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }
                }
                editor.apply();
            }
        }
    }

    public void restoreDataFeedsMap() {
        synchronized (mData) {
            SharedPreferences preferences = SecurePreferencesHelper.getDataFeeds();
            Map<String, ?> feedsMap = preferences.getAll();
            Gson gson = new Gson();
            for (Object value : feedsMap.values()) {
                CachedDataFeed feed = gson.fromJson(value.toString(), CachedDataFeed.class);
                mData.add(feed);
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
        }
    }

    public void addValue(String url, UsingFields usingFields, HttpParamsDataDesc httpParams, DataFeed dataFeed) {
        if (url.startsWith("http") || url.startsWith("local")) {
            CachedDataFeed cachedDataFeed = new CachedDataFeed(url, usingFields, httpParams, dataFeed);
            synchronized (mData) {
                mData.remove(cachedDataFeed);
                mData.add(cachedDataFeed);
            }
        }
    }

    public void remove(List<String> expiredUrls) {
        for (String url : expiredUrls) {
            removeValue(url);
            String redirectUrl = RedirectMap.getInstance().getRedirect(url);
            if (redirectUrl!=null)
                removeValue(redirectUrl);
            List<String> baseUrls = RedirectMap.getInstance().getBaseRedirectUrls(url);
            for (String baseUrl : baseUrls) {
                removeValue(baseUrl);
            }
        }
    }

    public void removeValue(String url) {
        synchronized (mData) {
            for (Iterator<CachedDataFeed> iterator = mData.iterator(); iterator.hasNext(); ) {
                CachedDataFeed cachedDataFeed = iterator.next();
                if (cachedDataFeed.url.startsWith(url)) {
                    iterator.remove();
                }
            }
        }
    }

    private CachedDataFeed getValue(String url, HttpParamsDataDesc httpParams) {
        List<Pair<String, String>> params = parseHttpParams(httpParams);
        synchronized (mData) {
            for (CachedDataFeed feed : mData) {
                if (feed.url.equals(url) && compareHttpParams(params, feed.httpParams)) {
                    return feed;
                }
            }
        }
        return null;
    }

    public UsingFields getCachedUsingFields(IFeedClient feedClient) {
        return getCachedUsingFields(feedClient.getResolvedUrl(), feedClient.getHttpParams());
    }

    public UsingFields getCachedUsingFields(String url, HttpParamsDataDesc httpParams) {
        CachedDataFeed storedDataFeed = getValue(url, httpParams);

        if (storedDataFeed != null) {
            return storedDataFeed.usingFields;
        }
        return null;
    }

    public DataFeed getDataFeed(IFeedClient feedClient) {
        String uri = AssetsManager.resolveURI(feedClient.getStringSource(), ((AbstractAGViewDataDesc) feedClient).getFeedBaseAdress());

        CachedDataFeed storedDataFeed = getValue(uri, feedClient.getHttpParams());

        if (storedDataFeed != null && hasAllRequiredFields(feedClient.getUsingFields(), storedDataFeed)) {
            return storedDataFeed.dataFeed;
        }
        return null;
    }


    private boolean hasAllRequiredFields(UsingFields requiredUsingFields, CachedDataFeed storedDataFeed) {
        List<Field> cachedUsingFields = storedDataFeed.usingFields.getFields();
        for (Field field : requiredUsingFields.getFields()) {
            if (!cachedUsingFields.contains(field)) {
                return false;
            }
        }
        return true;
    }

    public long getDataFeedTimestamp(IFeedClient feedClient) {
        CachedDataFeed storedDataFeed = getValue(feedClient.getResolvedUrl(), feedClient.getHttpParams());

        if (storedDataFeed != null && hasAllRequiredFields(feedClient.getUsingFields(), storedDataFeed)) {
            return storedDataFeed.timestamp;
        } else {
            return -1;
        }
    }

    private static List<Pair<String, String>> parseHttpParams(HttpParamsDataDesc httpParams) {
        List<Pair<String, String>> httpParamsList = new ArrayList<>();
        for (HttpParamsElementDataDesc element : httpParams.getHttpParamsElementDataDescs()) {
            httpParamsList.add(new Pair<>(element.getParamName(), element.getParamValue().getStringValue()));
        }
        return httpParamsList;
    }

    public boolean compareHttpParams(List<Pair<String, String>> params1, List<Pair<String, String>> params2) {
        if (params1.size() != params2.size())
            return false;

        for (Pair<String, String> element : params1) {
            if (!params2.contains(element))
                return false;
        }
        return true;
    }

    public class CachedDataFeed implements Serializable {

        private String url;
        private DataFeed dataFeed;
        private UsingFields usingFields;
        private List<Pair<String, String>> httpParams;
        private long timestamp;

        public CachedDataFeed(String url, UsingFields usingFields, HttpParamsDataDesc httpParams, DataFeed dataFeed) {
            this.url = url;
            this.dataFeed = dataFeed;
            this.usingFields = usingFields;
            this.httpParams = parseHttpParams(httpParams);
            this.timestamp = new Date().getTime();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CachedDataFeed)) {
                return false;
            }
            CachedDataFeed compared = (CachedDataFeed) o;
            if (!this.url.equals(compared.url))
                return false;

            if (!compareHttpParams(httpParams, compared.httpParams))
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            int hashCode = url.hashCode();
            for (Pair<String, String> httpParam : httpParams) {
                hashCode += httpParam.first.hashCode() + httpParam.second.hashCode();
            }
            return hashCode;
        }
    }

}
