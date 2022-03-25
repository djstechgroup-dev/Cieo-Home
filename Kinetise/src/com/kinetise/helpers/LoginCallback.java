package com.kinetise.helpers;

public interface LoginCallback {
    void onLoginSuccess(String accessToken);
    void onFailed();
}
