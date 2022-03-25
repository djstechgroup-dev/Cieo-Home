package com.kinetise.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.helpers.preferences.SecurePreferencesHelper;
import com.kinetise.support.logger.Logger;
import com.kinetise.views.FullscreenWebview;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class SalesForceHelper {
    private static final String SHARED_PREFERENCE_SALESFORCE_TOKEN = "SalesForceToken";

    public static String mSalesForceToken;

    public static void setToken(String token) {
        mSalesForceToken = token;
        saveToSharedPreferences();
    }

    public static String getToken() {
        if(mSalesForceToken==null){
            mSalesForceToken=retrieveFromPreferences();
        }
        return mSalesForceToken;
    }

    public static void clearToken() {
        mSalesForceToken = "";
    }

    public static String retrieveFromPreferences() {
        Context context = AGApplicationState.getInstance().getContext();
        SharedPreferences sp = SecurePreferencesHelper.getUserData();
        return sp.getString(SHARED_PREFERENCE_SALESFORCE_TOKEN, "");
    }

    public static void saveToSharedPreferences() {
        Context context = AGApplicationState.getInstance().getContext();
        SharedPreferences sp =  SecurePreferencesHelper.getUserData();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SHARED_PREFERENCE_SALESFORCE_TOKEN,mSalesForceToken).apply();
    }

    public static void loginToSalesForce(Activity activity, String loginUrl, Map<String, String> httpParams, Map<String, String> headerParams, String redirectUrl, LoginCallback callback){
        FullscreenWebview fullscreenWebview = FullscreenWebview.getInstance();
        String url = AssetsManager.addHttpQueryParams(loginUrl, httpParams);
        fullscreenWebview.showWebView(new LoginWebClient(redirectUrl, callback), url, headerParams, activity);
    }

    private static class LoginWebClient extends WebViewClient {

        private final String mRedirectUrlWithAccessToken;
        private final LoginCallback mLoginCallback;

        public LoginWebClient(String redirectUrl, LoginCallback loginCallback) {
            StringBuilder builder = new StringBuilder();
            builder.append(redirectUrl);
            builder.append("#");
            builder.append("access_token=");
            mRedirectUrlWithAccessToken = builder.toString();
            mLoginCallback = loginCallback;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Logger.d("SalesForceRedirectUrl", url);
            if (url.startsWith(mRedirectUrlWithAccessToken)) {
                int tokenStart = url.indexOf("=");
                int tokenEnd = url.indexOf("&", tokenStart);

                String accessToken = url.substring(tokenStart + 1, tokenEnd);
                try {
                    accessToken = URLDecoder.decode(accessToken, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mLoginCallback.onLoginSuccess(accessToken);
                FullscreenWebview.getInstance().closeWebView();
            }
            return false;
        }
    }
}
