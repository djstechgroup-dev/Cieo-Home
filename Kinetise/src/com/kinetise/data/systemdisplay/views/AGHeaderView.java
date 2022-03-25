package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.view.View;

import com.kinetise.data.descriptors.AGHeaderDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.helpers.drawing.ViewDrawer;

import java.util.List;

public class AGHeaderView extends AbstractAGCollectionView<AGHeaderDataDesc> implements IAGSectionView {

    private AGHeaderDataDesc mDataDesc;

    public AGHeaderView(SystemDisplay display, AGHeaderDataDesc desc) {
        super(display, desc);
        mDataDesc = desc;
    }

    @Override
    protected void onLayout(boolean bool, int l, int t, int r, int b) {
        super.onLayout(bool, l, t, r, b);
        final int count = getChildCount();
        List<AbstractAGElementDataDesc> childDataDesc = mDataDesc.getAllControls();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final AGViewCalcDesc childCalcDesc = ((AbstractAGViewDataDesc) childDataDesc.get(i)).getCalcDesc();
            double leftDouble, topDouble;
            leftDouble = Math.round(childCalcDesc.getPositionX()) + childCalcDesc.getMarginLeft();
            topDouble = Math.round(childCalcDesc.getPositionY()) + childCalcDesc.getMarginTop();

            // sizes
            int width = (int) (Math.round(childCalcDesc.getWidth() + childCalcDesc.getBorder().getHorizontalBorderWidth() + leftDouble) - Math.round(leftDouble));
            int height = (int) (Math.round(childCalcDesc.getHeight() + childCalcDesc.getBorder().getVerticalBorderHeight() + topDouble) - Math.round(topDouble));

            int left = (int) Math.round(leftDouble);
            int top = (int) Math.round(topDouble);

            child.layout(left, top, left + width, top + height);
        }
    }

    @Override
    public void onClick(View view) {
        // nothing to do
    }

    @Override
    public void addChildView(IAGView view) {
        super.addChildView(view);
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return null;
    }

    @Override
    public void setBackgroundBitmap(Bitmap bitmap) {

    }
}
