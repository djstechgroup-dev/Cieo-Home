package com.kinetise.components.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.kinetise.components.broadcastreceivers.LocationProvidersChangeReceiver;
import com.kinetise.data.location.LocationUpdate;
import com.kinetise.data.location.LocationUpdateParams;
import com.kinetise.data.location.LocationUpdateSession;
import com.kinetise.data.sourcemanager.RealmManager;
import com.kinetise.helpers.locationhelper.IProviderChangeListener;
import com.kinetise.helpers.locationhelper.LocationHelper;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Paweł on 2015-12-09.
 */
public class LocationService extends Service implements LocationListener, IProviderChangeListener {

    public static final String ACTION_START_LOCATION_TRACKING = "com.kinetise.action.TRACK_LOCATION_START";
    public static final String ACTION_STOP_LOCATION_TRACKING = "com.kinetise.action.TRACK_LOCATION_STOP";

    public static final String URL = "URL";
    public static final String HEADER_PARAMS = "HEADER_PARAMS";
    public static final String MIN_TIME = "MIN_TIME";
    public static final String MIN_DISTANCE = "MIN_DISTANCE";
    public static final int DEFAULT_MIN_TIME = 10000;
    public static final int DEFAULT_MIN_VALUE = 100;

    private LocationProvidersChangeReceiver mProvidersChangeReceiver;
    private long mMinTime;
    private int mMinDistance;

    private int mSessionId;

    //region Service methods

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null && action.equals(ACTION_START_LOCATION_TRACKING)) {
                stopLocationUpdates();
                closeAllSessions();

                mMinTime = intent.getLongExtra(MIN_TIME, DEFAULT_MIN_TIME);
                mMinDistance = intent.getIntExtra(MIN_DISTANCE, DEFAULT_MIN_VALUE);

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (LocationHelper.isGPSProviderEnabled(locationManager) == false) {
                    requestGPSProvider();
                }

                RealmList<LocationUpdateParams> headerParams = LocationUpdateParams.getHttpParamsAsRealmList((HashMap<String, String>) intent.getSerializableExtra(HEADER_PARAMS));

                mSessionId = RealmManager.getInstance().getLastSessionId() + 1;

                LocationUpdateSession locationUpdateSession = new LocationUpdateSession();
                locationUpdateSession.setSessionId(mSessionId);
                locationUpdateSession.setUrl(intent.getStringExtra(URL));
                locationUpdateSession.setHeaderParams(headerParams);
                locationUpdateSession.setActive(true);

                RealmManager.getInstance().insertLocationUpdateSession(locationUpdateSession);

                startLocationUpdates();
                registerProvidersChangeReceiver();
            } else if (action != null && action.equals(ACTION_STOP_LOCATION_TRACKING)) {
                stopLocationUpdates();
                closeAllSessions();
                stopService();
            }
        }
        return START_STICKY;
    }

    private void closeAllSessions() { //when Service was killed some session may stay active
        RealmManager.getInstance().closeAllSessions();
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (LocationHelper.isGPSProviderEnabled(locationManager)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mMinTime, mMinDistance, this);
        } else if (LocationHelper.isNetworkProviderEnabled(locationManager)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mMinTime, mMinDistance, this);
        }
    }

    private void requestGPSProvider() {
        Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        gpsOptionsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(gpsOptionsIntent);
    }

    @SuppressWarnings("MissingPermission")
    private void stopLocationUpdates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
    }

    private void registerProvidersChangeReceiver() {
        if (mProvidersChangeReceiver == null) {
            mProvidersChangeReceiver = new LocationProvidersChangeReceiver(this);
            IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
            registerReceiver(mProvidersChangeReceiver, filter);
        }
    }

    private void unregisterProvidersChangeReceiver() {
        if (mProvidersChangeReceiver != null) {
            unregisterReceiver(mProvidersChangeReceiver);
            mProvidersChangeReceiver = null;
        }
    }

    public void stopService() {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        unregisterProvidersChangeReceiver();
        super.onDestroy();
    }

    //endregion

    //region LocationListener

    @Override
    public void onLocationChanged(Location location) {
        final LocationUpdate locationUpdate = new LocationUpdate();
        locationUpdate.setAccuracy(location.getAccuracy());
        locationUpdate.setLatitude(location.getLatitude());
        locationUpdate.setLongitude(location.getLongitude());
        locationUpdate.setAltitude(location.getAltitude());
        locationUpdate.setTimestamp(location.getTime());
        locationUpdate.setSessionId(mSessionId);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RealmManager.getInstance().insertLocationUpdate(locationUpdate);
                startService(LocationUploadService.createUploadIntent(getApplicationContext()));
            }
        });


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        stopLocationUpdates();
        startLocationUpdates();
    }

    @Override
    public void onProviderDisabled(String s) {
        stopLocationUpdates();
        startLocationUpdates();
    }

    //endregion

    //region IProviderChangeListener

    @Override
    public void onProviderChange() {
        stopLocationUpdates();
        startLocationUpdates();
    }

    //endregion

}
