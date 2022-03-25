package com.kinetise.data.application.loginmanager;

import android.content.Context;
import android.content.SharedPreferences;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.alterapimanager.AbstractRequestRunnable;
import com.kinetise.data.application.alterapimanager.IRequestCallback;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.helpers.encoding.Base64;
import com.kinetise.helpers.http.HttpRequestManager;
import com.kinetise.helpers.preferences.SecurePreferencesHelper;
import com.kinetise.helpers.threading.ThreadPool;

import java.util.HashMap;

public class BasicAuthLoginManager implements IRequestCallback {
    private final static String TOKEN_NAME_IN_FILE = "authenticationToken";

    private static BasicAuthLoginManager mInstance;
    private String mAuthenticationToken;

    private IRequestCallback mCallback;

    private BasicAuthLoginManager() {
        retrieveAuthenticationTokenFromFile();
    }

    public static BasicAuthLoginManager getInstance() {
        if (mInstance == null) {
            synchronized (BasicAuthLoginManager.class) {
                if (mInstance == null) {
                    mInstance = new BasicAuthLoginManager();
                }
            }
        }

        return mInstance;
    }

    public static void clearInstance() {
        mInstance = null;
    }

    public void login(String url, String login, String password, IRequestCallback loginCallback) {
        AGApplicationState.getInstance().getSystemDisplay().blockScreenWithLoadingDialog(true);

        mAuthenticationToken = encodeLoginAndPassword(login, password);
        String authenticationHeaderValue = "Basic " + mAuthenticationToken;

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", authenticationHeaderValue);

        mCallback = loginCallback;

        AbstractRequestRunnable task = new AbstractRequestRunnable(AGHttpMethodType.GET, url, null, headers, null, HttpRequestManager.RequestType.BASIC_AUTH_LOGIN, this);
        ThreadPool.getInstance().execute(task);
    }

    public String getEmptyToken() {
        return encodeLoginAndPassword("", "");
    }

    private String encodeLoginAndPassword(String login, String password) {
        String token = login + ":" + password;
        return new String(Base64.encodeBase64(token.getBytes()));
    }

    public void saveAuthenticationTokenToFile() {
        Context context = AGApplicationState.getInstance().getContext();
        if (mAuthenticationToken != null && context != null) {
            SharedPreferences.Editor editor = SecurePreferencesHelper.getUserData().edit();
            editor.putString(TOKEN_NAME_IN_FILE, mAuthenticationToken).apply();
        }
    }

    public void retrieveAuthenticationTokenFromFile() {
        Context context = AGApplicationState.getInstance().getContext();
        if (context != null) {
            SharedPreferences preferences = SecurePreferencesHelper.getUserData();
            mAuthenticationToken = preferences.getString(TOKEN_NAME_IN_FILE, null);
        }
    }

    public String getAuthenticationToken() {
        return mAuthenticationToken;
    }

    public boolean isUserLoggedIn(){
        return mAuthenticationToken != null;
    }

    public void clearAuthenticationToken() {
        mAuthenticationToken = null;
        Context context = AGApplicationState.getInstance().getContext();
        if (context != null) {
            SharedPreferences.Editor editor = SecurePreferencesHelper.getUserData().edit();
            editor.remove(TOKEN_NAME_IN_FILE).apply();
        }
    }

    @Override
    public void onError(PopupMessage... errorMessages) {
        clearAuthenticationToken();
        SystemDisplay systemDisplay = AGApplicationState.getInstance().getSystemDisplay();
        if (systemDisplay != null)
            systemDisplay.blockScreenWithLoadingDialog(false);
        mCallback.onError(errorMessages);
        mCallback = null;
    }

    @Override
    public void onSuccess(PopupMessage... messages) {
        SystemDisplay systemDisplay = AGApplicationState.getInstance().getSystemDisplay();
        if (systemDisplay != null)
            systemDisplay.blockScreenWithLoadingDialog(false);
        saveAuthenticationTokenToFile();
        mCallback.onSuccess(messages);
        mCallback = null;
    }
}
