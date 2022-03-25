package com.kinetise.helpers.locationhelper;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.google.android.gms.maps.LocationSource;
import com.kinetise.support.logger.Logger;

import java.util.List;

public class LocationHelper implements LocationListener {

    private static final long MIN_LOCATION_UPDATE_INTERVAL = 1000;
    private static final long MIN_LOCATION_UPDATE_DISTANCE = 0;
    private static LocationHelper mInstance;
    private Location mLastKnownLocation;
    private android.location.LocationManager mManager;
    private LocationSource.OnLocationChangedListener mOnLocationChangedListener;

    public static LocationHelper getInstance() {
        if (mInstance == null) {
            synchronized (LocationHelper.class){
                if (mInstance == null) {
                    mInstance = new LocationHelper();
                }
            }
        }
        return mInstance;
    }

    private LocationHelper(){}

    public static void clearInstance(){
        mInstance = null;
    }

    public static boolean isLocationEnabled(android.location.LocationManager manager) {
        boolean gpsEnabled = manager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        boolean networkEnabled = manager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);

        return (gpsEnabled || networkEnabled);
    }

    public boolean isLocationEnabled() {
        boolean gpsEnabled = mManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        boolean networkEnabled = mManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);

        return (gpsEnabled || networkEnabled);
    }

    public static boolean isGPSProviderEnabled(LocationManager manager) {
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isNetworkProviderEnabled(LocationManager manager) {
        return manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void startLocationSearching(Context context) {
        android.location.LocationManager manager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //TODO handle location permission
        startLocationSearching(manager);
    }

    public void stopLocationSearching() {
        if (mManager != null) {
            mManager.removeUpdates(this);
        }
    }

    private void startLocationSearching(android.location.LocationManager manager) {
        mManager = manager;
        List<String> providers = mManager.getProviders(false);

        for (String provider : providers) {
            mLastKnownLocation = getBetterLocation(mLastKnownLocation, mManager.getLastKnownLocation(provider));
            requestLocationUpdates(provider);
        }
    }

    private Location getBetterLocation(Location lastLocation, Location lastKnownLocation) {
        if (lastLocation != null && lastKnownLocation != null
                && lastKnownLocation.getAccuracy() < lastLocation.getAccuracy()) {
            return lastKnownLocation;
        } else {
            return lastLocation;
        }
    }


    private void requestLocationUpdates(String provider) {
        mManager.requestLocationUpdates(provider, MIN_LOCATION_UPDATE_INTERVAL, MIN_LOCATION_UPDATE_DISTANCE, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastKnownLocation = location;
        if (mLastKnownLocation != null) {
            if (mOnLocationChangedListener != null) {
                mOnLocationChangedListener.onLocationChanged(location);
            }
            Logger.d(((Object) this).getClass().getSimpleName(),
                    String.format("Latitude: %f, Longitude: %f, Accuracy: %f, \nProvider: %s",
                            mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude(),
                            mLastKnownLocation.getAccuracy(),
                            mLastKnownLocation.getProvider()));
        }
    }

    public Location getLastKnownLocation() {
        return mLastKnownLocation;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }


    public void setOnLocationChangedListener(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        mOnLocationChangedListener = onLocationChangedListener;
    }
}
