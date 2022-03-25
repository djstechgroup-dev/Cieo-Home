package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.view.View;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGCustomControlDataDesc;
import com.kinetise.data.systemdisplay.LayoutHelper;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.helpers.drawing.ViewDrawer;

public class AGCustomControlView extends AGControl<AGCustomControlDataDesc> {

    public AGCustomControlView(SystemDisplay display, AGCustomControlDataDesc desc) {
        super(display, desc);
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        super.setDescriptor(descriptor);
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return mDrawer;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for(int i=0;i<getChildCount();i++){
            LayoutHelper.measureFill(getChildAt(i), mDescriptor.getCalcDesc());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        for(int i=0;i<getChildCount();i++){
            LayoutHelper.layoutFill(getChildAt(i), mDescriptor.getCalcDesc());
        }
    }
}
