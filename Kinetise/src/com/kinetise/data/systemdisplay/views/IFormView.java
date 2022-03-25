package com.kinetise.data.systemdisplay.views;

import android.view.View;

public interface IFormView extends View.OnClickListener {

    void showInvalidMessageToast();

    void showInvalidView();

    void hideInvalidView();

}
