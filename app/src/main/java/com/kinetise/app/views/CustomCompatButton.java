package com.kinetise.app.views;

import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.kinetise.data.descriptors.datadescriptors.AGCustomControlDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.views.AGCustomControlView;


public class CustomCompatButton extends AGCustomControlView {
    private static final String TEXT_ATTRIBUTE = "text";

    private final View mButton;

    public CustomCompatButton(SystemDisplay display, AGCustomControlDataDesc desc) {
        super(display, desc);
        mButton = createCompatButton();
        addView(mButton);
    }

    private View createCompatButton() {
        AppCompatButton button = new AppCompatButton(mDisplay.getActivity());
        button.setText(mDescriptor.getAttribute(TEXT_ATTRIBUTE));
        return button;
    }
}
