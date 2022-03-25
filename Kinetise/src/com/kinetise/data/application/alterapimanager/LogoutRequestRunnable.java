package com.kinetise.data.application.alterapimanager;

import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.helpers.http.HttpRequestManager;

public class LogoutRequestRunnable extends AbstractRequestRunnable {

    public LogoutRequestRunnable(String url, String postBody, IRequestCallback callback) {
        super(AGHttpMethodType.POST, url, postBody, null, null, HttpRequestManager.RequestType.LOGOUT, callback);
    }

    @Override
    protected void reportErrorToCommand(PopupMessage... messages) {
        reportSuccesToCommand();
    }
}
