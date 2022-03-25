package com.kinetise.helpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.helpers.http.HttpRequestManager;
import com.kinetise.helpers.http.NetworkUtils;
import com.kinetise.helpers.http.TextResponseRequestCallback;
import com.kinetise.helpers.threading.ThreadPool;
import com.kinetise.helpers.threading.UncancelableTask;
import com.kinetise.views.FullscreenWebview;

import java.util.Set;

public class LinkedinService {
    //region Constants
    private static final String LINKEDIN_LOGIN_URL = "https://www.linkedin.com/uas/oauth2/authorization?response_type=code&client_id=%s&state=V0qJbYDfPaIXzjSNORFk&redirect_uri=%s";
    private static final String REDIRECT_URI = "http://localhost";
    private static final String CODE_NAME = "code";
    private static final String ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/accessToken?grant_type=authorization_code&code=%s&redirect_uri=%s&client_id=%s&client_secret=%s";
    //endregion

    //region Instance fields
    private LoginCallback mLoginCallback;
    private final Activity mActivity;
    /**
     * Fix for onPageStarted method in LoginWebClient being called twice
     **/
    private boolean mAuthorizationCodeRetrieved;
    //endregion

    //region Static fields
    private static String sLastAuthorizationCode;
    private static String sLastAccessToken;
    //endregion

    public LinkedinService(Activity activity) {
        mActivity = activity;
    }

    public static String getLastAccesToken() {
        return sLastAccessToken;
    }

    public void loginToLinkedIn(LoginCallback callback) {
        mLoginCallback = callback;
        showFullscreenWebview();
    }

    private void showFullscreenWebview() {
        FullscreenWebview fullscreenWebview = FullscreenWebview.getInstance();
        fullscreenWebview.showWebView(new LoginWebClient(), createAuthorizationCodeUrl(mActivity), null, mActivity);
    }

    private String createAuthorizationCodeUrl(Activity activity) {
        String clientId = activity.getString(RWrapper.string.linkedin_key);
        return String.format(LINKEDIN_LOGIN_URL, clientId, REDIRECT_URI);
    }

    private String createAccessTokenUrl(Activity activity) {
        String clientId = activity.getString(RWrapper.string.linkedin_key);
        String clientSecret = activity.getString(RWrapper.string.linkedin_secret);
        return String.format(ACCESS_TOKEN_URL, sLastAuthorizationCode, REDIRECT_URI, clientId, clientSecret);
    }

    private boolean readAuthorizationCode(String url) {
        Uri uri = Uri.parse(url);
        Set<String> queryNames = uri.getQueryParameterNames();
        if (queryNames.contains(CODE_NAME)) {
            sLastAuthorizationCode = uri.getQueryParameter(CODE_NAME);
            return true;
        }

        loginFailed();
        return false;
    }

    private void requestAccessToken() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url = createAccessTokenUrl(mActivity);
                NetworkUtils.sendRequest(url, null, "", new TextResponseRequestCallback() {
                    @Override
                    public void onSuccess(String response) {
                        sLastAccessToken = readAccessToken(response);
                        mLoginCallback.onLoginSuccess(sLastAccessToken);
                    }

                    @Override
                    public void onFailed(HttpRequestManager requestManager, PopupMessage... messages) {
                        loginFailed();
                    }
                });
            }
        };
        ThreadPool.getInstance().executeBackground(new UncancelableTask(runnable));
    }

    protected String readAccessToken(String responseString) {
        try {
            String[] accessTokenStrings = responseString.split(",");
            accessTokenStrings[0] = accessTokenStrings[0].substring(1);
            String lastValue = accessTokenStrings[accessTokenStrings.length - 1];
            accessTokenStrings[accessTokenStrings.length - 1] = lastValue.substring(0, lastValue.length() - 1);
            String[] pair;
            String key, value;
            for (String node : accessTokenStrings) {
                pair = node.split(":");
                key = pair[0];
                key = key.substring(1, key.length() - 1);
                value = pair[1];
                value = value.substring(1, value.length() - 1);
                if (key.compareTo("access_token") == 0)
                    return value;
            }
        } catch (Exception e) {

        }
        return "";
    }

    private void loginFailed() {
        SystemDisplay systemDisplay = AGApplicationState.getInstance().getSystemDisplay();
        if (systemDisplay != null)
            systemDisplay.blockScreenWithLoadingDialog(false);
    }

    //region Private classes
    private class LoginWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.startsWith(REDIRECT_URI) && mAuthorizationCodeRetrieved == false) {
                mAuthorizationCodeRetrieved = true;
                FullscreenWebview.getInstance().closeWebView();
                SystemDisplay systemDisplay = AGApplicationState.getInstance().getSystemDisplay();
                if (systemDisplay != null)
                    systemDisplay.blockScreenWithLoadingDialog(true);
                if (readAuthorizationCode(url))
                    requestAccessToken();
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    }

    //endregion
}
