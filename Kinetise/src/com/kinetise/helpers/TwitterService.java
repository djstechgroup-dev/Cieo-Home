package com.kinetise.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.helpers.http.HttpRequestManager;
import com.kinetise.helpers.http.NetworkUtils;
import com.kinetise.helpers.http.TextResponseRequestCallback;
import com.kinetise.helpers.preferences.SecurePreferencesHelper;
import com.kinetise.helpers.threading.ThreadPool;
import com.kinetise.helpers.threading.UncancelableTask;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class TwitterService {

    public class TwitterResponse
    {
        public String token_type;

        public String access_token;
    }

    private static final double MIN_TOKEN_UPDATE_DELAY = 5 * 60 * 1000; //5 minutes

    private static final String SHARED_PREFERENCE_TWITTER_TOKEN = "sharedTwitterToken";
    private static final String TOKEN_URL = "https://api.twitter.com/oauth2/token";
    private static final String INVALIDATE_TOKEN_URL = "https://api.twitter.com/oauth2/invalidate_token";
    private static final String REQUEST_BODY = "grant_type=client_credentials";
    private TwitterResponse mTwitterResponse;

    private long mLastTokenUpdate;
    private String mAccessToken = "";
    private Context mContext;
    private static TwitterService mInstance;

    private TwitterService(Context context) {
        mContext = context;
    }

    private TwitterService(){}

    public static TwitterService getInstance() {
        if (mInstance == null) {
            synchronized (TwitterService.class) {
                if (mInstance == null) {
                    mInstance = new TwitterService(AGApplicationState.getInstance().getContext());
                }
            }
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    public void retrieveAccessToken() {
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            final long currentTime = System.currentTimeMillis();
            if (currentTime-mLastTokenUpdate > MIN_TOKEN_UPDATE_DELAY) {
                mLastTokenUpdate = currentTime;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        NetworkUtils.sendRequest(TOKEN_URL, getTwitterHeaders(), REQUEST_BODY, HttpRequestManager.RequestType.OTHER, new TextResponseRequestCallback() {
                            @Override
                            public void onFailed(HttpRequestManager requestManager, PopupMessage... messages) {
                                mLastTokenUpdate = 0;
                            }

                            @Override
                            public void onSuccess(String response) {
                                mAccessToken = "Bearer " + parseTwitterResponse(response);
                                final SharedPreferences preferences = SecurePreferencesHelper.getUserData();
                                preferences.edit().putString(SHARED_PREFERENCE_TWITTER_TOKEN, mAccessToken).apply();

                            }
                        });
                    }
                };

                ThreadPool.getInstance().executeBackground(new UncancelableTask(runnable));
            }
        } else {
            SharedPreferences preferences = SecurePreferencesHelper.getUserData();
            if (preferences.contains(SHARED_PREFERENCE_TWITTER_TOKEN)) {
                mAccessToken = preferences.getString(SHARED_PREFERENCE_TWITTER_TOKEN, null);
            }
        }
    }

    protected String parseTwitterResponse(String responseString) {
        String accessTokenPart = responseString.split(",")[1];
        String accessTokenWithQuotesAndParenthasis = accessTokenPart.split(":")[1];
        return accessTokenWithQuotesAndParenthasis.substring(1,accessTokenWithQuotesAndParenthasis.length()-2);
    }

    public void setTwitterResponse(TwitterResponse twitterResponse){
        mTwitterResponse = twitterResponse;
    }

    private Map<String, String> getTwitterHeaders() {
        Map<String, String> headers = new HashMap<>();

        String keyAndSecret = String.format("%s:%s", mContext.getResources().getString(RWrapper.string.twitter_key), mContext.getResources().getString(RWrapper.string.twitter_secret));
        String base64;
        try {
            base64 = Base64.encodeToString(keyAndSecret.getBytes("UTF-8"), Base64.DEFAULT);
            base64 = base64.replace("\n","");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            base64 = "";
        }

        headers.put("Authorization", "Basic " + base64);
        headers.put("User-Agent", "Kinetise");
        headers.put("Accept", "*/*");

        return headers;
    }

    public String getAccesToken() {
        return mAccessToken;
    }

}
