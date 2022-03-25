package com.kinetise.components.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kinetise.data.application.formdatautils.FormData;
import com.kinetise.data.application.formdatautils.FormFormaterV3;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.location.LocationUpdate;
import com.kinetise.data.location.LocationUpdateParams;
import com.kinetise.data.location.LocationUpdateSession;
import com.kinetise.data.sourcemanager.RealmManager;
import com.kinetise.helpers.http.HttpRequestManager;
import com.kinetise.helpers.http.NetworkUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationUploadService extends Service {

    public static final int LOCATIONS_LIMIT = 4;
    public static final long FAIL_DELAY = 10 * 1000;

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ALTITUDE = "altitude";
    public static final String TIMESTAMP = "timestamp";
    public static final String ACCURACY = "accuracy";

    private boolean mIsUploading;
    private Handler mHandler;
    private NetworkReceiver mNetworkReceiver;

    public static Intent createUploadIntent(Context context) {
        return new Intent(context, LocationUploadService.class);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mIsUploading) {
            mIsUploading = true;
            mHandler = new Handler(getMainLooper());
            startLocationUpload();
        }
        return START_STICKY;
    }

    private void stopService() {
        RealmManager.getInstance().deleteInactiveSessions();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterConnectivityReceiver();
    }

    private void registerNetworkReceiver() {
        if (mNetworkReceiver == null) {
            mNetworkReceiver = new NetworkReceiver();
            final IntentFilter filters = new IntentFilter();
            filters.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(mNetworkReceiver, filters);
        }
    }

    private void unregisterConnectivityReceiver() {
        if (mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver);
            mNetworkReceiver = null;
        }
    }

    private void startLocationUpload() {
        final List<LocationUpdate> locations = RealmManager.getInstance().getFirstLocationUpdates(LOCATIONS_LIMIT);
        if (locations == null || locations.size() == 0) {
            mIsUploading = false;
            stopService();
        } else {
            if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                LocationUpdate first = locations.get(0);
                LocationUpdateSession session = RealmManager.getInstance().getLocationUpdatesSession(first.getSessionId());
                String json = createLocationJson(locations).toString();
                Map<String, String> headerParams = LocationUpdateParams.getHttpParamsAsMap(session.getHeaderParams());

                UpdateLocationThread task = new UpdateLocationThread(session.getUrl(), headerParams, locations, json);
                task.start();
            } else {
                mIsUploading = false;
                registerNetworkReceiver();
            }
        }
    }

    @NonNull
    private JsonArray createLocationJson(final List<LocationUpdate> locations) {
        JsonArray jsonArray = new JsonArray();
        for (LocationUpdate locationUpdate : locations) {
            double latitude = locationUpdate.getLatitude();
            double longitude = locationUpdate.getLongitude();
            double altitude = locationUpdate.getAltitude();
            float accuracy = locationUpdate.getAccuracy();
            long timestamp = locationUpdate.getTimestamp();

            Map<String, String> params = new HashMap<>();
            params.put(LATITUDE, Double.toString(latitude));
            params.put(LONGITUDE, Double.toString(longitude));
            params.put(ALTITUDE, Double.toString(altitude));
            params.put(ACCURACY, Float.toString(accuracy));
            params.put(TIMESTAMP, Long.toString(timestamp));

            FormFormaterV3 formater = new FormFormaterV3();
            JsonObject json = formater.format(new FormData(), params);
            jsonArray.add(json);
        }
        return jsonArray;
    }

    private class UpdateLocationThread extends Thread {

        private String url;
        private Map<String, String> headers;
        private List<LocationUpdate> locations;
        private String locationsJson;

        public UpdateLocationThread(String url, Map<String, String> headers, List<LocationUpdate> locations, String locationsJson) {
            this.url = url;
            this.headers = headers;
            this.locations = locations;
            this.locationsJson = locationsJson;
        }

        @Override
        public void run() {
            NetworkUtils.sendRequest(url, headers, locationsJson, new NetworkUtils.RequestCallback() {
                @Override
                public void onFailed(HttpRequestManager requestManager, PopupMessage... messages) {
                    try {
                        Thread.sleep(FAIL_DELAY);
                    } catch (Exception e) {
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            startLocationUpload();
                        }
                    });
                }

                @Override
                public void onResponse(HttpRequestManager requestManager) {
                    onLocationsUploaded();
                }

                @Override
                public void onLogout() {
                    mIsUploading = false;
                }
            });
        }

        void onLocationsUploaded() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    RealmManager.getInstance().removeLocation(locations);
                    startLocationUpload();
                }
            });
        }


    }

    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent action) {
            if (NetworkUtils.isNetworkAvailable()) {
                unregisterConnectivityReceiver();
                if (!mIsUploading) {
                    mIsUploading = true;
                    startLocationUpload();
                }
            }
        }
    }

}
