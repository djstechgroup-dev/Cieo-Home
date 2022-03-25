package com.kinetise.components.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.kinetise.R;
import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.components.services.LocationUploadService;
import com.kinetise.data.VariableStorage;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.nativeshare.ShareData;
import com.kinetise.data.application.externalapplications.OpenGalleryApp;
import com.kinetise.data.application.feedmanager.DataFeedDatabase;
import com.kinetise.data.application.feedmanager.DataFeedsMap;
import com.kinetise.data.application.feedmanager.FeedManager;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedDBManager;
import com.kinetise.data.application.loginmanager.BasicAuthLoginManager;
import com.kinetise.data.application.overalymanager.OverlayManager;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.calcmanager.CalcManager;
import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.LocalStorageDescriptionDataDesc;
import com.kinetise.data.descriptors.types.AGOrientationType;
import com.kinetise.data.descriptors.types.AGScreenTransition;
import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.packagemanager.AppPackageManager;
import com.kinetise.data.parsermanager.IParserManager;
import com.kinetise.data.parsermanager.ParserManager;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.data.sourcemanager.FontLibrary;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.data.sourcemanager.propertymanager.PropertyStorage;
import com.kinetise.data.sourcemanager.propertymanager.Synchronizer;
import com.kinetise.data.systemdisplay.AGWebViewCallback;
import com.kinetise.data.systemdisplay.IPlatformView;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.helpers.CodeScannerSetter;
import com.kinetise.data.systemdisplay.helpers.GetPhoneContactSetter;
import com.kinetise.data.systemdisplay.helpers.IPermissionListener;
import com.kinetise.data.systemdisplay.helpers.IPermissionRequestListener;
import com.kinetise.data.systemdisplay.helpers.PermissionManager;
import com.kinetise.data.systemdisplay.helpers.PhotoSetter;
import com.kinetise.data.systemdisplay.views.AGBodyView;
import com.kinetise.data.systemdisplay.views.AGCodeScannerView;
import com.kinetise.data.systemdisplay.views.AGGetPhoneContactView;
import com.kinetise.data.systemdisplay.views.AGPhotoView;
import com.kinetise.data.systemdisplay.views.AGScreenView;
import com.kinetise.data.systemdisplay.views.maps.AGMapFragment;
import com.kinetise.data.systemdisplay.views.text.CharWidthLoaderHelper;
import com.kinetise.data.systemdisplay.views.text.FontSizeHelper;
import com.kinetise.debug.DebugTouchDetector;
import com.kinetise.helpers.AppSaveStateHelper;
import com.kinetise.helpers.BitmapHelper;
import com.kinetise.helpers.FullscreenVideoBridge;
import com.kinetise.helpers.NetworkStateReceiver;
import com.kinetise.helpers.PdfManager;
import com.kinetise.helpers.RWrapper;
import com.kinetise.helpers.TwitterService;
import com.kinetise.helpers.analytics.AnalyticsManager;
import com.kinetise.helpers.facebook.FacebookService;
import com.kinetise.helpers.http.NetworkUtils;
import com.kinetise.helpers.http.OkHttpClientManager;
import com.kinetise.helpers.http.RedirectMap;
import com.kinetise.helpers.locationhelper.LocationHelper;
import com.kinetise.helpers.preferences.SecurePreferencesHelper;
import com.kinetise.helpers.threading.AGAsyncTask;
import com.kinetise.helpers.threading.ThreadPool;
import com.kinetise.helpers.time.DateUpdater;
import com.kinetise.helpers.time.ServerTimeManager;
import com.kinetise.helpers.youtube.GoogleService;
import com.kinetise.support.layouts.OnMeasureListener;
import com.kinetise.support.layouts.SizeAwareRelativeLayout;
import com.kinetise.support.logger.Logger;
import com.kinetise.support.scrolls.scrollManager.ScrollManager;
import com.kinetise.views.FullscreenWebview;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class KinetiseActivity extends AppCompatActivity implements IPlatformView, OnMeasureListener, ActivityCompat.OnRequestPermissionsResultCallback, IPermissionListener {
    public static final String PHOTO_VIEW_ID_BUNDLE_KEY = "PhotoViewId";

    public static int mLastScreenIndex = -1; //Fix for Overlay remove view after animation end.

    private static String mFilePathKey = "FilePath";
    private SystemDisplay mSystemDisplay;
    private ConfigurationInfo mConfigurationInfo;
    private UiLifecycleHelper mUiLifecycleHelper;

    private View mLastFocus;
    private int mOnResumeOrientation;
    private boolean mSystemInitialized = false;
    private boolean mWasResumed = false;
    private boolean mAppStateSaved = false;


    private ArrayList<AGWebViewCallback> mCallbacks = new ArrayList<AGWebViewCallback>();
    //Callback called after new view hierarchy after loading new screen is set as a new main view.
    //Used to set background image on photo view after returning from photo/chooser application
    private Runnable mOnMainViewSet;
    private boolean mSystemIsInitializated = false;

    private static int mInstances = 0;

    private NetworkStateReceiver mNetworkStateReciever;

    private AGAsyncTask mInitTask;
    private Set<String> waitingForPermissionResponse = new HashSet<>();
    private ArrayList<IPermissionRequestListener> mPermissionRequestListeners;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        ++mInstances;
        super.onCreate(savedInstanceState);
        setContentView(RWrapper.layout.activity_main);
        KinetiseApplication.getInstance().logToCrashlytics("[KA] onCreate");

        setHardwareAcceleration();

        AGApplicationState.getInstance().setContext(getApplicationContext());
        SecurePreferencesHelper.migrateOldPreferences(getApplicationContext());
        if (savedInstanceState == null) {
            if (KinetiseApplication.isNewAppVersion(getApplicationContext())) {
                onUpgrade();
            }
        }

        initMainDisplay();
        SystemDisplay.blockScreenWithLoadingDialog(true, this);

        initUiLifecycleHelper(savedInstanceState);
        initThirdParties();
        startService(LocationUploadService.createUploadIntent(getApplicationContext()));

        findViewById(RWrapper.id.debugTouchInterceptView).setOnTouchListener(new DebugTouchDetector(this));

        mInitTask = new AGAsyncTask() {

            @Override
            public void run() {
                final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                mConfigurationInfo = activityManager.getDeviceConfigurationInfo();

                initSystem();
                initLocalDB(ParserManager.getInstance());

                if (!mIsCanceled) {
                    runOnUiThread(() -> {
                        IParserManager parserManager = ParserManager.getInstance();
                        parserManager.getApplicationDescription().resolveDynamicFields();
                        loadFirstScreen();
                        DateUpdater.getInstance().initTimeUpdates();
                    });
                }

                mInitTask = null;
            }
        };
        ThreadPool.getInstance().execute(mInitTask);
    }

    private void initLocalDB(IParserManager parserManager) {
        LocalStorageDescriptionDataDesc descriptionDataDesc = parserManager.getLocalStorageDataDesc();
        BitmapHelper.copyToLocale(BitmapHelper.DB_ATTACHMENT);
        if (descriptionDataDesc != null)
            DataFeedDBManager.initLocalDB(descriptionDataDesc);
    }

    private void setHardwareAcceleration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }
    }

    private void onUpgrade() {
        KinetiseApplication.updateSavedVersion(getApplicationContext());
        DataFeedsMap.getInstance().clear();
    }

    private void loadFirstScreen() {
        AGApplicationState.getInstance().startLoadingFirstScreen();
    }

    private void initThirdParties() {
        PermissionManager.getInstance().retrieveAccessTokens();
        PermissionManager.getInstance().retrieveGCMRegistrationID(getApplicationContext());
    }

    private void initUiLifecycleHelper(Bundle savedInstanceState) {
        mUiLifecycleHelper = new UiLifecycleHelper(KinetiseActivity.this, FacebookService.getInstance().getSessionStatusCallback());
        mUiLifecycleHelper.onCreate(savedInstanceState);
    }

    private void initMainDisplay() {
        SizeAwareRelativeLayout mainDisplay = (SizeAwareRelativeLayout) findViewById(RWrapper.id.mainDisplay);
        mainDisplay.setOnMeasureListener(KinetiseActivity.this);
    }

    private synchronized void initSystem() {
        CharWidthLoaderHelper.init(getApplicationContext());

        mSystemDisplay = new SystemDisplay(this);
        AGApplicationState.getInstance().initInstance(getApplicationContext(), mSystemDisplay);
        LanguageManager.getInstance();

        ServerTimeManager.initializeServerDate();

        mSystemInitialized = true;

        View mainLayout = findViewById(RWrapper.id.main);
        mSystemDisplay.setDisplaySize(mainLayout.getWidth(), mainLayout.getHeight());
    }

    @Override
    protected void onStart() {
        super.onStart();
        KinetiseApplication.getInstance().logToCrashlytics("[KA] onStart");
        RedirectMap.getInstance().restoreRedirectMap();
        if (PermissionManager.getInstance().hasGpsPermission()) {
            LocationHelper.getInstance().startLocationSearching(getApplicationContext());
        }
    }

    public void closeKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    public void setMainView(SystemDisplay display, AGScreenView newScreenView, AGScreenTransition transition) {
        try {
            SystemDisplay.blockScreenWithLoadingDialog(true, this, true);
            Logger.v(this, "setMainView", newScreenView.toString());

            AGOrientationType orientationToSet = (newScreenView.getDescriptor()).getOrientationType();
            setOrientation(orientationToSet);
            if (mSystemDisplay == null) {
                mSystemDisplay = display;
            }
            SizeAwareRelativeLayout mainDisplay = (SizeAwareRelativeLayout) findViewById(RWrapper.id.mainDisplay);

            final View oldScreenView;
            if (mLastScreenIndex > -1 && mainDisplay.getChildAt(mLastScreenIndex) instanceof AGScreenView) {
                oldScreenView = mainDisplay.getChildAt(mLastScreenIndex);
            } else {
                oldScreenView = null;
            }

            switch (transition) {
                case FADE:
                    goToScreenWithTransitionInAndOut(newScreenView, oldScreenView, mainDisplay, R.anim.fade_in, R.anim.fade_out);
                    break;
                case SLIDE_LEFT:
                    goToScreenWithTransitionInAndOut(newScreenView, oldScreenView, mainDisplay, R.anim.slide_in_right_to_left, R.anim.slide_out_right_to_left);
                    break;
                case SLIDE_RIGHT:
                    goToScreenWithTransitionInAndOut(newScreenView, oldScreenView, mainDisplay, R.anim.slide_in_left_to_right, R.anim.slide_out_left_to_right);
                    break;
                case COVER_FROM_LEFT:
                    goToScreenWithTransitionIn(newScreenView, oldScreenView, mainDisplay, R.anim.slide_in_left_to_right);
                    break;
                case COVER_FROM_RIGHT:
                    goToScreenWithTransitionIn(newScreenView, oldScreenView, mainDisplay, R.anim.slide_in_right_to_left);
                    break;
                case COVER_FROM_TOP:
                    goToScreenWithTransitionIn(newScreenView, oldScreenView, mainDisplay, R.anim.slide_in_top_to_bottom);
                    break;
                case COVER_FROM_BOTTOM:
                    goToScreenWithTransitionIn(newScreenView, oldScreenView, mainDisplay, R.anim.slide_in_bottom_to_top);
                    break;
                case UNCOVER_TO_LEFT:
                    goToScreenWithTransitionOut(newScreenView, oldScreenView, mainDisplay, R.anim.slide_out_right_to_left);
                    break;
                case UNCOVER_TO_RIGHT:
                    goToScreenWithTransitionOut(newScreenView, oldScreenView, mainDisplay, R.anim.slide_out_left_to_right);
                    break;
                case UNCOVER_TO_TOP:
                    goToScreenWithTransitionOut(newScreenView, oldScreenView, mainDisplay, R.anim.slide_out_bottom_to_top);
                    break;
                case UNCOVER_TO_BOTTOM:
                    goToScreenWithTransitionOut(newScreenView, oldScreenView, mainDisplay, R.anim.slide_out_top_to_bottom);
                    break;
                default:
                    if (mLastScreenIndex > -1 && mainDisplay.getChildAt(mLastScreenIndex) instanceof AGScreenView)
                        mainDisplay.removeViewAt(mLastScreenIndex);
                    addViewWithoutAnimation(newScreenView, mainDisplay);
                    mLastScreenIndex = mainDisplay.indexOfChild(newScreenView);
                    onNewScreenEntered(newScreenView);
                    //In case of changing newScreenView while running changes on views from using AsyncCaller eg. bitmap setting
                    break;
            }


        } catch (Exception logged) {
            ExceptionManager.getInstance().handleException(logged, false);
        }
        mSystemIsInitializated = true;
        if (mOnMainViewSet != null) {
            mOnMainViewSet.run();
        }
    }

    void goToScreenWithTransitionInAndOut(AGScreenView newScreenView, View oldScreenView, final SizeAwareRelativeLayout mainDisplay, int transitionIn, int transitionOut) {
        if (oldScreenView != null) {
            removeViewWithAnimation(newScreenView, oldScreenView, mainDisplay, transitionOut);
        }
        addViewWithoutAnimation(newScreenView, mainDisplay);

        Animation animation = AnimationUtils.loadAnimation(getBaseContext(), transitionIn);
        newScreenView.startAnimation(animation);
    }

    void goToScreenWithTransitionIn(AGScreenView newScreenView, final View oldScreenView, final SizeAwareRelativeLayout mainDisplay, int transitionIn) {
        addViewWithoutAnimation(newScreenView, mainDisplay);

        Animation animation = AnimationUtils.loadAnimation(getBaseContext(), transitionIn);
        if (oldScreenView != null) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mainDisplay.removeView(oldScreenView);
                    mLastScreenIndex = mainDisplay.indexOfChild(newScreenView);
                    onNewScreenEntered(newScreenView);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        newScreenView.startAnimation(animation);
    }

    void onNewScreenEntered(AGScreenView newScreenView) {
        SystemDisplay.blockScreenWithLoadingDialog(false, this);
        newScreenView.onScreenEntered();
    }

    void goToScreenWithTransitionOut(AGScreenView newScreenView, final View oldScreenView, final SizeAwareRelativeLayout mainDisplay, int transitionOut) {
        addViewWithoutAnimation(newScreenView, mainDisplay);

        if (oldScreenView != null) {
            oldScreenView.bringToFront();
            removeViewWithAnimation(newScreenView, oldScreenView, mainDisplay, transitionOut);
        }
    }

    private void addViewWithoutAnimation(AGScreenView newScreenView, SizeAwareRelativeLayout mainDisplay) {
        mainDisplay.addView(newScreenView);
        mainDisplay.setVisibility(View.VISIBLE);
    }

    private void removeViewWithAnimation(final AGScreenView newScreenView, final View oldScreenView, final SizeAwareRelativeLayout mainDisplay, int transitionOut) {
        Animation animation = AnimationUtils.loadAnimation(getBaseContext(), transitionOut);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().post(() -> {
                    mainDisplay.removeView(oldScreenView);
                    mLastScreenIndex = mainDisplay.indexOfChild(newScreenView);
                    onNewScreenEntered(newScreenView);
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        oldScreenView.startAnimation(animation);
    }

    private void setOrientation(AGOrientationType orientation) {
        if (orientation.equals(AGOrientationType.PORTRAIT)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (orientation.equals(AGOrientationType.LANDSCAPE)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (orientation.equals(AGOrientationType.BOTH)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    public void detachMapFragment() {
        FragmentManager manager = getFragmentManager();
        Fragment mapFragment = manager.findFragmentByTag(AGMapFragment.TAG);
        if (mapFragment != null) {
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.remove(mapFragment);
            View view = mapFragment.getView();
            if (view instanceof ViewGroup) {
                final ViewGroup viewGroup = (ViewGroup) view;
                runOnUiThread(() -> {
                    viewGroup.removeAllViews();
                    viewGroup.setVisibility(View.INVISIBLE);
                });
            }
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    public boolean isGoogleMapSupported() {
        try {
            String map_key = getString(RWrapper.string.getMapKey());
            return map_key != null && map_key.length() > 0 && mConfigurationInfo.reqGlEsVersion >= 0x20000 &&
                    GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()) == ConnectionResult.SUCCESS;
        } catch (NoClassDefFoundError ex) {
            return false;
        }
    }

    @Override
    public void removeMainView() {
    }

    @Override
    public void addWebViewCallback(AGWebViewCallback pCallback) {
        mCallbacks.add(pCallback);
    }

    @Override
    public void removeWebViewCallback(AGWebViewCallback pCallback) {
        mCallbacks.remove(pCallback);
    }

    @Override
    public void onBackPressed() {
        if (OverlayManager.getInstance().isOverlayShown()) {
            OverlayManager.getInstance().hideCurrentOverlay(mSystemDisplay);
            return;
        }

        if (AGApplicationState.getInstance().getScreenLoader() != null && AGApplicationState.getInstance().getScreenLoader().loadPreviousScreen(AGScreenTransition.NONE))
            return;

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        KinetiseApplication.getInstance().logToCrashlytics("[KA] onPause");
        if (mSystemInitialized && mSystemDisplay != null) {
            AGScreenDataDesc currentScreen = mSystemDisplay.getCurrentScreen();
            if (currentScreen != null) {
                FeedManager.stopLoadingFeeds(currentScreen);
            }
        }
        Logger.v(this, "onPause");
        if (mSystemDisplay != null) {
            mSystemDisplay.pauseBackgroundVideo();
        }
        //we need to check this so facebook application wont hang, because it need timers to run
        if (!FacebookService.getInstance().isFacebookAppEnabled()) {
            for (AGWebViewCallback callback : mCallbacks) {
                callback.pause();
            }
        }
        AGApplicationState.getInstance().onPause();
        mUiLifecycleHelper.onPause();
        unregisterNetworkStateReceiver();

        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d(this, "onStop", "OnStop called");
        KinetiseApplication.getInstance().logToCrashlytics("[KA] onStop");
        AGApplicationState.getInstance().saveFontSizeMultiplier();
        RedirectMap.serialize();
        PropertyStorage.serialize();
        Synchronizer.serialize();
        DataFeedsMap.serialize();
        DataFeedDatabase.serialize();
        VariableStorage.serialize();
        if (PermissionManager.getInstance().hasGpsPermission()) {
            LocationHelper.getInstance().stopLocationSearching();
        }
    }

    @Override
    public void onDestroy() {
        KinetiseApplication.getInstance().logToCrashlytics("[KA] onDestroy");
        if (mSystemDisplay != null)
            mSystemDisplay.blockScreenWithLoadingDialog(false);
        AssetsManager.getInstance().destroy();

        PdfManager.getInstance().cleanTempFiles(getApplicationContext());
        mInstances--;

        if (mInitTask != null) {
            mInitTask.cancel();
        }
        ThreadPool.getInstance().shutdown();

        if (mAppStateSaved) {
            mAppStateSaved = false;
        } else {
            AppSaveStateHelper.clearCacheDir(getApplicationContext());
        }
        PhotoSetter.setClickedPhotoButtonView(null);
        CodeScannerSetter.setClickedCodeScannerView(null);
        GetPhoneContactSetter.setClickedGetPhoneContactView(null);

        super.onDestroy();
        mUiLifecycleHelper.onDestroy();
        if (mInstances <= 0 && mSystemDisplay != null) {
            mInstances = 0;
            mSystemDisplay.destroy();
        }
        DateUpdater.getInstance().cancelTimeUpdates();

        clearSingletons();
        OpenGalleryApp.removeSavedPhotos();
    }

    private void clearSingletons() {
        AGApplicationState.clearInstance();
        AppPackageManager.clearInstance();
        PdfManager.clearInstance();
        TwitterService.clearInstance();
        VariableStorage.clearInstance();
        FullscreenWebview.clearInstance();
        ExceptionManager.clearInstance();
        AssetsManager.clearInstance();
        FontLibrary.clearInstance();
        LanguageManager.clearInstance();
        AnalyticsManager.clearInstance();
        FacebookService.clearInstance();
        OkHttpClientManager.clearInstance();
        RedirectMap.clearInstance();
        LocationHelper.clearInstance();
        DateUpdater.clearInstance();
        BasicAuthLoginManager.clearInstance();
        OverlayManager.clearInstance();
        PropertyStorage.clearInstance();
        Synchronizer.clearInstance();
        DataFeedsMap.clearInstance();
        PermissionManager.clearInstance();
        ScrollManager.clearInstance();
        FontSizeHelper.clearInstance();
        CalcManager.clearCalculatesInstances();
    }

    @Override
    protected void onResume() {
        super.onResume();
        KinetiseApplication.getInstance().logToCrashlytics("[KA] onResume");
        Logger.v(this, "onResume");

        PermissionManager.getInstance().retrieveAccessTokens();

        mAppStateSaved = false;
        mUiLifecycleHelper.onResume();
        mWasResumed = true;
        //also we need to check here if we realy can stop timers for webbrowser
        if (!FacebookService.getInstance().isFacebookAppEnabled()) {
            for (AGWebViewCallback callback : mCallbacks) {
                callback.resume();
            }
        }

        if (AGApplicationState.getInstance() != null) {
            AGApplicationState.getInstance().onResume();
        }
        SystemDisplay.blockScreenWithLoadingDialog(false, this);
        preventScrollsReset(Resources.getSystem().getConfiguration().orientation, false);
        if (mSystemDisplay != null) {
            AGApplicationState.getInstance().onCloseExternalApplication();
            mSystemDisplay.startBackgroundVideo();
        }

        ShareData.clearShareData();

        if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            registerNetworkStateReceiver();
            PopupManager.showToast(LanguageManager.getInstance().getString(LanguageManager.ERROR_NO_CONNECTION));
        } else {
            if (mSystemDisplay != null) {
                AGScreenDataDesc currentScreen = mSystemDisplay.getCurrentScreen();
                if (currentScreen != null) {
                    FeedManager.saveFeedsDataOfAllFeedsInside(currentScreen);
                    FeedManager.startLoadingFeeds(currentScreen, false, true);
                }
            }
        }

    }

    private void registerNetworkStateReceiver() {
        mNetworkStateReciever = new NetworkStateReceiver();
        final IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mNetworkStateReciever, filters);
    }

    private void unregisterNetworkStateReceiver() {
        if (mNetworkStateReciever != null) {
            unregisterReceiver(mNetworkStateReciever);
            mNetworkStateReciever = null;
        }
    }

    private boolean preventScrollsReset(int orientation, boolean prevent) {
        if (prevent && mWasResumed) {
            mWasResumed = false;
            return mOnResumeOrientation != orientation;
        } else {
            mOnResumeOrientation = orientation;
            return false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        KinetiseApplication.getInstance().logToCrashlytics("[KA] onConfigurationChange");
        View currentFocus;
        if (getCurrentFocus() != null) {
            closeKeyboard();

            currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
            }
            mLastFocus = currentFocus;
        }

        super.onConfigurationChanged(newConfig);
    }

    private boolean showKeyboardIfApplies(View view) {
        if (view == null) {
            return false;
        }

        if (view instanceof EditText) {
            view.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
            imm.restartInput(view);
            return true;
        }

        return false;
    }

    /**
     * Why it is used: Tablets dont check if size is system managed screen size so they give results from getWindowManager().getDefaultDisplay(); as
     * same as mMainDisplay.getWidth() and mMainDisplay.getHeight();
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onMeasure(int width, int height) {
        Display androidDisplay = getWindowManager().getDefaultDisplay();
        if (mSystemDisplay == null || (mSystemDisplay.getWidth() == width && mSystemDisplay.getHeight() == height)) {
            return;
        }

        boolean check = true;
        if (!isTablet(getApplicationContext())) {
            check = (width != androidDisplay.getWidth() || height != androidDisplay.getHeight());
        }
        if (mSystemInitialized && check) {
            showKeyboardIfApplies(mLastFocus);
            mSystemDisplay.setDisplaySize(width, height);
            mSystemDisplay.recalculateAndLayoutScreen();

            OverlayManager.getInstance().recalculateOverlays(mSystemDisplay);

            if (mLastFocus != null) {
                mLastFocus.requestFocus();
                FrameLayout mainLayout = (FrameLayout) findViewById(RWrapper.id.main);
                scrollToView(mainLayout, mLastFocus);
            }
            mLastFocus = null;
        }
    }

    public void scrollToView(FrameLayout mainLayout, View view) {
        AGScreenView screenView = (AGScreenView) mainLayout.getChildAt(0);
        if (screenView != null) {
            AGBodyView body = screenView.getBodyView();
            body.scrollToDeepChild(view);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        KinetiseApplication.getInstance().logToCrashlytics("[KA] onSaveInstanceState");
        Logger.v(this, "onSaveInstanceState");
        mUiLifecycleHelper.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putString(mFilePathKey, OpenGalleryApp.getFilePath());
            AGPhotoView photoView = PhotoSetter.getClickedPhotoButtonView();
            if (photoView != null) {
                outState.putInt(PHOTO_VIEW_ID_BUNDLE_KEY, photoView.getId());
            }
            AGCodeScannerView scannerView = CodeScannerSetter.getClickedCodeScannerView();
            if (scannerView != null) {
                String codeScannerKey = "CodeScannerViewId";
                outState.putInt(codeScannerKey, scannerView.getId());
            }
            AGGetPhoneContactView getPhoneContactView = GetPhoneContactSetter.getClickedGetPhoneContactView();
            if (getPhoneContactView != null) {
                String getPhoneContactKey = "GetPhoneContactViewId";
                outState.putInt(getPhoneContactKey, getPhoneContactView.getId());
            }
            mAppStateSaved = true;
            if (getResources().getBoolean(RWrapper.bool.app_state_saving)) {
                try {
                    AppSaveStateHelper.saveApplicationState(getApplicationContext());
                    AppSaveStateHelper.saveScreenHistoryManager(getApplicationContext());
                    AppSaveStateHelper.saveAlterApiSessionId(outState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null && getResources().getBoolean(RWrapper.bool.app_state_saving)) {
            if (savedInstanceState.containsKey(mFilePathKey)) {
                OpenGalleryApp.setFilePath((String) savedInstanceState.get(mFilePathKey));
            }
            mAppStateSaved = true;
        }
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        KinetiseApplication.getInstance().logToCrashlytics("[KA] onActivityResult, requestCode: " + requestCode + ", resultCode: " + resultCode);
        if (requestCode == FullscreenVideoActivity.FULLSCREEN_VIDEO_REQUEST_CODE && data != null) {
            restoreVideoState(data);
        }
        if (requestCode == OpenGalleryApp.PHOTO_CAPTURE) {
            setResultPhoto(resultCode);
        }
        if (requestCode == OpenGalleryApp.PHOTO_GALLERY) {
            setResultGallery(data);
        }
        if (requestCode == ScanCodeActivity.REQUEST_SCAN_CODE_CONTROL) {
            setResultQRCode(resultCode, data);
        }
        if (requestCode == GetPhoneContactSetter.REQUEST_GET_PHONE_CONTACT) {
            GetPhoneContactSetter.setPhoneContact(resultCode, data, KinetiseActivity.this);
        }
        if (requestCode == GoogleService.REQUEST_GOOGLE_LOGIN || requestCode == GoogleService.REQUEST_GOOGLE_ERROR) {
            GoogleService.getInstance().onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
        Session session = Session.getActiveSession();
        if (session != null) {
            session.onActivityResult(KinetiseActivity.this, requestCode, resultCode, data);
        }
    }

    private void setResultGallery(Intent data) {
        if (data == null)
            return;
        final Uri uri = data.getData();
        if (uri != null) {
            PhotoSetter.setPhotoBackground(uri, getContentResolver());
        }
    }

    private void restoreVideoState(Intent data) {
        String viewId = data.getStringExtra(FullscreenVideoActivity.REQUESTING_VIEW_ID_KEY);
        int currentPosition = data.getIntExtra(FullscreenVideoActivity.CURRENT_VIDEO_POSITION_KEY, 0);
        boolean isPlaying = data.getBooleanExtra(FullscreenVideoActivity.IS_PLAYING_KEY, true);

        FullscreenVideoBridge.getInstance().notifyVideoViewById(viewId, currentPosition, isPlaying);
    }

    private void setResultPhoto(int resultCode) {
        if (OpenGalleryApp.getFilePath() == null || resultCode != RESULT_OK)
            return;

        if (mSystemInitialized) {
            PhotoSetter.setPhotoButtonView(OpenGalleryApp.getFilePath());
        } else {
            mOnMainViewSet = () -> {
                PhotoSetter.setPhotoButtonView(OpenGalleryApp.getFilePath());
                mOnMainViewSet = null;
            };
        }
    }

    private void setResultQRCode(final int resultCode, final Intent data) {
        if (mSystemIsInitializated) {
            CodeScannerSetter.setScannedCode(resultCode, data);
        } else {
            mOnMainViewSet = () -> {
                CodeScannerSetter.setScannedCode(resultCode, data);
                mOnMainViewSet = null;
            };
        }
    }


    void setPermissionRequestListener(IPermissionRequestListener permissionRequestListener) {
        if (permissionRequestListener != null) {
            if (mPermissionRequestListeners == null)
                mPermissionRequestListeners = new ArrayList<>();
            this.mPermissionRequestListeners.add(permissionRequestListener);
        } else
            mPermissionRequestListeners = null;
    }

    private ArrayList<IPermissionRequestListener> getPermissionRequestListener() {
        return this.mPermissionRequestListeners;
    }

    @Override
    public void onPermissionLack(int requestCode, IPermissionRequestListener permissionRequestListener) {
        setPermissionRequestListener(permissionRequestListener);
        String request = null;
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                request = Manifest.permission.CAMERA;
                break;
            case READ_CONTACTS_REQUEST_CODE:
                request = Manifest.permission.READ_CONTACTS;
                break;
            case WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
                request = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                break;
            case ACCESS_FINE_LOCATION_REQUEST_CODE:
                request = Manifest.permission.ACCESS_FINE_LOCATION;
                break;
        }

        if (request != null)
            if (!waitingForPermissionResponse.contains(request)) {
                ActivityCompat.requestPermissions(this, new String[]{request}, requestCode);
                waitingForPermissionResponse.add(request);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        for (String permission : permissions) {
            waitingForPermissionResponse.remove(permission);
        }
        ArrayList<IPermissionRequestListener> mPermissionRequestListener = getPermissionRequestListener();
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && mPermissionRequestListener != null) {
            for (IPermissionRequestListener permission : mPermissionRequestListener)
                permission.onPermissionGranted();
        } else {
            if (mPermissionRequestListener != null)
                for (IPermissionRequestListener permission : mPermissionRequestListener)
                    permission.onPermissionDenied();
        }
        setPermissionRequestListener(null);
    }

}

