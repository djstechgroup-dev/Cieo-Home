package com.kinetise.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kinetise.data.systemdisplay.helpers.PermissionManager;
import com.kinetise.helpers.analytics.AnalyticsManager;
import com.kinetise.helpers.http.NetworkUtils;

/**
 * Created by Kuba Komorowski on 2014-06-30.
 * <p/>
 * Klasa obslugujaca zdarzenia polaczenia i rozlaczenia z internetem.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            PermissionManager.getInstance().retrieveAccessTokens();
        }
    }

}
