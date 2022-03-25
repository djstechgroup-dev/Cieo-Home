package com.kinetise.components.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kinetise.helpers.locationhelper.IProviderChangeListener;

/**
 * Created by Paweł on 2015-12-09.
 */
public class LocationProvidersChangeReceiver extends BroadcastReceiver {

    private IProviderChangeListener mListener;

    public LocationProvidersChangeReceiver(IProviderChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mListener.onProviderChange();
    }
}
