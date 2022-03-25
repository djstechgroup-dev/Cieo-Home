package com.kinetise.data.application.alterapimanager;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.helpers.asynccaller.AsyncCaller;
import com.kinetise.helpers.http.CacheControlOptions;
import com.kinetise.helpers.http.HttpRequestManager;
import com.kinetise.helpers.http.NetworkUtils;
import com.kinetise.helpers.http.OkHttpClientManager;
import com.kinetise.helpers.threading.AGAsyncTask;

import java.util.Map;

public class AbstractRequestRunnable extends AGAsyncTask implements NetworkUtils.RequestCallback {

    protected IRequestCallback mCallback;
    protected HttpRequestManager.RequestType mRequestType;
    protected String mUrl;
    protected HttpRequestManager mRequestManager;
    protected Map<String, String> mRequestHeaders;
    protected AGHttpMethodType mMethodType;
    protected String mPostBody;
    protected String mHttpResponseTransform;

    public AbstractRequestRunnable(AGHttpMethodType httpMethod, String url, String postBody, Map<String, String> headers, String responseTransform, HttpRequestManager.RequestType requestType, IRequestCallback callback) {
        mUrl = url;
        mRequestType = requestType;
        mRequestHeaders = headers;
        mPostBody = postBody;
        mHttpResponseTransform = responseTransform;
        mMethodType = httpMethod;
        mCallback = callback;
    }

    protected void runRequest() {
        NetworkUtils.sendRequest(OkHttpClientManager.getInstance().getClient(), mMethodType, mUrl, mRequestHeaders, mPostBody, mHttpResponseTransform, mRequestType, CacheControlOptions.NO_CACHE, this);
    }

    protected void reportErrorToCommand(final PopupMessage... messages) {
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null)
                    mCallback.onError(messages);
            }
        });
    }

    protected void reportSuccesToCommand(final PopupMessage... messages) {
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null)
                    mCallback.onSuccess(messages);
            }
        });
    }

    @Override
    public void run() {
        runRequest();
    }

    public String toString() {
        return "HttpAlterApiRequest - Thread";
    }

    @Override
    public void onFailed(HttpRequestManager requestManager, PopupMessage... messages) {
        reportErrorToCommand(messages);
    }

    @Override
    public void onResponse(HttpRequestManager requestManager) {
        AAResponse alterApiResponse = requestManager.getAlterApiResponse(mHttpResponseTransform);
        AlterApiManager.handleAAResponse(alterApiResponse);

        if (hasRequiredNodes(alterApiResponse)) {
            reportSuccesToCommand(HttpResponseHandler.getAlterApiMessages(alterApiResponse, LanguageManager.getInstance().getString(LanguageManager.POPUP_INFO_HEADER)));
        } else {
            PopupMessage[] messages = HttpResponseHandler.getAlterApiMessages(alterApiResponse, LanguageManager.getInstance().getString(LanguageManager.POPUP_ERROR_HEADER));
            if (messages.length > 0)
                reportErrorToCommand(messages);
            else
                reportErrorToCommand(new PopupMessage(true, HttpRequestManager.getErrorMessage(mRequestType)));
        }
    }

    @Override
    public void onLogout() {
        AGApplicationState.getInstance().logoutUser();
    }

    protected boolean hasRequiredNodes(AAResponse alterApiResponse) {
        return true;
    }
}

