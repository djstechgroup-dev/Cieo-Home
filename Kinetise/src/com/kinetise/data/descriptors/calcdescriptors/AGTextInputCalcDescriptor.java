package com.kinetise.data.descriptors.calcdescriptors;

import com.kinetise.data.descriptors.types.IntQuad;

public class AGTextInputCalcDescriptor extends AGViewCalcDesc{
    private BasicViewCalcDesc editTextCalcDesc;
    private IntQuad mTextPadding;

    public AGTextInputCalcDescriptor(){
        mTextPadding = new IntQuad();
        editTextCalcDesc = new BasicViewCalcDesc();
    }

    public AGViewCalcDesc createCalcDesc() {
        return new AGTextInputCalcDescriptor();
    }

    public BasicViewCalcDesc getEditTextCalcDesc() {
        return editTextCalcDesc;
    }

    public IntQuad getTextPadding() {
        return mTextPadding;
    }
}
