package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.FrameLayout;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.drawing.ViewDrawer;

/**
 * User: Mateusz
 * Date: 17.05.13
 * Time: 16:04
 */
public class AGPopupView extends AGControl implements IAGView {

    private SystemDisplay mDisplay;
    private IAGView mChildView;

    public AGPopupView(SystemDisplay display, View childView) {
        super(display, (AbstractAGElementDataDesc) null);

        mChildView = (IAGView) childView;

        addView(childView);
        setOnClickListener(this);

        setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);

        final AbstractAGViewDataDesc desc = (AbstractAGViewDataDesc) getDescriptor();
        final AGViewCalcDesc childCalcDesc = desc.getCalcDesc();

        double leftDouble = childCalcDesc.getPositionX() + childCalcDesc.getMarginLeft();
        double topDouble = childCalcDesc.getPositionY() + childCalcDesc.getMarginTop();
        // sizes
        int width = (int) (Math.round(childCalcDesc.getWidth() + childCalcDesc.getBorder().getHorizontalBorderWidth() + leftDouble) - Math.round(leftDouble));
        int height = (int) (Math.round(childCalcDesc.getHeight() + childCalcDesc.getBorder().getVerticalBorderHeight() + topDouble) - Math.round(topDouble));

        int left = (int) Math.round(leftDouble);
        int top = (int) Math.round(topDouble);
        int right = left + width;
        int bottom = top + height;

        ((View) mChildView).layout(left, top, right, bottom);
    }

    @Override
    public AbstractAGElementDataDesc getDescriptor() {
        return mChildView != null ? mChildView.getDescriptor() : null;
    }

    @Override
    public IAGView getAGViewParent() {
        return null;
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return null;
    }

    @Override
    public void loadAssets() {
    }

    @Override
    public boolean accept(IViewVisitor visitor) {

        return visitor.visit(this) || mChildView.accept(visitor);
    }

    @Override
    public void onClick(View view) {
        // nothing to do
    }

    @Override
     public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        mChildView.setDescriptor(descriptor);
    }

    @Override
    public void setBackgroundBitmap(Bitmap bitmap) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            child.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
