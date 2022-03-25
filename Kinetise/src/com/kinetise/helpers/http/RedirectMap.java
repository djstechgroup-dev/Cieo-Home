package com.kinetise.helpers.http;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.kinetise.helpers.preferences.SecurePreferencesHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RedirectMap {
    public static final String REDIRECT_MAP_STRING_NAME = "RedirectMap";

    private static RedirectMap mInstance;

    @Expose
    private HashMap<String, String> mRedirectMap;

    private RedirectMap() {
        mRedirectMap = new HashMap<>();
    }

    public static RedirectMap getInstance() {
        if (mInstance == null) {
            synchronized (RedirectMap.class) {
                if (mInstance == null) {
                    mInstance = new RedirectMap();
                }
            }
        }
        return mInstance;
    }

    public static void clearInstance() {
        mInstance = null;
    }

    public void clear() {
        mRedirectMap = new HashMap<>();
    }

    public synchronized void remove(List<String> expiredUrls) {
        for (String url : expiredUrls) {
            remove(url);
        }
    }

    public void remove(String url) {
        synchronized (mRedirectMap) {
            Map.Entry<String, String> entry;
            Iterator<Map.Entry<String, String>> iter = mRedirectMap.entrySet().iterator();
            while (iter.hasNext()) {
                entry = iter.next();
                if (entry.getKey().startsWith(url) || entry.getValue().startsWith(url))
                    iter.remove();
            }
        }
    }

    public void addRedirect(String url, String redirectedUrl) {
        synchronized (mRedirectMap) {
            mRedirectMap.put(url, redirectedUrl);
        }
    }

    public synchronized String getRedirect(String url) {
        synchronized (mRedirectMap) {
            return mRedirectMap.get(url);
        }
    }

    public List<String> getBaseRedirectUrls(String url) {
        synchronized (mRedirectMap) {
            List<String> baseUrls = new ArrayList<String>();
            Map.Entry<String, String> entry;
            Iterator<Map.Entry<String, String>> iter = mRedirectMap.entrySet().iterator();
            while (iter.hasNext()) {
                entry = iter.next();
                if (entry.getValue().startsWith(url))
                    baseUrls.add(entry.getKey());
            }
            return baseUrls;
        }
    }

    public boolean isCached(String url) {
        return getRedirect(url) != null;
    }

    public static void serialize() {
        if (mInstance != null) {
            synchronized (mInstance.mRedirectMap) {
                Type type = new TypeToken<HashMap<String, String>>() {
                }.getType();
                Gson gson = new Gson();
                String redirectMapJson = gson.toJson(mInstance.mRedirectMap, type);
                SharedPreferences preferencesFile = SecurePreferencesHelper.getUserData();
                SharedPreferences.Editor editor = preferencesFile.edit();
                editor.putString(REDIRECT_MAP_STRING_NAME, redirectMapJson);
                editor.apply();
            }
        }
    }

    public void restoreRedirectMap() {
        getInstance();
        SharedPreferences preferencesFile = SecurePreferencesHelper.getUserData();
        String serializedMap = preferencesFile.getString(REDIRECT_MAP_STRING_NAME, null);

        if (serializedMap != null) {
            synchronized (mRedirectMap) {
                Type type = new TypeToken<HashMap<String, String>>() {
                }.getType();
                try {
                    HashMap map = new Gson().fromJson(serializedMap, type);
                    if (map != null)
                        mRedirectMap = map;
                } catch (Exception e) { //IllegalStateException or RuntimeException
                    e.printStackTrace();
                }
            }
        }
    }
}
