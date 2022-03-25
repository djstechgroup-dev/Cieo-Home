package com.kinetise.data.systemdisplay.views;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.kinetise.data.descriptors.datadescriptors.AGSearchInputDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;

public class AGSearchInputView extends AGTextInputView {

    public AGSearchInputView(SystemDisplay display, AGSearchInputDataDesc desc) {
        super(display, desc);
    }

    @Override
    protected void initEditText() {
        super.initEditText();
        mInputView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        super.onFocusChange(view, hasFocus);
        mInputView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        ((AGSearchInputDataDesc) mDescriptor).onAccept();
        super.onEditorAction(textView, i, keyEvent);
        return true;
    }
}
