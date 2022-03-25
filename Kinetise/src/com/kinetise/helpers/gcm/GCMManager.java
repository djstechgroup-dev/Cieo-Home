package com.kinetise.helpers.gcm;

import android.content.Context;
import android.content.SharedPreferences;

import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.helpers.preferences.SecurePreferencesHelper;
import com.kinetise.helpers.threading.ThreadPool;

public class GCMManager {

    public static final String GCM_REGISTRATION_STRING_NAME = "GCM_REGISTRATION_ID";
    public static final String GCM_APP_VER = "GCM_APPLICATION_VERSION";

    /**
     * Checks whether it is necessary to refresh GCM token (on application upgrade or first run)
     *
     * @param context
     */
    public void updateGCM(Context context) {
        SharedPreferences preferencesFile = SecurePreferencesHelper.getUserData();
        int currentVersion = KinetiseApplication.getCurrentAppVersion(context);
        int lastVersion = preferencesFile.getInt(GCM_APP_VER, 0);
        if (currentVersion > lastVersion) {
            registerGCM(context);
        }
    }

    /**
     * Get GCM registration ID and send it to the back-end server (in background thread).
     * Fix: Sometimes after getting registrationID onTokenRefresh in InstanceIDListenerService is called immediately indicating token change.
     * However it didn't change.
     * To not send registrationID twice to the server we need to check if token really changed
     * @param context
     */
    public void registerGCM(Context context) {
        SharedPreferences preferencesFile = SecurePreferencesHelper.getUserData();
        String oldRegistrationId = preferencesFile.getString(GCM_REGISTRATION_STRING_NAME, "");
        GCMRegisterRunnable runnable = new GCMRegisterRunnable(context, oldRegistrationId, new GCMRegisteredCallback());
        ThreadPool.getInstance().executeBackground(runnable);
    }

    //callback for future task (maybe there will be some logic for push registering)
    public class GCMRegisteredCallback {

        public void onGCMRegisterFailed() {
        }

        public void onGCMRegistered(Context context, String registrationId) {
                saveRegistrationID(context, registrationId);
        }
    }

    private void saveRegistrationID(Context context, String registrationId) {
        SharedPreferences preferencesFile = SecurePreferencesHelper.getUserData();
        SharedPreferences.Editor editor = preferencesFile.edit();
        editor.putString(GCM_REGISTRATION_STRING_NAME, registrationId);
        editor.putInt(GCM_APP_VER, KinetiseApplication.getCurrentAppVersion(context));
        editor.apply();
    }

}
