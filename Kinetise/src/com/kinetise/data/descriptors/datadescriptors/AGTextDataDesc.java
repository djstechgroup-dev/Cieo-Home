package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.components.ITextDescriptor;
import com.kinetise.data.descriptors.datadescriptors.components.TextDescriptor;

public class AGTextDataDesc extends AbstractAGViewDataDesc implements ITextDescriptor {

    private TextDescriptor mTextDescriptor;

    public AGTextDataDesc(String id) {
        super(id);
        mTextDescriptor = new TextDescriptor(this);
    }

    public TextDescriptor getTextDescriptor() {
        return mTextDescriptor;
    }

    public void setTextDescriptor(TextDescriptor textDescriptor) {
        mTextDescriptor = textDescriptor;
    }

    @Override
    public AbstractAGElementDataDesc createInstance() {
        return new AGTextDataDesc(getId());
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mTextDescriptor.resolveVariable();
    }

    @Override
    public AGViewCalcDesc getCalcDesc() {
        if (mCalcDescriptor == null) {
            mCalcDescriptor = new AGViewCalcDesc();
        }

        return (AGViewCalcDesc) mCalcDescriptor;
    }

    @Override
    public AGTextDataDesc copy() {
        AGTextDataDesc copied = (AGTextDataDesc) super.copy();
        copied.mTextDescriptor = mTextDescriptor.copy(copied);
        return copied;
    }
}
