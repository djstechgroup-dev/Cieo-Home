package com.kinetise.components.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.kinetise.helpers.RWrapper;
import com.kinetise.helpers.preferences.SecurePreferencesHelper;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

public class KinetiseApplication extends MultiDexApplication {

    public static final String APP_VERSION = "VERSION";
    private static KinetiseApplication mInstance;
    protected boolean mUseCrashlytics;
    protected boolean mUseStetho;

    public static KinetiseApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        initSecurePreferences();
        initStetho();
        initFabric();
        initImageLoader();
        initRealm();
    }

    private void initSecurePreferences() {
        SecurePreferencesHelper.init(this);
    }

    private void initStetho() {
        if (getResources().getBoolean(RWrapper.bool.use_stetho)) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                            .build());
            mUseStetho = true;
        }
    }

    private void initFabric() {
        if (getResources().getBoolean(RWrapper.bool.use_crashlytics)) {
            mUseCrashlytics = true;
            Fabric.with(this, new Crashlytics());
        }
    }

    public void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .displayer(new FadeInBitmapDisplayer(300))
                .cacheOnDisk(true)
                .resetViewBeforeLoading(false)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .denyCacheImageMultipleSizesInMemory()
                .writeDebugLogs()
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(1048576 * 10)
                .threadPoolSize(5).build();
        ImageLoader.getInstance().init(config);
    }

    private void initRealm() {
        Realm.init(this);
    }

    public static int getCurrentAppVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public static int getLastAppVersion() {
        SharedPreferences preferencesFile = SecurePreferencesHelper.getApplicationSettings();
        return preferencesFile.getInt(APP_VERSION, 0);
    }

    public static boolean isNewAppVersion(Context context) {
        int lastVersion = getLastAppVersion();
        int currentVersion = getCurrentAppVersion(context);
        if (currentVersion > lastVersion)
            return true;
        else
            return false;
    }

    public static void updateSavedVersion(Context context) {
        SharedPreferences preferencesFile = SecurePreferencesHelper.getApplicationSettings();
        SharedPreferences.Editor editor = preferencesFile.edit();
        int version = getCurrentAppVersion(context);
        editor.putInt(APP_VERSION, version);
        editor.apply();
    }

    public boolean useCrashlytics() {
        return mUseCrashlytics;
    }

    public boolean useStetho() {
        return mUseStetho;
    }

    public void logToCrashlytics(String message) {
        if (useCrashlytics()) {
            Crashlytics.getInstance().core.log(message);
        }
    }

}
