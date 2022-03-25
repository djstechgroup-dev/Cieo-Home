package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.view.View;

import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGContainerCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.systemdisplay.IRebuildableView;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.views.scrolls.AGScrollView;
import com.kinetise.data.systemdisplay.views.scrolls.FreeScrollView;
import com.kinetise.helpers.drawing.BackgroundSetterCommandCallback;
import com.kinetise.helpers.drawing.ContainerDrawer;
import com.kinetise.helpers.drawing.ViewDrawer;

public class AGContainerView<T extends AbstractAGContainerDataDesc> extends AbstractAGCollectionView<T> implements IRebuildableView, BackgroundSetterCommandCallback {

    protected int mMaxChildRightPosition = 0;
    protected int mMaxChildBottomPosition = 0;
    protected AGContainerCalcDesc mCalcDesc;
    protected ContainerDrawer mDrawer = new ContainerDrawer(this);

    public AGContainerView(SystemDisplay display, T desc) {
        super(display, desc);

        setWillNotDraw(false);
        setAlwaysDrawnWithCacheEnabled(false);
        setOnClickListener(this);
        mCalcDesc = desc.getCalcDesc();
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return mDrawer;
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        String baseUrl = mDescriptor.getFeedBaseAdress();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        mBackgroundSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
        mDrawer.refresh();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        AbstractAGContainerDataDesc desc = getDescriptor();
        AGContainerCalcDesc calcDesc = desc.getCalcDesc();

        //mMaxChildBottomPosition = 0;
        //mMaxChildRightPosition = 0;

        final int count = getChildCount();
        double maxChildRightPos = 0;
        double maxChildBottomPos = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null || !(child instanceof IAGView))
                continue;
            // zmienione poniewaz czasem hierarchia deskryptorow nie odpowiadala hierarchi widokow
            final AGViewCalcDesc childCalcDesc = (AGViewCalcDesc) ((IAGView) child).getDescriptor().getCalcDesc();

            // sizes
            int width = (int) (Math.round(childCalcDesc.getWidth() + Math.round(childCalcDesc.getBorder().getHorizontalBorderWidth())));
            int height = (int) (Math.round(childCalcDesc.getHeight() + Math.round(childCalcDesc.getBorder().getVerticalBorderHeight())));

            int left = (int) Math.round(calcDesc.getPaddingLeft() + calcDesc.getBorder().getLeftAsInt() + Math.round(childCalcDesc.getPositionX()) + childCalcDesc.getMarginLeft());
            int top = (int) Math.round(calcDesc.getPaddingTop() + calcDesc.getBorder().getTopAsInt() + Math.round(childCalcDesc.getPositionY()) + childCalcDesc.getMarginTop());
            int right = left + width;
            int bottom = top + height;

            child.layout(left, top, right, bottom);

            maxChildBottomPos = (int) Math.max(Math.round(bottom + childCalcDesc.getMarginBottom()), maxChildBottomPos);
            maxChildRightPos = (int) Math.max(Math.round(right + childCalcDesc.getMarginRight()), maxChildRightPos);
        }

        int lastMaxBottom = mMaxChildBottomPosition;
        int lastMaxRight = mMaxChildRightPosition;

        mMaxChildBottomPosition = (int) (maxChildBottomPos + calcDesc.getBorder().getBottomAsInt() + Math.round(calcDesc.getPaddingBottom()));
        mMaxChildRightPosition = (int) (maxChildRightPos + calcDesc.getBorder().getRightAsInt() + Math.round(calcDesc.getPaddingRight()));

        if (this instanceof AGScrollView && (mMaxChildRightPosition < lastMaxRight || mMaxChildBottomPosition < lastMaxBottom)) {
            scrollTo(0, 0);
        }
    }

    public int getMaxChildRightPosition() {
        return mMaxChildRightPosition;
    }

    public int getMaxChildBottomPosition() {
        return mMaxChildBottomPosition;
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        super.setDescriptor(descriptor);
        mDescriptor = (T) descriptor;
        mCalcDesc = mDescriptor.getCalcDesc();
    }

    @Override
    public void rebuildView() {
        AbstractAGCollectionView currentGroup = this instanceof FreeScrollView ? (AbstractAGCollectionView) getChildAt(0) : this;
        currentGroup.removeAllChildrenViews();
        for (int controlsId = 0; controlsId < getDescriptor().getAllControls().size(); controlsId++) {
            AbstractAGElementDataDesc desc = getDescriptor().getAllControls().get(controlsId);
            if (desc instanceof AbstractAGViewDataDesc && ((AbstractAGViewDataDesc) desc).isRemoved()) {
                continue;
            }
            IAGView view = (IAGView) ViewFactoryManager.createViewHierarchy(desc, getSystemDisplay());
            currentGroup.addChildView(view, controlsId);
        }
    }
}
