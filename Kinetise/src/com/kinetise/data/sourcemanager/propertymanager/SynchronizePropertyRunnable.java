package com.kinetise.data.sourcemanager.propertymanager;

import com.kinetise.data.application.alterapimanager.AAResponse;
import com.kinetise.data.application.alterapimanager.AGOkHttpConfigurator;
import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.helpers.http.HttpRequestManager;
import com.kinetise.helpers.http.NetworkUtils;
import com.kinetise.helpers.http.OkHttpClientManager;
import com.kinetise.helpers.threading.AGAsyncTask;
import com.squareup.okhttp.Request;

import java.util.Date;
import java.util.Map;

public class SynchronizePropertyRunnable extends AGAsyncTask {

    private final AGHttpMethodType mHttpMethod;
    private final String mResponseTransform;
    private String mUrl;
    private String mPostBody;
    private Map<String, String> mHeaderParams;
    private Synchronizer.SynchronizePropertyCallback mCallback;

    public SynchronizePropertyRunnable(AGHttpMethodType methodType, String url, String postBody, Map<String, String> headers, String responseTransform, Synchronizer.SynchronizePropertyCallback callback) {
        this.mUrl = url;
        this.mPostBody = postBody;
        this.mCallback = callback;
        this.mHeaderParams = headers;
        mHttpMethod = methodType;
        mResponseTransform = responseTransform;
    }

    @Override
    public void cancel() {
        super.cancel();
        mCallback.onCancel();
    }

    @Override
    public void run() {
        if (mIsCanceled) {
            return;
        }

        HttpRequestManager requestManager = new HttpRequestManager();
        try {
            Request.Builder builder;
            if (mHttpMethod == AGHttpMethodType.GET) {
                builder = AGOkHttpConfigurator.configureOkHttpRequestForGet(mUrl, mHeaderParams);
            } else {
                builder = AGOkHttpConfigurator.configureOkHttpRequestForPost(mUrl, mHeaderParams);
                AGOkHttpConfigurator.setPost(builder, mHttpMethod, HttpRequestManager.RequestType.SILENT, mPostBody);
            }

            requestManager.executeRequest(OkHttpClientManager.getInstance().getClient(), builder);
            int statusCode = requestManager.getStatusCode();
            Date date = NetworkUtils.getHeaderDate(requestManager.getHeaders());
            long timestamp;
            if (date == null) {
                timestamp = new Date().getTime();
            } else {
                timestamp = date.getTime();
            }

            //expired urls handling
            AAResponse response = requestManager.getAlterApiResponse(mResponseTransform);
            AlterApiManager.handleAAResponse(response);
            //todo: check canceled flag before returning the result
            mCallback.onPropertySynchronized(statusCode, timestamp);
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.onSynchronizeException();
        }
    }

}
