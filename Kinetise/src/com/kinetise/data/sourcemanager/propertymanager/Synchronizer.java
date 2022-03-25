package com.kinetise.data.sourcemanager.propertymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.helpers.http.HttpRequestManager;
import com.kinetise.helpers.http.NetworkUtils;
import com.kinetise.helpers.preferences.SecurePreferencesHelper;
import com.kinetise.helpers.threading.ThreadPool;

import java.util.LinkedHashMap;
import java.util.Map;

public class Synchronizer {
    public static final String SHARED_PREFERENCES_SYNCHRONIZER = "sharedSynchronizer";

    public static final long ERROR_DELAY = 30 * 1000; //30 seconds

    protected static Synchronizer mInstance;

    protected LinkedHashMap<String, SynchronizeProperty> mQueue;
    protected boolean mLocked;
    protected NetworkReceiver mNetworkReceiver;

    public static Synchronizer getInstance() {
        if (mInstance == null) {

            synchronized (Synchronizer.class){
                if (mInstance == null) {
                    restoreSynchronizer();
                    if (mInstance.mQueue==null)
                        mInstance.mQueue = new LinkedHashMap<String, SynchronizeProperty>();
                    mInstance.mLocked = false;
                    mInstance.synchronize();
                }
            }
        }
        return mInstance;
    }

    protected Synchronizer() { }

    public static void clearInstance(){
        mInstance = null;
    }

    /**
     * Restores Synchronizer queue from persistent storage or creates empty map.
     *
     */
    public static void restoreSynchronizer() {
        //read from persistant storage
        SharedPreferences preferences = SecurePreferencesHelper.getUserData();
        String synchronizerJson = preferences.getString(Synchronizer.SHARED_PREFERENCES_SYNCHRONIZER, null);
        if (synchronizerJson != null) {
            mInstance = new Gson().fromJson(synchronizerJson, Synchronizer.class);
        }
        if (mInstance==null) {
            mInstance = new Synchronizer();
        }
    }

    /**
     * Saves Synchronizer queue to the persistent storage. It can be then restored on app start. Only if synchronizer exists (was used)
     */
    public static void serialize() {
        if (mInstance!=null) {
            mInstance.unregisterNetworkReceiver();
            String sychronizerJson = new Gson().toJson(mInstance);
            SharedPreferences.Editor editor = SecurePreferencesHelper.getUserData().edit();
            editor.putString(SHARED_PREFERENCES_SYNCHRONIZER, sychronizerJson).apply();
        }
    }

    public void sendRequest(String key, String value, AGHttpMethodType methodType, String url, Map<String, String> headerParams, String postParams, String responseTransform) {
        addToPropertyStorage(key, value);
        addToSynchronizerQueue(key, value,methodType, url, headerParams, postParams, responseTransform);
        synchronize();
    }

    protected void addToPropertyStorage(String key, String value) {
        addToPropertyStorage(key, value, Long.MAX_VALUE);
    }

    protected void addToPropertyStorage(String key, String value, long timestamp) {
        Property property = new Property(value, timestamp);
        PropertyStorage.getInstance().addValue(key, property);
    }

    protected void addToSynchronizerQueue(String key, String value, AGHttpMethodType methodType, String url, Map<String, String> headerParams, String postParams, String responseTransform) {
        SynchronizeProperty synchronizeProperty = new SynchronizeProperty(key, value, methodType, url, headerParams, postParams, responseTransform);
        mQueue.put(key, synchronizeProperty);
    }

    protected void synchronize() {
        if (!mLocked && mQueue.size() > 0) {
            //if internet is available
            if (AGApplicationState.getInstance() != null && NetworkUtils.isNetworkAvailable(AGApplicationState.getInstance().getContext())) {
                SynchronizeProperty synchronizeProperty = getFirstElement(mQueue);
                SynchronizePropertyRunnable runnable = new SynchronizePropertyRunnable(synchronizeProperty.getAGHttpMethodType(), synchronizeProperty.getUrl(), synchronizeProperty.getPostBody(), synchronizeProperty.getHeaderParams(), synchronizeProperty.getResponseTransform(), new SynchronizePropertyCallback(synchronizeProperty.getKey(), synchronizeProperty.getValue()));
                mLocked = true;
                ThreadPool.getInstance().executeBackground(runnable);
            } else {
                registerNetworkReceiver();
            }
        }
    }

    private void registerNetworkReceiver() {
        if (mNetworkReceiver == null && AGApplicationState.getInstance().getActivity() != null) {
            final IntentFilter filters = new IntentFilter();
            filters.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            mNetworkReceiver = new NetworkReceiver();
            AGApplicationState.getInstance().getActivity().registerReceiver(mNetworkReceiver, filters);
        }
    }

    private void unregisterNetworkReceiver() {
        if (mNetworkReceiver != null && AGApplicationState.getInstance() != null) {
            AGApplicationState.getInstance().getActivity().unregisterReceiver(mNetworkReceiver);
            mNetworkReceiver = null;
        }
    }

    public static SynchronizeProperty getFirstElement(LinkedHashMap<String, SynchronizeProperty> map) {
        return map.entrySet().iterator().next().getValue();
    }

    private void omitFirst(String key) {
        mQueue.put(key, mQueue.remove(key));
        sleep();
    }

    private void sleep() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(ERROR_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mLocked = false;
                synchronize();
            }
        }.start();
    }

    public class SynchronizePropertyCallback {
        private String mKey;
        private String mValue;

        public SynchronizePropertyCallback(String key, String value) {
            this.mKey = key;
            this.mValue = value;
        }

        public void onPropertySynchronized(int statusCode, long timestamp) {
            if (HttpRequestManager.httpStatusToResultCode(statusCode) == HttpRequestManager.HttpResponseResultCode.OK) {
                SynchronizeProperty currentSynchronizeProperty = mQueue.get(mKey);
                if ((currentSynchronizeProperty.getValue()==null && mValue==null) ||
                        currentSynchronizeProperty.getValue().equals(mValue)) { //only if property hasn't changed (both null or same String value)
                    addToPropertyStorage(mKey, mValue, timestamp);
                    mQueue.remove(mKey);
                }
                mLocked = false;
                synchronize();
            } else {
                omitFirst(mKey);
            }
        }

        public void onCancel() {
            mLocked = false;
            synchronize();
        }

        public void onSynchronizeException() {
            omitFirst(mKey);
        }

    }

    protected class SynchronizeProperty {
        private final AGHttpMethodType mAGHttpMethodType;
        private final String mResponseTransform;
        private String mKey;
        private String mValue;
        private String mUrl;
        private Map<String, String> mHeaderParams;
        private String mPostBody;

        public SynchronizeProperty(String key, String value, AGHttpMethodType methodType, String url, Map<String, String> headerParams, String postBody, String responseTransform) {
            this.mKey = key;
            this.mValue = value;
            this.mPostBody = postBody;
            this.mHeaderParams = headerParams;
            this.mUrl = url;
            this.mAGHttpMethodType = methodType;
            this.mResponseTransform = responseTransform;
        }

        public String getKey() {
            return mKey;
        }

        public String getValue() {
            return mValue;
        }

        public String getUrl() {
            return mUrl;
        }

        public String getPostBody() {
            return mPostBody;
        }

        public Map<String, String> getHeaderParams() {
            return mHeaderParams;
        }

        public String getResponseTransform() {
            return mResponseTransform;
        }

        public AGHttpMethodType getAGHttpMethodType() {
            return mAGHttpMethodType;
        }
    }

    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtils.isNetworkAvailable(context)) {
                unregisterNetworkReceiver();
                synchronize();
            }
        }
    }

    //for tests
    public LinkedHashMap<String, SynchronizeProperty> getQueue() {
        return mQueue;
    }

    public void clearQueue() {
        mQueue.clear();
    }
}
