package com.kinetise.data.systemdisplay.helpers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.helpers.RWrapper;
import com.kinetise.helpers.TwitterService;
import com.kinetise.helpers.facebook.FacebookService;
import com.kinetise.helpers.gcm.GCMManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class PermissionManager {
    private static PermissionManager mInstance;

    private static final String PERMISSIONS_JSON_FILE = "permissions.txt";
    private static final String GPS_PERMISSION = "gps";
    private static final String FACEBOOK_PERMISSION = "facebooktoken";
    private static final String TWITTER_PERMISSION = "twittertoken";
    private static final String PUSH_PERMISSION = "push";

    private boolean mGpsPermission;
    private boolean mFacebookPermissions;
    private boolean mTwitterPermissions;
    private boolean mPushPermission;

    private PermissionManager(Context context) {
        init(context);
    }

    private PermissionManager(){}

    public static PermissionManager getInstance() {
        if (mInstance == null) {
            synchronized (PermissionManager.class) {
                if (mInstance == null) {
                    mInstance = new PermissionManager(AGApplicationState.getInstance().getContext());
                }
            }
        }
        return mInstance;
    }

    public static void clearInstance() {
        mInstance = null;
    }

    private void init(Context context) {
        InputStream stream;
        try {
            stream = context.getAssets().open(context.getString(RWrapper.string.developer_assets_prefix) + File.separator + PERMISSIONS_JSON_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase(Locale.US).contains(GPS_PERMISSION)) {
                    mGpsPermission = true;
                }
                if (line.toLowerCase(Locale.US).contains(FACEBOOK_PERMISSION)) {
                    mFacebookPermissions = true;
                }
                if (line.toLowerCase(Locale.US).contains(TWITTER_PERMISSION)) {
                    mTwitterPermissions = true;
                }
                if (line.toLowerCase(Locale.US).contains(PUSH_PERMISSION)) {
                    mPushPermission = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasGpsPermission() {
        return mGpsPermission;
    }

    public boolean hasFacebookPermissions() {
        return mFacebookPermissions;
    }

    public boolean hasTwitterPermissions() {
        return mTwitterPermissions;
    }

    public boolean hasPushPermission() {
        return mPushPermission;
    }

    public void retrieveAccessTokens() {
        if (hasTwitterPermissions())
            TwitterService.getInstance().retrieveAccessToken();
        if (hasFacebookPermissions())
            FacebookService.getInstance().retrieveAccessToken();
    }

    public void retrieveGCMRegistrationID(Context context) {
        if (hasPushPermission()) {
            new GCMManager().updateGCM(context);
        }
    }

    public static boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasGrantedPermission(Context context, String permissionName) {
        int mCameraPermissionState = ActivityCompat.checkSelfPermission(context, permissionName);
        return mCameraPermissionState == PackageManager.PERMISSION_GRANTED ? true : false;
    }
}
