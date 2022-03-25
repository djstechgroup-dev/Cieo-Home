package com.kinetise.data.systemdisplay.views;

import com.kinetise.data.descriptors.calcdescriptors.AGElementCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGErrorDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;

public class AGErrorView extends AGImageView {

    public AGErrorView(SystemDisplay display, AGErrorDataDesc desc) {
        super(display, desc);
        desc.setBackgroundColor(-1);
        desc.setBorderColor(-1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        AGElementCalcDesc mCalc = mDescriptor.getCalcDesc();
        if (oldw > 0 && oldh > 0) {
            mCalc.setHeight(h);
            mCalc.setWidth(w);

            getDescriptor().getCalcDesc().setWidth(w);
            getDescriptor().getCalcDesc().setHeight(h);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

}
