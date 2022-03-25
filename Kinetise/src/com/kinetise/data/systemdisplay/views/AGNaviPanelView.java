package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;

import com.kinetise.data.descriptors.AGNaviPanelDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.helpers.drawing.ViewDrawer;

import java.util.List;

public class AGNaviPanelView extends AbstractAGCollectionView implements IAGSectionView {

    private final AGNaviPanelDataDesc mDataDesc;

    public AGNaviPanelView(SystemDisplay display, AGNaviPanelDataDesc desc) {
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

            double leftDouble = Math.round(childCalcDesc.getPositionX()) + childCalcDesc.getMarginLeft();
            double topDouble = Math.round(childCalcDesc.getPositionY()) + childCalcDesc.getMarginTop();
            // sizes
            int width = (int) (Math.round(childCalcDesc.getWidth() + childCalcDesc.getBorder().getHorizontalBorderWidth() + leftDouble) - Math.round(leftDouble));
            int height = (int) (Math.round(childCalcDesc.getHeight() + childCalcDesc.getBorder().getVerticalBorderHeight() + topDouble) - Math.round(topDouble));

            int left = (int) Math.round(leftDouble);
            int top = (int) Math.round(topDouble);

            // Commented-out as String.format is heavy operation. Uncomment when needed for debugging.
            /*Logger.v(this,"onLayout", String.format("%s: child.layout: left=%d, top=%d, width=%d, height=%d",
                    child.getClass().getName(), left, top, width, height));*/

            child.layout(left, top, left + width, top + height);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // zrobione aby przeciaganie palcem po navipanelu nie skrolowalo widokow za nim
        super.onTouchEvent(event);
        return true;
    }

    @Override
    public void onClick(View view) {
        // nothing to do
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return null;
    }

    @Override
    public void setBackgroundBitmap(Bitmap bitmap) {

    }
}
