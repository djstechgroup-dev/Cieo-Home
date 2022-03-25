package com.kinetise.data.application.popupmanager;

import com.kinetise.data.sourcemanager.LanguageManager;

public class PopupMessage {
    private String mTitle;
    private String mDescription;

    public PopupMessage(boolean isError, String description) {
        if (isError) {
            mTitle = LanguageManager.getInstance().getString(LanguageManager.POPUP_ERROR_HEADER);
        } else {
            mTitle = LanguageManager.getInstance().getString(LanguageManager.POPUP_INFO_HEADER);
        }
        mDescription = description;
    }

    public PopupMessage(String title, String description) {
        mTitle = title;
        mDescription = description;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
}
