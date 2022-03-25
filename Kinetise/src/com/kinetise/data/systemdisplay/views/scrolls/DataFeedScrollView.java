package com.kinetise.data.systemdisplay.views.scrolls;

import android.view.View;

import com.kinetise.data.application.feedmanager.DownloadFeedCommand;
import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.calcdescriptors.AGContainerCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGLoadingDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGDataFeedDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.views.scrolls.dataFeedHelpers.AGDataFeedAdapter;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.support.logger.Logger;
import com.kinetise.support.scrolls.scrollManager.ScrollType;

import java.util.List;

public class DataFeedScrollView extends AGDataFeedScrollView implements IFeedScrollView {


    AGViewCalcDesc mCalcDesc;
    SystemDisplay mDisplay;
    private AGDataFeedAdapter mAdapter;
    private boolean mIsScrollStartOrEnd;

    public DataFeedScrollView(SystemDisplay display, AbstractAGContainerDataDesc desc, ScrollType scrollType) {
        super(display, desc, scrollType);
        mCalcDesc = desc.getCalcDesc();
        mDisplay = display;
        setAdapter(new AGDataFeedAdapter(this, mDisplay, desc.getPresentControls()));
    }

    /**
     * Ustawia adapter dla datafeeda, automatycznie wywoluje {@link #refreshList()};
     *
     * @param adapter adapter do ustawienia
     */
    public void setAdapter(AGDataFeedAdapter adapter) {
        mAdapter = adapter;
        refreshList();
        mAdapter.initFeed();
    }

    /**
     * Powoduje odświeżenie listy dla potrzeb adaptera, usuwa wszystkie widoki z listy,
     */
    private void refreshList() {
        Logger.d("Adapter", "refreshList");
        removeAllViews();
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        String baseUrl = mDescriptor.getFeedBaseAdress();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        mBackgroundSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
        // Nic nie robimy i nie przkazujemy dalej - adapter załaduje assety swoim widokom którymi zarządza (wywoła im loadAssets())
    }

    @Override
    public void attachAndLayoutChild(View view, boolean isRecycled) {
        attachChild(view, isRecycled);
        layoutChild(view);
    }

    @Override
    public boolean accept(IViewVisitor visitor) {
        mAdapter.accept(visitor);
        return super.accept(visitor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mAdapter.recalculateChildPositions();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Logger.d("Adapter", "OnLayout");
        getViewDrawer().refresh();
        //Jaako ze standardowa metoda onLayout liczy tylko srollPosition dla  dzieci dodanych aktualnei do widoku, to nizej jest rozszerzenie
        //ktore wylicza maksymalną pozycję dziecka bottom i right, w celu zapewnienia scrollowania
        //calculateChildPositions();
        AbstractAGContainerDataDesc desc = getDescriptor();
        AGContainerCalcDesc calcDesc = desc.getCalcDesc();

        mMaxChildBottomPosition = 0;
        mMaxChildRightPosition = 0;

        //Foreach jest zrobiony by na podstawie deskryptorow policzyc obszar scrollowalny feeda, kolejna petla jest uzyta po to by na dzieciach wywolac layout,
        //jako ze ten widok korzysta z adaptera, liczba deskryptorow (potencjalnych dzieci) zwykle jest wieksza niz liczba aktualnie dodanych dzieci w hierarchii

        List<AbstractAGElementDataDesc> childDataDescriptors = desc.getPresentControls();
        for (AbstractAGElementDataDesc childDataDesc : childDataDescriptors) {
            AGViewCalcDesc childCalcDesc = (AGViewCalcDesc) childDataDesc.getCalcDesc();
            int top = (int) Math.round(calcDesc.getPaddingTop() + calcDesc.getBorder().getTopAsInt() + Math.round(childCalcDesc.getPositionY()) + childCalcDesc.getMarginTop());
            int left = (int) Math.round(calcDesc.getPaddingLeft() + calcDesc.getBorder().getLeftAsInt() + Math.round(childCalcDesc.getPositionX()) + childCalcDesc.getMarginLeft());
            int height = (int) (Math.round(childCalcDesc.getHeight() + Math.round(childCalcDesc.getBorder().getVerticalBorderHeight())));
            int width = (int) (Math.round(childCalcDesc.getWidth() + Math.round(childCalcDesc.getBorder().getHorizontalBorderWidth())));
            int right = left + width;
            int bottom = top + height;
            if (bottom > mMaxChildBottomPosition)
                mMaxChildBottomPosition = (int) Math.round(bottom + childCalcDesc.getMarginBottom());
            if (right > mMaxChildRightPosition)
                mMaxChildRightPosition = (int) Math.round(right + childCalcDesc.getMarginRight());
        }
        mMaxChildRightPosition += (int) calcDesc.getBorder().getRight() + (int) calcDesc.getPaddingRight();
        mMaxChildBottomPosition += (int) calcDesc.getBorder().getBottom() + (int) calcDesc.getPaddingBottom();

        mAdapter.sortOutChildrenByScrollDirection();
        for (View view : mAdapter.getItems()) {
            if (view != null)
                layoutChild(view);
        }
        if (!isLoadingDisplayed())
            resetScrollsIfNeeded();

        mIsScrollStartOrEnd = isScrollStartOrEnd();
    }

    private boolean isLoadingDisplayed() {
        return getDescriptor().getPresentControls().size() == 1 && getDescriptor().getPresentControls().get(0) instanceof AGLoadingDataDesc;
    }

    private void resetScrollsIfNeeded() {
        int y = getScrollY();
        int x = getScrollX();
        if (mMaxChildBottomPosition <= y + mCalcDesc.getHeight()) {
            y = mMaxChildBottomPosition - (int) (mCalcDesc.getHeight());
            if (y < 0)
                y = 0;
        }
        if (mMaxChildRightPosition <= x + mCalcDesc.getWidth()) {
            x = mMaxChildRightPosition - (int) (mCalcDesc.getWidth() + mCalcDesc.getPaddingRight() + mCalcDesc.getPaddingLeft() + mCalcDesc.getBorder().getRight());
            if (x < 0)
                x = 0;
        }
        scrollTo(x, y);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        mIsScrollStartOrEnd = isScrollStartOrEnd();

        mAdapter.sortOutChildrenByScrollDirection();
    }

    private boolean isScrollStartOrEnd() {
        if (mDescriptor.isScrollVertical()) {
            if (mDescriptor.isInverted())
                return getScrollY() + getHeight() >= getContentHeight() - (getLastChildHeight() * 0.5);
            else
                return getScrollY() <= getFirstChildHeight() * 0.5;
        } else {
            if (mDescriptor.isInverted())
                return getScrollX() + getWidth() >= getContentWidth() - (getLastChildWidth() * 0.5);
            else
                return getScrollX() <= getFirstChildWidth() * 0.5;
        }
    }

    private double getFirstChildHeight() {
        AbstractAGElementDataDesc firstChild = getFirstChild();
        if (firstChild == null)
            return 0;
        return firstChild.getCalcDesc().getHeight();
    }

    private double getFirstChildWidth() {
        AbstractAGElementDataDesc firstChild = getFirstChild();
        if (firstChild == null)
            return 0;
        return firstChild.getCalcDesc().getWidth();
    }

    private AbstractAGElementDataDesc getFirstChild() {
        if (mDescriptor.getPresentControls().size() > 0)
            return mDescriptor.getPresentControls().get(0);
        return null;
    }

    private double getLastChildHeight() {
        AbstractAGElementDataDesc lastChild = getLastChild();
        if (lastChild == null)
            return 0;
        return lastChild.getCalcDesc().getHeight();
    }

    private double getLastChildWidth() {
        AbstractAGElementDataDesc lastChild = getLastChild();
        if (lastChild == null)
            return 0;
        return lastChild.getCalcDesc().getWidth();
    }

    private AbstractAGElementDataDesc getLastChild() {
        if (mDescriptor.getPresentControls().size() > 0)
            return mDescriptor.getPresentControls().get(mDescriptor.getPresentControls().size() - 1);
        return null;
    }

    @Override
    public void rebuildView() {
        Logger.d("Adapter", "RepaintView");
        AbstractAGDataFeedDataDesc feedClient = (AbstractAGDataFeedDataDesc) getDescriptor();
        List<AbstractAGElementDataDesc> views = feedClient.getPresentControls();
        if (feedClient.isLoadingMore() && mAdapter != null) {
            loadMoreViews();
        } else {
            setAdapter(new AGDataFeedAdapter(this, mDisplay, views));
            if (views.size()!=1 || !(views.get(0) instanceof AGLoadingDataDesc))
                restoreScroll();
        }

        if (mIsScrollStartOrEnd) {
            int contentChange;
            if (mDescriptor.isScrollVertical()) {
                double oldContentHeight = ((AbstractAGDataFeedDataDesc) getDescriptor()).getContentHeight();
                contentChange = (int) Math.ceil(mCalcDesc.getContentHeight() - oldContentHeight);
                if (oldContentHeight > 0 && contentChange > 0) {
                    if (mDescriptor.isInverted()) {

                        int delta = (int) Math.ceil(
                                mCalcDesc.getContentHeight()
                                        + mCalcDesc.getPaddingTop()
                                        + mCalcDesc.getPaddingBottom()
                                        - mCalcDesc.getHeight()
                                        - getScrollY());
                        if (delta > 0)
                            smoothScrollBy(0, delta);
                    } else {
                        scrollBy(0, contentChange + getScrollY());
                        smoothScrollBy(0, -getScrollY());
                    }
                }
            } else {
                double oldContentWidth = ((AbstractAGDataFeedDataDesc) getDescriptor()).getContentWidth();
                contentChange = (int) Math.ceil(mCalcDesc.getContentWidth() - oldContentWidth);
                if (oldContentWidth > 0 && contentChange > 0) {

                    if (mDescriptor.isInverted()) {
                        int delta = (int) Math.ceil(
                                mCalcDesc.getContentWidth()
                                        + mCalcDesc.getPaddingLeft()
                                        + mCalcDesc.getPaddingRight()
                                        - mCalcDesc.getWidth()
                                        - getScrollX());
                        if (delta > 0)
                            smoothScrollBy(delta, 0);
                    } else {
                        scrollBy(contentChange + getScrollX(), 0);
                        smoothScrollBy(-getScrollX(), 0);
                    }
                }
            }
        }

        if (!isLoadingDisplayed()) {
            ((AbstractAGDataFeedDataDesc) getDescriptor()).setContentHeight(mCalcDesc.getContentHeight());
            ((AbstractAGDataFeedDataDesc) getDescriptor()).setContentWidth(mCalcDesc.getContentWidth());
        }
    }

    private void loadMoreViews() {
        //remove loading view
        removeView(mAdapter.pollLastItem());
        mAdapter.initFeed();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDescriptor instanceof IFeedClient) {
            IFeedClient feedClient = (IFeedClient) mDescriptor;
            DownloadFeedCommand command = feedClient.getDownloadCommad();
            if (command != null)
                command.cancel();
        }
    }

    @Override
    public AGDataFeedScrollView getScrollView() {
        return this;
    }


    @Override
    public int getFeedScrollX() {
        return getScrollX();
    }

    @Override
    public int getFeedScrollY() {
        return getScrollY();
    }

    @Override
    public AbstractAGContainerDataDesc getFeedDescriptor() {
        return getDescriptor();
    }

    @Override
    public ScrollType getFeedScrollType() {
        return getScrollType();
    }


}

