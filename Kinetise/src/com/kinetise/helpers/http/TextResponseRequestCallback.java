package com.kinetise.helpers.http;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.helpers.http.HttpRequestManager;
import com.kinetise.helpers.http.NetworkUtils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public abstract class TextResponseRequestCallback implements NetworkUtils.RequestCallback {

    @Override
    public void onResponse(HttpRequestManager requestManager) {
        InputStream response = requestManager.getContent();
        try {
            String responseBody = IOUtils.toString(response);
            onSuccess(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(response);
        }
    }

    public abstract void onSuccess(String response);

    @Override
    public void onLogout() {
        AGApplicationState.getInstance().logoutUser();
    }
}
