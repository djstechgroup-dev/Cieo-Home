package com.kinetise.data.application.alterapimanager;

import com.kinetise.data.application.popupmanager.PopupMessage;

public interface IRequestCallback {
    void onError(PopupMessage... messages);
    void onSuccess(PopupMessage... messages);
}
