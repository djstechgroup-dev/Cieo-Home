package com.kinetise.data.application.alterapimanager;

import android.text.TextUtils;

import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.helpers.http.HttpRequestManager;

import java.util.Map;

public class LoginRequestRunnable extends AbstractRequestRunnable {

    public LoginRequestRunnable(AGHttpMethodType method, String url, String postBody, Map<String, String> headers, String responseTransform, IRequestCallback callback) {
        super(method, url, postBody, headers, responseTransform, HttpRequestManager.RequestType.LOGIN, callback);
    }

    @Override
    protected boolean hasRequiredNodes(AAResponse alterApiResponse) {
        if (alterApiResponse == null || TextUtils.isEmpty(alterApiResponse.sessionId))
            return false;
        else
            return true;
    }
}
