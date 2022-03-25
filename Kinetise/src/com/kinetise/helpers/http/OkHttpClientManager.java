package com.kinetise.helpers.http;

import android.content.Context;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.alterapimanager.AGOkHttpConfigurator;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class OkHttpClientManager {

    public static final int CACHE_SIZE = 100 * 1024 * 1024; //in bytes

    private static OkHttpClientManager mInstance;
    private OkHttpClient mClient;

    public static OkHttpClientManager getInstance() {
        if (mInstance ==null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null)
                    mInstance = new OkHttpClientManager(AGApplicationState.getInstance().getContext());
            }
        }
        return mInstance;
    }

    private OkHttpClientManager(){}

    private OkHttpClientManager(Context context) {
        try {
            mClient = AGOkHttpConfigurator.configureOkHttpClient();
            initCache(new File(context.getCacheDir().getAbsolutePath(), "okHttpCache"), CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        try {
            mClient.getCache().evictAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearInstance(){
        mInstance = null;
    }

    public void initCache(File cacheFile, int cacheSize) {
        Cache cache = new Cache(cacheFile, cacheSize);
        mClient.setCache(cache);
    }

    public void removeFromCache(String url) {
        try {
            Iterator<String> iterator = mClient.getCache().urls();
            while (iterator.hasNext()) {
                if (iterator.next().equals(url)) {
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OkHttpClient getClient() {
        return mClient;
    }

}

