package com.kinetise.data.systemdisplay.views.scrolls;

import android.view.View;

import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGContainerCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.views.IAGView;
import com.kinetise.debug.TimeLog;
import com.kinetise.helpers.drawing.ViewDrawer;
import com.kinetise.support.logger.Logger;
import com.kinetise.support.scrolls.scrollManager.ScrollType;

import java.util.List;

public class InnerScroll extends AGDataFeedScrollView {

    private final boolean mParentFeed;
    private AbstractAGContainerDataDesc mParentDescriptor;

    public InnerScroll(SystemDisplay display, AbstractAGContainerDataDesc desc, boolean isParentFeed) {
        super(display, prepareInnerScrollDesc(desc), ScrollType.HORIZONTAL);
        mParentDescriptor = desc;
        Logger.e(this, "InnerScrollCreated");
        mParentFeed = isParentFeed;
    }

    /**
     * Prepares inner scroll view that will scroll horizontaly
     *
     * @return Descriptor for inner scrollView
     */
    public static AbstractAGContainerDataDesc prepareInnerScrollDesc(AbstractAGContainerDataDesc desc) {
        TimeLog.startLoggingTime("containerCopy");
        AbstractAGContainerDataDesc copy = desc.copyWithoutCopyingChildren();
        copy.setCalcDesc(desc.getCalcDesc());
        copy.setBackgroundColor(AGXmlParserHelper.COLOR_TRANSPARENT);

        TimeLog.stopLoggingTime("containerCopy");
        return copy;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        AbstractAGContainerDataDesc desc = getDescriptor();
        AGContainerCalcDesc calcDesc = desc.getCalcDesc();

        double maxChildRightPos = 0;
        double maxChildBottomPos = 0;

        List<AbstractAGElementDataDesc> childDataDesc = desc.getAllControls();

        for (int i = 0; i < childDataDesc.size(); i++) {
            final AGViewCalcDesc childCalcDesc = (AGViewCalcDesc) childDataDesc.get(i).getCalcDesc();

            // sizes
            int width = (int) (Math.round(childCalcDesc.getWidth() + Math.round(childCalcDesc.getBorder().getHorizontalBorderWidth())));
            int height = (int) (Math.round(childCalcDesc.getHeight() + Math.round(childCalcDesc.getBorder().getVerticalBorderHeight())));

            int left = (int) Math.round(calcDesc.getPaddingLeft() + calcDesc.getBorder().getLeftAsInt() + Math.round(childCalcDesc.getPositionX()) + childCalcDesc.getMarginLeft());
            int top = (int) Math.round(calcDesc.getPaddingTop() + calcDesc.getBorder().getTopAsInt() + Math.round(childCalcDesc.getPositionY()) + childCalcDesc.getMarginTop());
            int right = left + width;
            int bottom = top + height;

            maxChildBottomPos = (int) Math.max(Math.round(bottom + childCalcDesc.getMarginBottom()), maxChildBottomPos);
            maxChildRightPos = (int) Math.max(Math.round(right + childCalcDesc.getMarginRight()), maxChildRightPos);

            if (!mParentFeed) {
                final View child = getChildAt(i);
                if (child != null) {
                    child.layout(left, top, right, bottom);
                }
            }
        }

        mMaxChildBottomPosition = (int) (maxChildBottomPos + calcDesc.getBorder().getBottomAsInt() + Math.round(calcDesc.getPaddingBottom()));
        mMaxChildRightPosition = (int) (maxChildRightPos + calcDesc.getBorder().getRightAsInt() + Math.round(calcDesc.getPaddingRight()));

        ViewDrawer drawer = getViewDrawer();
        if (drawer != null) {
            drawer.refresh();
        }
        resetScrollsIfNeeded();
    }

    private void resetScrollsIfNeeded() {
        int x = getScrollX();
        if (mMaxChildRightPosition <= x + mCalcDesc.getWidth() + mCalcDesc.getPaddingRight() + mCalcDesc.getPaddingLeft()) {
            x = mMaxChildRightPosition - (int) (mCalcDesc.getWidth() + mCalcDesc.getPaddingRight() + mCalcDesc.getPaddingLeft() + mCalcDesc.getBorder().getLeft());
            if (x < 0)
                x = 0;
        }
        scrollTo(x, 0);
    }

    @Override
    public int getContentHeight() {
        return calcChildMaxBottom();
    }

    @Override
    public int getViewPortHeight() {
        return super.getViewPortHeight();
    }

    /**
     * Calculates maximum position of bottom child,
     *
     * @return {@link InnerScroll#getMaxChildBottom()}
     */
    public int getMaxChildBottom() {
        return calcChildMaxBottom();
    }

    /**
     * Calculates maximum position of bottom child,
     *
     * @return bottom position of bottom child
     */
    private int calcChildMaxBottom() {
        Logger.v(this, "calcChildMaxBottom");
        int maxBottom = 0;

        AbstractAGContainerDataDesc desc = getDescriptor();
        AGContainerCalcDesc calcDesc = desc.getCalcDesc();

        final int count = getChildCount();
        List<AbstractAGElementDataDesc> childDataDesc = desc.getPresentControls();

        for (int i = 0; i < Math.min(count, childDataDesc.size()); i++) {
            if (i > childDataDesc.size()) {
                continue;
            }
            final AGViewCalcDesc childCalcDesc = ((AbstractAGViewDataDesc) childDataDesc.get(i)).getCalcDesc();

            // sizes
            int height = (int) Math.round(childCalcDesc.getHeight() + childCalcDesc.getBorder().getVerticalBorderHeight());

            int top = (int) Math.round(calcDesc.getPaddingTop() + calcDesc.getBorder().getTop() +
                    childCalcDesc.getPositionY() + childCalcDesc.getMarginTop());

            int bottom = top + height;

            if (bottom > maxBottom)
                maxBottom = bottom;
        }

        return maxBottom;
    }

    @Override
    public String getTag() {
        return "InnerScroll";
    }

    @Override
    public IAGView getAGViewParent() {
        return super.getAGViewParent().getAGViewParent();
    }

    @Override
    protected void setDescriptorScrolls(int x, int y) {
        getParentDescriptor().setScrollX(x);
    }

    public void setParentDescriptor(AbstractAGContainerDataDesc parentDescriptor) {
        mParentDescriptor = parentDescriptor;
    }

    public AbstractAGContainerDataDesc getParentDescriptor() {
        return mParentDescriptor;
    }
}
