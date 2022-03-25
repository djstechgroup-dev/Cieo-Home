package com.kinetise.helpers.analytics;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kinetise.R;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.helpers.RWrapper;
import com.kinetise.helpers.preferences.SecurePreferencesHelper;
import com.kinetise.helpers.threading.ThreadPool;

public class AnalyticsManager {
    private static AnalyticsManager mInstance;

    private String mAppName;
    private Tracker mKinetiseTracker;

    public static AnalyticsManager getInstance() {
        if (mInstance==null) {
            synchronized (AnalyticsManager.class) {
                if (mInstance == null) {
                    mInstance = new AnalyticsManager(AGApplicationState.getInstance().getContext());
                }
            }
        }
        return mInstance;
    }

    private AnalyticsManager(Context context) {
        mAppName = context.getString(RWrapper.string.app_name);
        GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(context);
        mKinetiseTracker = googleAnalytics.newTracker(R.xml.analytics_config); //default tracker
    }

    public static void clearInstance(){
        mInstance = null;
    }

    public void sendScreenView(String atag, String detalGID) {
        String screenName;
        if(detalGID!=null)
            screenName = String.format("%s/%s/%s", mAppName, atag, detalGID);
        else
            screenName = String.format("%s/%s", mAppName, atag);
        sendScreenHitEvent(mKinetiseTracker,screenName);
    }

    protected void sendScreenHitEvent(Tracker t,String screenName) {
        if (t!=null) {
            t.setScreenName(screenName);
            t.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }
}
