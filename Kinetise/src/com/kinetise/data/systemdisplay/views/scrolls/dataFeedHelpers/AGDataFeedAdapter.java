package com.kinetise.data.systemdisplay.views.scrolls.dataFeedHelpers;

import android.view.View;

import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.views.IAGView;
import com.kinetise.data.systemdisplay.views.ViewFactoryManager;
import com.kinetise.data.systemdisplay.views.scrolls.IFeedScrollView;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.support.logger.Logger;
import com.kinetise.support.scrolls.scrollManager.ScrollType;

import java.util.ArrayList;
import java.util.List;


public class AGDataFeedAdapter {

    private List<AbstractAGElementDataDesc> mViewDescs;
    private List<ChildPosition> mChildPositions;
    private List<View> mItems;
    private RecycledViewsHolder mRecycledViews;
    private SystemDisplay mDisplay;
    private IFeedScrollView mDataFeedView;
    private int mFirstVisibleIndex;
    private int mLastVisibleIndex;

    public AGDataFeedAdapter(IFeedScrollView view, SystemDisplay display, List<AbstractAGElementDataDesc> items) {
        mDataFeedView = view;
        mDisplay = display;
        mViewDescs = items;
        init();
    }

    public void init() {
        mRecycledViews = new RecycledViewsHolder();
        mChildPositions = new ArrayList<>(getCount());
        mItems = new ArrayList<>(getCount());
        mFirstVisibleIndex = 0;
        mLastVisibleIndex = 0;
    }

    public int getCount() {
        return mViewDescs != null ? mViewDescs.size() : 0;
    }

    public View pollLastItem() {
        View v = mItems.get(mItems.size() - 1);
        mItems.remove(mItems.size() - 1);
        mChildPositions.remove(mChildPositions.size() - 1);
        if (mFirstVisibleIndex == mLastVisibleIndex)
            mFirstVisibleIndex--;
        mLastVisibleIndex--;
        return v;
    }

    public AbstractAGElementDataDesc getDescriptor(int index) {
        return (mViewDescs != null && mViewDescs.size() > index) ? mViewDescs.get(index) : null;
    }

    public View getView(int position, View convertView) {
        //TODO: to make view recycling work properly a new method should be implemented for recalculating text view size with a size min when the text is changed
        AbstractAGElementDataDesc descriptor = getDescriptor(position);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(descriptor.getCalcDesc().getViewWidth(), View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(descriptor.getCalcDesc().getViewHeight(), View.MeasureSpec.EXACTLY);
        if (convertView != null) {
            ((IAGView) convertView).setDescriptor(descriptor);
            ((IAGView) convertView).loadAssets();
            convertView.measure(widthMeasureSpec, heightMeasureSpec);
            return convertView;
        } else {
            Logger.v(this, "getView", "Creating new view");
            View newView = ViewFactoryManager.createViewHierarchy(mViewDescs.get(position), mDisplay);
            ((IAGView) newView).loadAssets();
            newView.measure(widthMeasureSpec, heightMeasureSpec);
            return newView;
        }
    }

    public List<View> getItems() {
        return mItems;
    }

    public void initFeed() {
        recalculateChildPositions();
        sortOutChildrenByScrollDirection();
    }

    public void recalculateChildPositions() {
        mChildPositions.clear();
        mFirstVisibleIndex = 0;
        calculateChildPositions(0);
    }

    private void calculateChildPositions(int startIndex) {
        Logger.d("Adapter", "calculateChildPositions");
        for (int x = startIndex; x < getCount(); x++) {
            AGViewCalcDesc calcDesc = (AGViewCalcDesc) getDescriptor(x).getCalcDesc();

            if (mDataFeedView.getFeedScrollType() == ScrollType.HORIZONTAL) {
                int left = (int) Math.round(calcDesc.getPositionX()
                        + calcDesc.getMarginLeft()
                        + calcDesc.getPaddingLeft()); //we must move position regarding parent padding
                int right = (int) Math.round(left
                        + calcDesc.getWidth()
                        + calcDesc.getMarginLeft() //we must move regarding own start margin
                        + calcDesc.getMarginRight()
                        + calcDesc.getPaddingRight()
                        + calcDesc.getBorder().getHorizontalBorderWidth()
                        + mDataFeedView.getFeedDescriptor().getCalcDesc().getPaddingLeft()  //we must move position regarding parent padding
                );
                mChildPositions.add(new ChildPosition(left, right));

            } else {
                int top = (int) Math.round(calcDesc.getPositionY()
                        + calcDesc.getMarginTop()) + (int) Math.round(mDataFeedView.getFeedDescriptor().getCalcDesc().getPaddingTop()); //we must move position regarding parent padding
                int bottom = (int) Math.round(calcDesc.getPositionY()
                                + calcDesc.getMarginTop()
                                + calcDesc.getHeight()
                                + calcDesc.getMarginBottom()
                                + calcDesc.getPaddingBottom()
                                + calcDesc.getBorder().getVerticalBorderHeight())
                                + (int) Math.round(mDataFeedView.getFeedDescriptor().getCalcDesc().getPaddingTop());
                mChildPositions.add(new ChildPosition(top, bottom));
            }
        }
    }

    public void sortOutChildrenByScrollDirection() {
        Logger.d("Adapter", "SortOutChildrenByScrollDirection");
        if (mDataFeedView.getFeedScrollType() == ScrollType.HORIZONTAL) {
            int leftBound = mDataFeedView.getFeedScrollX();
            int rightBound = leftBound + (int) mDataFeedView.getFeedDescriptor().getCalcDesc().getWidth();
            sortOutChildren(leftBound, rightBound);
        } else {
            int topBound = mDataFeedView.getFeedScrollY();
            int bottomBound = topBound + (int) mDataFeedView.getFeedDescriptor().getCalcDesc().getHeight();
            sortOutChildren(topBound, bottomBound);
        }
    }

    private void sortOutChildren(int startBound, int endBound) {
        //removeViews that are not on screen
        Logger.d("Adapter", "SortOutChildren (loop)");
        boolean isFirstVisible = false;
        boolean isVisible;

        for (int index = 0; index <  mChildPositions.size(); index++) {
            View viewAtX = null;
            if (index == mItems.size()) //mItems is too small - init with null view
                mItems.add(viewAtX);
            isVisible = mChildPositions.get(index).isVisibleOnScreen(startBound, endBound);
            if (isVisible) { //set last visible index PLUS ONE(!) for next loop execution to end closer to visible items (not to max size when scrolling back)
                mLastVisibleIndex = index+1;
                if (!isFirstVisible) {
                    isFirstVisible = true;
                    mFirstVisibleIndex = index; //set first visible index for next loop execution to start closer to visible items (not from 0 when scrolling forward)
                }
            }
            if (mItems.size() > index) {
                viewAtX = mItems.get(index);
                if (viewAtX != null && !isVisible) {
                    mDataFeedView.getScrollView().detachView(viewAtX);
                    saveViewForReuse(viewAtX);
                    mItems.set(index, null);
                }
            }
            if (viewAtX == null && isVisible) {
                addChild(index);
            }
        }
    }

    private void saveViewForReuse(View viewAtX) {
        AbstractAGViewDataDesc descriptor = (AbstractAGViewDataDesc) ((IAGView) viewAtX).getDescriptor();
        if (descriptor instanceof AbstractAGContainerDataDesc) {
            int templateNumber = descriptor.getTemplateNumber();
            mRecycledViews.addViewForIndex(viewAtX, templateNumber);
        }
    }

    /**
     * Adds another item to the   at specified possition. To get childs view it uses an adapter.
     *
     * @param position possition of the element to load in the datafeed
     * @return returns a view of added element
     */
    private void addChild(int position) {
        Logger.d("Adapter", "addChild");
        View recycled = null;
        if (getDescriptor(position) instanceof AbstractAGContainerDataDesc) {
            AbstractAGViewDataDesc desc = (AbstractAGViewDataDesc) getDescriptor(position);
            int templateNumber = desc.getTemplateNumber();
            recycled = getRecycledView(templateNumber);
        }
        addChild(position, getView(position, recycled), recycled != null);
    }

    private void addChild(int position, View view, boolean recycled) {
        mItems.set(position, view);
        mDataFeedView.attachAndLayoutChild(mItems.get(position), recycled);
    }

    private View getRecycledView(int templateNumber) {
        return mRecycledViews.getViewForIndex(templateNumber);
    }

    public boolean accept(IViewVisitor visitor) {
        for (View view : mItems) {
            if (view != null && ((IAGView) view).accept(visitor)) {
                return true;
            }
        }
        return false;
    }
}
