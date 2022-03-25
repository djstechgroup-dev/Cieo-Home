package com.kinetise.data.systemdisplay.views;

import android.text.method.PasswordTransformationMethod;

import com.kinetise.data.descriptors.datadescriptors.AGPasswordDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;

public class AGPasswordView extends AGTextInputView {
    public AGPasswordView(SystemDisplay display, AGPasswordDataDesc desc) {
        super(display, desc);
    }

    @Override
    protected void initEditText() {
        super.initEditText();
        mInputView.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mInputView.setInputType(mDescriptor.getKeyboard());
        setFontTypeface();
    }

}
