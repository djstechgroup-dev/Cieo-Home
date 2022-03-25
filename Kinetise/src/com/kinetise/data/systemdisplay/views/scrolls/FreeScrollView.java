package com.kinetise.data.systemdisplay.views.scrolls;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.views.IAGCollectionView;
import com.kinetise.data.systemdisplay.views.IAGView;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.support.scrolls.scrollManager.ScrollType;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class FreeScrollView extends AGScrollView {

    private InnerScroll mInnerScroll;

    public FreeScrollView(SystemDisplay display, AbstractAGContainerDataDesc desc) {
        super(display, desc, ScrollType.VERTICAL);
        mInnerScroll = new InnerScroll(display, desc, this instanceof IFeedScrollView);
        mInnerScroll.setSoundEffectsEnabled(false);
        addView(mInnerScroll);
        setDrawingCacheEnabled(false);
        setWillNotCacheDrawing(true);
    }

    @Override
    public String getTag() {
    	return "FreeScrollView";
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        mDescriptor = (AbstractAGContainerDataDesc) descriptor;
        mInnerScroll.setDescriptor(InnerScroll.prepareInnerScrollDesc(mDescriptor));
        mInnerScroll.setParentDescriptor(mDescriptor);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        getViewDrawer().refresh();

        int bottom = (int) Math.round(mCalcDesc.getContentHeight());
        int topSpacing = (int) Math.round(mCalcDesc.getPaddingTop()) + mCalcDesc.getBorder().getTopAsInt();
        int bottomSpacing = (int) Math.round(mCalcDesc.getPaddingBottom()) + mCalcDesc.getBorder().getBottomAsInt();
        mInnerScroll.layout(0, 0, r - l, bottom + topSpacing + bottomSpacing);

        mMaxChildBottomPosition = topSpacing + bottomSpacing + bottom;
        mMaxChildRightPosition = r - l;
        restoreScroll();
    }

    @Override
    public int getMaxChildBottomPosition() {
        return mMaxChildBottomPosition;
    }

    @Override
    protected void setDescriptorScrolls(int x, int y) {
        getDescriptor().setScrollY(y);
    }

    public void scrollViewTo(int x, int y) {
        super.scrollTo(0, y);
        getInnerSroll().scrollTo(x, 0);
    }
    public void scrollViewBy(int x, int y) {
        super.scrollBy(0, y);
        getInnerSroll().scrollBy(x, 0);
    }

    @Override
    public IAGView getAGViewParent() {
        ViewParent parent = getParent();
        if (!(parent instanceof IAGView)) {
            throw new InvalidParameterException("Parent of IAGView object have to implement IAGView interface");
        }

        return (IAGView) parent;
    }

    @Override
    public boolean accept(IViewVisitor visitor) {
    	if (getInnerSroll().accept(visitor)) return true;

        return visitor.visit(this);
    }


    protected InnerScroll getInnerSroll() {
        return mInnerScroll;
    }

    @Override
    public ArrayList<IAGView> getChildrenViews() {
        return mInnerScroll.getChildrenViews();
    }

    @Override
    public void addChildView(IAGView view) {
        mInnerScroll.addChildView(view);
    }

    @Override
    public void addChildView(IAGView view, int index) {
        mInnerScroll.addChildView(view, index);
    }

    @Override
    public void removeChildView(IAGView view) {
        mInnerScroll.removeChildView(view);
    }

    @Override
    public void removeChildView(int index) {
        View child = getChildAt(0);
        if (child instanceof IAGCollectionView) {
            ((IAGCollectionView) child).removeChildView(index);
        }
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
    }

    @Override
    public void removeAllViewsInLayout() {
        super.removeAllViewsInLayout();
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params instanceof FrameLayout.LayoutParams) {
            ((LayoutParams) params).setMargins(0, 0, 0, 0);
        }
        super.setLayoutParams(params);
    }

    /**
     * Restores last position of scrolls. However, sth view layout is triggered before all children are rendered. As restore scrolls uses scrollTo that
     * updates descriptors we need to reupdate them with old values in case body layouts one more time. In other words - we should set descriptors only
     * in case user scrolls view.
     */
    public void restoreScroll() {
        int oldX = getDescriptor().getScrollX();
        int oldY = getDescriptor().getScrollY();
        scrollViewTo(getDescriptor().getScrollX(), getDescriptor().getScrollY());
        setDescriptorScrolls(oldX, oldY);
    }

}
