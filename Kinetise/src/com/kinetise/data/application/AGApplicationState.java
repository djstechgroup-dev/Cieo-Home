package com.kinetise.data.application;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.application.feedmanager.DataFeedsMap;
import com.kinetise.data.application.loginmanager.BasicAuthLoginManager;
import com.kinetise.data.application.loginmanager.LoginManager;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.application.screenhistory.ApplicationState;
import com.kinetise.data.application.screenhistory.ScreenHistoryManager;
import com.kinetise.data.application.screenloader.ScreenLoader;
import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.ApplicationDescriptionDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGErrorDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGLoadingDataDesc;
import com.kinetise.data.descriptors.datadescriptors.OverlayDataDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.ResetScrollAndFeedVisitor;
import com.kinetise.data.descriptors.helpers.DataDescHelper;
import com.kinetise.data.descriptors.types.AGScreenTransition;
import com.kinetise.data.packagemanager.AppPackage;
import com.kinetise.data.packagemanager.AppPackageManager;
import com.kinetise.data.parsermanager.ParserManager;
import com.kinetise.data.sourcemanager.BitmapCache;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.helpers.asynccaller.AsyncCaller;
import com.kinetise.helpers.http.OkHttpClientManager;
import com.kinetise.helpers.http.RedirectMap;
import com.kinetise.helpers.preferences.SecurePreferencesHelper;
import com.kinetise.helpers.regexp.RegexpHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AGApplicationState implements IAGApplication {
    protected static final String SHARED_PREFERENCE_FONT_SIZE = "FONT_SIZE_MULTIPLIER";
    protected static final float DEFAULT_FONT_MULTIPLIER = 1.0f;

    protected static AGApplicationState mInstance;

    protected Context mContext;

    protected LoginManager mLoginManager;
    protected ScreenLoader mScreenLoader;
    protected AlterApiManager mAlterApiManager;
    protected ScreenHistoryManager mHistoryManager;

    protected SystemDisplay mSystemDisplay;
    protected AGScreenDataDesc mCurrentScreenDesc;
    protected ApplicationDescriptionDataDesc mApplicationDescriptionDataDesc;
    protected ApplicationState mApplicationState;

    protected List<IAGApplicationStateListener> mStateListeners;

    protected boolean mExternalApplicationOpened;
    protected boolean mPaused = false;
    protected float mFontSizeMultiplier = DEFAULT_FONT_MULTIPLIER;

    public static AGApplicationState getInstance() {
        if (mInstance == null) {
            synchronized (AGApplicationState.class) {
                if (mInstance == null) {
                    mInstance = new AGApplicationState();
                }
            }
        }
        return mInstance;
    }

    protected AGApplicationState() {
        mStateListeners = new ArrayList<>();
    }

    @Override
    public Activity getActivity() {
        if (mSystemDisplay == null)
            return null;
        return mSystemDisplay.getActivity();
    }

    public void initInstance(Context context, SystemDisplay systemDisplay) {
        mContext = context;

        mLoginManager = new LoginManager();
        mScreenLoader = new ScreenLoader();
        mAlterApiManager = new AlterApiManager();
        mHistoryManager = new ScreenHistoryManager();

        mSystemDisplay = systemDisplay;
        restoreFontSizeMultipier();

        if (ParserManager.getInstance().getIAGParser() != null) {
            if (!ParserManager.getInstance().isPrepared())
                ParserManager.getInstance().prepare(null);
            mApplicationDescriptionDataDesc = ParserManager.getInstance().getApplicationDescription();
        }

        RegexpHelper.loadConfig();
        BasicAuthLoginManager.getInstance().retrieveAuthenticationTokenFromFile();
    }

    public void addStateListener(IAGApplicationStateListener listener) {
        mStateListeners.add(listener);
    }

    public void removeStateListener(IAGApplicationStateListener listener) {
        mStateListeners.remove(listener);
    }

    public static boolean isLoginScreen(String screenId) {
        ApplicationDescriptionDataDesc applicationDescriptionDataDesc = mInstance.getApplicationDescription();
        if (applicationDescriptionDataDesc == null)
            return false;
        String loginScreenId = applicationDescriptionDataDesc.getLoginScreenId();
        if (screenId == null || loginScreenId == null)
            return false;
        return screenId.equals(loginScreenId);
    }

    public static boolean hasLoginScreen() {
        return getInstance().getApplicationDescription().hasLoginScreen();
    }

    public static boolean isSplashScreen(String screenId) {
        ApplicationDescriptionDataDesc applicationDescriptionDataDesc = mInstance.getApplicationDescription();
        if (applicationDescriptionDataDesc == null)
            return false;
        String loginScreenId = applicationDescriptionDataDesc.getLoginScreenId();
        String startScreenId = applicationDescriptionDataDesc.getStartScreenId();
        if (loginScreenId != null) {
            return false;
        }
        if (startScreenId == null || screenId == null)
            return false;
        return screenId.equals(startScreenId);
    }

    public void startLoadingFirstScreen() {
        if (mApplicationDescriptionDataDesc != null) {
            mApplicationDescriptionDataDesc.resolveDynamicFields();
            String startScreenId = getFirstScreen();
            mScreenLoader.loadNextScreen(startScreenId, AGScreenTransition.NONE);
            mSystemDisplay.blockScreenWithLoadingDialog(false);
        }
    }

    public String getFirstScreen() {
        String startScreenId;
        if (isUserLoggedIn())
            startScreenId = mApplicationDescriptionDataDesc.getMainScreenId();
        else
            startScreenId = mApplicationDescriptionDataDesc.getStartScreenId();
        return startScreenId;
    }

    public String getFirstScreenNoSplash() {
        String startScreenId;
        if (isUserLoggedIn() || mApplicationDescriptionDataDesc.hasSplashScreen())
            startScreenId = mApplicationDescriptionDataDesc.getMainScreenId();
        else
            startScreenId = mApplicationDescriptionDataDesc.getStartScreenId();
        return startScreenId;
    }

    public boolean isUserLoggedIn() {
        return mLoginManager.isUserLoggedIn();
    }

    public void logoutUser() {
        logoutUser(null);
    }

    public void logoutUser(String screenId) {
        clearLoginData();
        if (screenId == null) {
            screenId = mApplicationDescriptionDataDesc.getLoginScreenId();
        }
        final String nextScreenId = screenId;

        if (nextScreenId != null) {
            AsyncCaller.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mHistoryManager.clear();
                    mScreenLoader.loadNextScreen(nextScreenId, AGScreenTransition.NONE, null, null, null, false);
                }
            });
        }
    }

    public void clearLoginData() {
        mLoginManager.clearLoginData();
        BitmapCache.getInstance().clear();
        DataFeedsMap.getInstance().clear();
        OkHttpClientManager.getInstance().clear();
        RedirectMap.getInstance().clear();

        //clear feed clients  and scrolls
        ResetScrollAndFeedVisitor visitor = new ResetScrollAndFeedVisitor();
        for (Map.Entry<String, AGScreenDataDesc> entry : ParserManager.getInstance().getScreenMap().entrySet()) {
            AGScreenDataDesc screen = entry.getValue();
            screen.accept(visitor);
        }

    }

    @Override
    public ScreenLoader getScreenLoader() { //TODO - a może lepiej w takim razie ScreenLoader zrobić singletonem? jest duużo odwołań "AGApplicationState.getInstance().getScreenLoader()."
        return mScreenLoader;
    }

    @Override
    public SystemDisplay getSystemDisplay() {
        return mSystemDisplay;
    }

    @Override
    public AlterApiManager getAlterApiManager() {
        return mAlterApiManager;
    }

    @Override
    public ScreenHistoryManager getHistoryManager() {
        return mHistoryManager;
    }

    @Override
    public ApplicationState getApplicationState() {
        return mApplicationState;
    }

    @Override
    public void setApplicationState(ApplicationState appState) {
        mApplicationState = appState;
    }

    @Override
    public AGScreenDataDesc getCurrentScreenDesc() {
        return mCurrentScreenDesc;
    }

    @Override
    public ApplicationDescriptionDataDesc getApplicationDescription() {
        return mApplicationDescriptionDataDesc;
    }

    @Override
    public AGErrorDataDesc getErrorDataDesc(int width, int height) {
        AppPackage appPackage = AppPackageManager.getInstance().getPackage();
        String error = "";
        if (appPackage != null) {
            error = appPackage.getErrorPlaceholderPath();
        }
        return DataDescHelper.getErrorDataDesc(error, width, height);
    }

    @Override
    public AGLoadingDataDesc getLoadingDataDesc(int width, int height) {
        return DataDescHelper.createLoadingDataDesc(width, height);
    }

    @Override
    public synchronized AGScreenDataDesc getScreenDesc(String screenId) {
        mCurrentScreenDesc = ParserManager.getInstance().getScreenDescriptor(screenId);
        return mCurrentScreenDesc;
    }

    @Override
    public OverlayDataDesc getOverlayDataDesc(String overlayId) {
        return ParserManager.getInstance().getApplicationOverlays().get(overlayId);
    }

    public float getFontSizeMultiplier() {
        return mFontSizeMultiplier;
    }

    public void increaseFontSizeMultiplier(float delta) {
        float maxFontSizeMultiplier = mApplicationDescriptionDataDesc.getMaxFontSizeMultiplier();
        float minFontSizeMultiplier = mApplicationDescriptionDataDesc.getMinFontSizeMultiplier();
        mFontSizeMultiplier += delta;
        if (mFontSizeMultiplier > maxFontSizeMultiplier)
            mFontSizeMultiplier = maxFontSizeMultiplier;
        if (mFontSizeMultiplier < minFontSizeMultiplier)
            mFontSizeMultiplier = minFontSizeMultiplier;
    }

    public void decreaseFontSizeMultiplier(float delta) {
        increaseFontSizeMultiplier(-delta);
    }

    public void onOpenExternalApplication() {
        mExternalApplicationOpened = true;
    }

    public void onCloseExternalApplication() {
        mExternalApplicationOpened = false;
    }

    public static void clearInstance() {
        mInstance = null;
    }

    public void saveFontSizeMultiplier() {
        SharedPreferences.Editor editor = SecurePreferencesHelper.getApplicationSettings().edit();
        editor.putFloat(SHARED_PREFERENCE_FONT_SIZE, mFontSizeMultiplier).apply();
    }

    private void restoreFontSizeMultipier() {
        SharedPreferences sp = SecurePreferencesHelper.getApplicationSettings();
        mFontSizeMultiplier = (sp == null) ? DEFAULT_FONT_MULTIPLIER : sp.getFloat(SHARED_PREFERENCE_FONT_SIZE, DEFAULT_FONT_MULTIPLIER);
    }

    public void setSystemDisplay(SystemDisplay systemDisplay) {
        mSystemDisplay = systemDisplay;
    }

    public void onPause() {
        mPaused = true;
        PopupManager.onAppPaused();
        for (IAGApplicationStateListener listener : mStateListeners) {
            if (listener != null) {
                listener.onPause();
            }
        }
    }

    public void onResume() {
        mPaused = false;
        RegexpHelper.loadConfig();
        for (IAGApplicationStateListener listener : mStateListeners) {
            if (listener != null) {
                listener.onResume();
            }
        }
    }

    public boolean isPaused() {
        return mPaused;
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public Context getContext() {
        if (mContext != null)
            return mContext;
        else if (KinetiseApplication.getInstance() != null)
            return KinetiseApplication.getInstance().getApplicationContext();
        else
            return null;
    }

    public void setScreenLoader(ScreenLoader screenLoader) {
        mScreenLoader = screenLoader;
    }

    public void setLoginManager(LoginManager loginManager) {
        mLoginManager = loginManager;
    }

    public void setApplicationDescriptionDataDesc(ApplicationDescriptionDataDesc desc) {
        mApplicationDescriptionDataDesc = desc;
    }
}
