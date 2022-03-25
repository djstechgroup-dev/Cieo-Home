package com.kinetise.data.systemdisplay.views.maps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.datadescriptors.AGMapDataDesc;
import com.kinetise.data.descriptors.types.InitCameraModeType;
import com.kinetise.data.systemdisplay.helpers.IPermissionListener;
import com.kinetise.data.systemdisplay.helpers.IPermissionRequestListener;
import com.kinetise.data.systemdisplay.helpers.PermissionManager;
import com.kinetise.data.systemdisplay.views.AGLoadingView;
import com.kinetise.helpers.asynccaller.AsyncCaller;
import com.kinetise.helpers.locationhelper.LocationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AGMapFragment extends MapFragment implements ViewTreeObserver.OnGlobalLayoutListener, OnMarkerClickListener, ClusterManager.OnClusterItemClickListener, IPermissionRequestListener, LocationListener {

    public static final String TAG = AGMapFragment.class.getSimpleName();
    private final static int MAP_PIN_ZOOM_PADDING = 50;
    public static final int MAXIMUM_SINGLE_PIN_ANIMATION_ZOOM = 17;

    private IMapPopUpClick mPopUpClick;
    private HashMap<Marker, PinInfo> mMarkersMap = new HashMap<>();
    private AGMapDataDesc mDataDesc;
    private List<PinInfo> mPins = new ArrayList<>();
    private AGLoadingView mLoading;
    private boolean mLoadingHidden = false;
    private boolean followPositionState = false;
    private boolean isUpdatedToMyLocation = false;
    private GoogleMap mGoogleMap;
    boolean isPermissionDenied = false;
    boolean waitForRequestResponse = false;
    boolean gpsPermissionGrantedWaiting = false;
    private GoogleApiClient mApiClient;
    private boolean isFragmentActive = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getView() != null && getView().getViewTreeObserver() != null) {
            getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mApiClient != null) {
            mApiClient.reconnect();
        }
        setFollowMyPosition(followPositionState);
        isFragmentActive = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mApiClient != null && mApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);
            mApiClient.disconnect();
        }
        isFragmentActive = false;
    }

    private void showLoading() {
        if (!mLoadingHidden) {
            mLoading = new AGLoadingView(getActivity());
            ((ViewGroup) getView()).addView(mLoading);
        }
    }

    public void hideLoading() {
        if (mLoading != null && getView() != null) {
            ((ViewGroup) getView()).removeView(mLoading);
            mLoading = null;
        }
        mLoadingHidden = true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        showLoading();
        updatePins(mPins);
        getView().getViewTreeObserver().addOnGlobalLayoutListener(this);
        super.onViewCreated(view, savedInstanceState);
    }

    public void setGoogleMap(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (gpsPermissionGrantedWaiting == true) {
            onPermissionGranted();
        }
    }

    public void updatePins(final List<PinInfo> pins) {
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPins = pins;
                if (mGoogleMap != null) {
                    mGoogleMap.clear();
                    mMarkersMap.clear();
                    mGoogleMap.setOnMarkerClickListener(AGMapFragment.this);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (int i = 0; i < pins.size(); i++) {
                        PinInfo pin = pins.get(i);
                        Marker m = mGoogleMap.addMarker(createMarkerOptions(pin));
                        mMarkersMap.put(m, pin);
                        builder.include(pin.getLatLng());
                    }
                    if (pins.size() > 0) {
                        if (mGoogleMap != null && isFragmentActive) {
                            if (pins.size() > 1) {
                                mGoogleMap.animateCamera(prepareCameraAnimationForMultiplePins(builder.build(), true));
                            } else {
                                mGoogleMap.animateCamera(prepareCameraAnimationForSinglePin(pins.get(0).getLatLng()));
                            }
                        }
                    }
                }
                hideLoading();
            }
        });
    }


    public void scrollMapForClustering(final List<PinInfo> pins) {
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPins = pins;
                if (mGoogleMap != null) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (int i = 0; i < pins.size(); i++) {
                        PinInfo pin = pins.get(i);
                        builder.include(pin.getLatLng());
                    }
                    float radius = mDataDesc.getInitMinRadius();
                    if (mDataDesc.getInitCameraMode() == InitCameraModeType.PINS) {
                        if (followPositionState) return;
                        if (pins.size() > 0) {
                            if (mGoogleMap != null) {

                                LatLngBounds bounds = builder.build();

                                LatLngBounds adjustedBounds = adjustBounds(bounds, radius);
                                boolean setPadding = !isRadiusChangedBounds(adjustedBounds, bounds);
                                CameraUpdate cameraUpdate = prepareCameraAnimationForMultiplePins(adjustedBounds, setPadding);
                                if (isFragmentActive)
                                    mGoogleMap.animateCamera(cameraUpdate);
                            }
                        }
                    }
                }
                hideLoading();
            }
        });
    }

    private boolean isRadiusChangedBounds(LatLngBounds adjustedBounds, LatLngBounds bounds) {
        return !(bounds.contains(adjustedBounds.southwest) && bounds.contains(adjustedBounds.northeast));
    }


    private LatLngBounds adjustBounds(LatLngBounds bounds, float radius) {
        if (radius <= 0) {
            return bounds;
        }
        LatLng center = bounds.getCenter();

        return new LatLngBounds.Builder().
                include(bounds.northeast).
                include(bounds.southwest).
                include(SphericalUtil.computeOffset(center, radius, 0)).
                include(SphericalUtil.computeOffset(center, radius, 90)).
                include(SphericalUtil.computeOffset(center, radius, 180)).
                include(SphericalUtil.computeOffset(center, radius, 270)).build();
    }


    private CameraUpdate prepareCameraAnimationForSinglePin(LatLng latLng) {
        return CameraUpdateFactory.newLatLngZoom(latLng, MAXIMUM_SINGLE_PIN_ANIMATION_ZOOM);
    }

    private CameraUpdate prepareCameraAnimationForMultiplePins(LatLngBounds bounds, boolean usePadding) {
        CameraUpdate cameraUpdate;
        int width = (int) mDataDesc.getCalcDesc().getWidth();
        int height = (int) mDataDesc.getCalcDesc().getHeight();
        int min = Math.min(width, height);
        int padding = 0;
        if (usePadding)
            padding = (min > 2 * MAP_PIN_ZOOM_PADDING) ? MAP_PIN_ZOOM_PADDING : ((min - 1) / 2);
        cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        return cameraUpdate;
    }

    private MarkerOptions createMarkerOptions(PinInfo pin) {
        return new MarkerOptions()
                .position(pin.getLatLng())
                .icon(BitmapDescriptorFactory.fromBitmap(pin.getPinBitmap()));
    }

    public void setMarkerClickListener(IMapPopUpClick listener) {
        mPopUpClick = listener;
    }

    public void setDesc(AGMapDataDesc mDataDesc) {
        this.mDataDesc = mDataDesc;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mPopUpClick.onMarkerClick(mMarkersMap.get(marker));
        return true;
    }

    @Override
    public void onGlobalLayout() {
        if (mDataDesc.getInitCameraMode() == InitCameraModeType.ME || mDataDesc.isMyLocationEnabled()) {
            requestForPermissionAndLocationProvider(this);
        }

        View view = this.getView();
        if (view != null && view.getViewTreeObserver() != null) {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }


    private void requestForPermissionAndLocationProvider(IPermissionRequestListener listener) {
        if (isPermissionDenied == true || waitForRequestResponse == true)
            return;
        waitForRequestResponse = true;
        final Context context = AGApplicationState.getInstance().getActivity();
        if (hasGpsPermission(context) == false) {
            requestLocationPermission(context, listener);
        } else {
            listener.onPermissionGranted();
            if (isAdded() && hasGpsProvider() == false) {
                requestGPSProvider();
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void setFollowMyPosition(final boolean fallowMyPosition) {
        if (followPositionState != fallowMyPosition) {
            followPositionState = fallowMyPosition;
            if (!followPositionState) {
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }

        if (mApiClient != null && mApiClient.isConnected()) {
            if (fallowMyPosition)
                requestLocationUpdates();
            return;
        }

        if (fallowMyPosition == false && isUpdatedToMyLocation == true) {
            return;
        }

        final Context context = AGApplicationState.getInstance().getActivity();
        if (context == null) return;

        mApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (mApiClient.isConnected())
                            requestLocationUpdates();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .build();
        mApiClient.connect();

    }

    @SuppressWarnings("MissingPermission")
    private void requestLocationUpdates() {
        final Context context = AGApplicationState.getInstance().getActivity();
        if (context != null && hasGpsPermission(context))
            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, createLocationRequest(), AGMapFragment.this);
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setMaxWaitTime(10000);
        return mLocationRequest;
    }

    private boolean hasGpsPermission(Context context) {
        return PermissionManager.hasGrantedPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @SuppressWarnings("MissingPermission")
    private void requestLocationPermission(Context context, IPermissionRequestListener listener) {
        ((IPermissionListener) context).onPermissionLack(IPermissionListener.ACCESS_FINE_LOCATION_REQUEST_CODE, listener);
    }


    @Override
    public boolean onClusterItemClick(ClusterItem clusterItem) {
        mPopUpClick.onMarkerClick((PinInfo) clusterItem);
        return false;
    }

    private boolean hasGpsProvider() {
        if (getActivity() != null && getActivity().getSystemService(Context.LOCATION_SERVICE) != null) {
            LocationManager locationManager = (LocationManager) KinetiseApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
            return LocationHelper.isGPSProviderEnabled(locationManager);
        }
        return false;
    }

    private void requestGPSProvider() {
        Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        gpsOptionsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(gpsOptionsIntent);
    }

    public void onMoveMapView() {
        setFollowMyPosition(false);
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void onPermissionGranted() {
        if (mGoogleMap == null) {
            gpsPermissionGrantedWaiting = true;
            return;
        }

        if (mDataDesc.isMyLocationEnabled() || mDataDesc.getInitCameraMode() == InitCameraModeType.ME) {
            if (mDataDesc.isMyLocationEnabled())
                mGoogleMap.setMyLocationEnabled(true);

            setFollowMyPosition(false);

            mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    setFollowMyPosition(true);
                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    enableGPS();
                    return false;
                }
            });
        }
        enableGPS();

        gpsPermissionGrantedWaiting = false;
        waitForRequestResponse = false;
    }

    private void enableGPS() {
        if (isAdded() && hasGpsProvider() == false) {
            requestGPSProvider();
        }
    }

    @Override
    public void onPermissionDenied() {
        isPermissionDenied = true;
        waitForRequestResponse = false;
    }


    @Override
    public void onLocationChanged(Location location) {
        if (followPositionState || (!isUpdatedToMyLocation && mDataDesc.getInitCameraMode() == InitCameraModeType.ME)) {
            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

            LatLngBounds bounds = new LatLngBounds(currentPosition, currentPosition);

            if (isFragmentActive)
                animateCamera(currentPosition, bounds);
            if (isUpdatedToMyLocation && followPositionState == false) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);
            }
        }
    }

    private void animateCamera(LatLng currentPosition, LatLngBounds bounds) {
        if (!isUpdatedToMyLocation) {
            isUpdatedToMyLocation = true;
            float radius = mDataDesc.getInitMinRadius();
            bounds = adjustBounds(bounds, radius);
            CameraUpdate cameraUpdate = prepareCameraAnimationForMultiplePins(bounds, false);
            mGoogleMap.animateCamera(cameraUpdate);
        } else {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentPosition);
            mGoogleMap.animateCamera(cameraUpdate);
        }
    }
}
