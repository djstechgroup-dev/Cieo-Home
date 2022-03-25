package com.kinetise.data.systemdisplay.views;

import android.view.Gravity;
import android.view.View;

import com.kinetise.data.descriptors.datadescriptors.AGTextAreaDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;

public class AGTextAreaView extends AGTextInputView {

    private AGTextAreaDataDesc mDescriptor;

    public AGTextAreaView(SystemDisplay display, AGTextAreaDataDesc desc) {
        super(display, desc);
    }

    @Override
    protected void initEditText() {
        initInput();
        mInputView.setSingleLine(false);
        mInputView.setMaxLines(Integer.MAX_VALUE);
        mDescriptor = (AGTextAreaDataDesc) getDescriptor();

        //lines max
        int contentSpaceWidth = (int) Math.round(mDescriptor.getCalcDesc().getContentSpaceWidth());
        mInputView.setMaxWidth(contentSpaceWidth);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        super.onFocusChange(view, hasFocus);
        if (hasFocus) {
            mInputView.setSelection(mInputView.getText().length());
        }
    }

    @Override
    protected int getTextGravity() {
        int parentgravity = super.getTextGravity();
        parentgravity |= Gravity.TOP;
        return parentgravity;
    }
}
