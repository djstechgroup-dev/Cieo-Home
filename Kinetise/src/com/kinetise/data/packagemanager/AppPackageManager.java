package com.kinetise.data.packagemanager;

import android.content.Context;

import com.kinetise.data.application.AGApplicationState;

public class AppPackageManager {

    private static AppPackageManager mInstance;
    private AppPackage mPackage;

    private AppPackageManager(Context context) {
        mPackage = new AppPackage(context);
    }

    private AppPackageManager() {
    }

    public static AppPackageManager getInstance() {
        if (mInstance == null) {
            synchronized (AppPackageManager.class) {
                if (mInstance == null) {
                    mInstance = new AppPackageManager(AGApplicationState.getInstance().getContext());
                }
            }
        }
        return mInstance;
    }

    public static void clearInstance() {
        mInstance = null;
    }

    public AppPackage getPackage() {
        return mPackage;
    }
}
