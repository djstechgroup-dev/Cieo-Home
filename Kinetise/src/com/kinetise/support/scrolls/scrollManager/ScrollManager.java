package com.kinetise.support.scrolls.scrollManager;

import android.content.res.Resources;
import android.view.MotionEvent;
import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.systemdisplay.views.IScrollable;
import com.kinetise.data.systemdisplay.views.scrolls.AGScrollView;
import com.kinetise.support.logger.Logger;
import com.kinetise.support.scrolls.scrollManager.scrollStack.ScrollStack;
import com.kinetise.support.scrolls.scrollManager.scrollStack.ScrollStackElement;

import java.util.Vector;

public class ScrollManager {

    private static ScrollManager mInstance = null;
    private EventDirection mEventDirection = EventDirection.UNKNOWN;

    private ScrollMotionEvent mStartMotionEvent = null;
    private ScrollMotionEvent mPreviousMotionEvent = null;
    private ScrollMotionEvent mCurrentMotionEvent = null;
    private static float sDensity = Resources.getSystem().getDisplayMetrics().density;

    private final static int SLOP = (int) Math.ceil(4 * sDensity);

    private ScrollManager() {
        mStartMotionEvent = new ScrollMotionEvent();
        mPreviousMotionEvent = new ScrollMotionEvent();
        mCurrentMotionEvent = new ScrollMotionEvent();
    }

    public static ScrollManager getInstance() {
        if (mInstance == null) {
            synchronized (ScrollManager.class){
                if (mInstance == null) {
                    mInstance = new ScrollManager();
                }
            }
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    /**
     * @return {@link EventDirection#VERTICAL} or {@link EventDirection#HORIZONTAL} for last touched scroll or {@link EventDirection#UNKNOWN} if no scroll was touched
     */
    public EventDirection getEventDirection() {
        return mEventDirection;
    }

    /**
     * Used to updateDate information about scrolling:<br>
     * if current {@link EventDirection} is {@link EventDirection#UNKNOWN} than we try to resolve EventDirection<br>
     * if MotionEvent is {@link MotionEvent#ACTION_DOWN} and {@link MotionEvent#getDownTime()} is different than saved than we {@link ScrollManager#resetScrollManager()}<br>
     * if MotionEvent is {@link MotionEvent#ACTION_DOWN} and {@link MotionEvent#getDownTime()} is same as saved, than we add {@code scroll} to scroll stack<br>
     * 
     * @param scroll current touched scroll
     * @param motionEvent that initiated scroll
     */
    public void setUpdate(IScrollable scroll, MotionEvent motionEvent) {

        if (motionEvent.getDownTime() != mStartMotionEvent.getDownTime()
                && motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            resetScrollManager();
            mStartMotionEvent.set(motionEvent);
        }

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ScrollStack.getStack().add(new ScrollStackElement(scroll));
        }

        if (motionEvent.getEventTime() != mCurrentMotionEvent.getEventTime()) {
            mPreviousMotionEvent.set(mCurrentMotionEvent);
        }

        mCurrentMotionEvent.set(motionEvent);

        if (getEventDirection() == EventDirection.UNKNOWN) {
        	mEventDirection = getEventDirection(mStartMotionEvent, mCurrentMotionEvent);
        }
    }
    
    /**
     * Checks direction of MotionEvent based on start and end positions of motion event
     * @param startMotionEvent start point of motion event for which we check direction
     * @param endMotionEvent end point of motion event for which we check direction
     * @return {@link EventDirection#UNKNOWN} if {@code startMotionEvent} and {@code endMotionEvent} horizontal and vertical deltas are less then {@link ScrollManager#SLOP}, otherwise
     * {@link EventDirection#HORIZONTAL} or {@link EventDirection#VERTICAL} 
     */
    private EventDirection getEventDirection(ScrollMotionEvent startMotionEvent, ScrollMotionEvent endMotionEvent){
        float deltaX = Math.abs(startMotionEvent.getRawX()
                - endMotionEvent.getRawX()) * 1 / sDensity;
        float deltaY = Math.abs(startMotionEvent.getRawY()
                - endMotionEvent.getRawY()) * 1 / sDensity;
        Logger.v(this,"getEventDirection", "eventyType:" + endMotionEvent.toString() + " deltaX:" + deltaX + " deltaY:" +
                deltaY);

        if (deltaX > SLOP || deltaY > SLOP) {
            if (deltaX > deltaY) {
                Logger.v(this, "getEventDirection", "Set EventDirection.HORIZONTAL");
                return EventDirection.HORIZONTAL;
            } else {
                Logger.v(this,"getEventDirection", "set EventDirection.VERTICAL");
                return EventDirection.VERTICAL;
            }
        }
        return EventDirection.UNKNOWN;
    }

    /**
     * Resets this ScrollManager
     * Clears:<br>
     * ScrollStack<br>
     * Start motion event<br>
     * Current motion event<br>
     * Previous motion event<br>
     * EventDirection
     */
    public void resetScrollManager() {
    	Logger.v(this,"resetScrollManager");
        ScrollStack.clear();
        mEventDirection = EventDirection.UNKNOWN;
        mStartMotionEvent.reset();
        mPreviousMotionEvent.reset();
        mCurrentMotionEvent.reset();
    }

    /**
     * Check {@link ScrollManager#canScrollBy(IScrollable, float, float)} for details
     */
    public boolean canIScrollBy(IScrollable scroll, float deltaX, float deltaY) {
        return canScrollBy(scroll, deltaX, deltaY);
    }

    /**
     * Checks if any child scroll can scroll by given values
     * @param scroll of which children we want to check
     * @param deltaX MotionEvent motion delta horizontal
     * @param deltaY MotionEvent motion delta vertical
     * @return true if none of child scrolls can scroll, false otherwise
     */
    public boolean anyChildCanScroll(IScrollable scroll,
                                     float deltaX, float deltaY) {

        boolean result = true;

        Vector<ScrollStackElement> scrollStack = ScrollStack.getStack();

        boolean found = false;
        for (int i = 0; i < scrollStack.size(); i++) {
            if (found) {

                if (canScrollByWithDownEventTest(scrollStack.get(i), deltaX, deltaY)) {
                    result = false;
                    break;
                }
            }

            if (scroll == scrollStack.get(i).getScroll()) {
                found = true;
            }
        }

        return result;
    }

    /**
     * Checks if {@code scroll} can scroll by given deltaX and deltaY
     * @param scroll which we want to check if can scroll by
     * @param deltaX horizontal delta of motion event
     * @param deltaY vertical delta of motion event
     * @return true if given scroll can be scrolled by deltaX or deltaY
     */
    private boolean canScrollBy(IScrollable scroll, float deltaX, float deltaY) {

    	Logger.v(this,"canScrollBy", String.format("Can scroll by: deltaX[%f], deltaY[%f], scroll[%s]", deltaX, deltaY, scroll.toString()));

        boolean resultVertical = false;
        boolean resultHorizontal = false;
        if(scroll instanceof AGScrollView){
        	AbstractAGContainerDataDesc desc = (AbstractAGContainerDataDesc) ((AGScrollView)scroll).getDescriptor();
        	resultVertical = !desc.isScrollVertical();
        	resultHorizontal = !desc.isScrollHorizontal();
        }

        int rangeTop = scroll.getScrollYValue();
        int contentHeight = scroll.getContentHeight();
        int viewPortHeight = scroll.getViewPortHeight();
        int rangeBottom = contentHeight -viewPortHeight - rangeTop;

        int rangeLeft = scroll.getScrollXValue();
        int rangeRight = scroll.getContentWidth() - scroll.getViewPortWidth()
                - scroll.getScrollXValue();

        Logger.v(this,"canScrollBy", "rangeTop:" + rangeTop + " rangeBottom:"
                + rangeBottom + " deltaY:" + deltaY + "rangeLeft:" + rangeLeft + " rangeRight:"
                + rangeRight + " deltaX:" + deltaX);

        if (canScroll(deltaY, rangeTop, rangeBottom)) {
            resultVertical = true;
        }

        if (canScroll(deltaX, rangeRight, rangeLeft)) {
            resultHorizontal = true;
        }

        return (resultVertical && resultHorizontal);
    }

    private boolean canScroll(float scrollDelta, int maxScrollValue, int minScrollValue) {
        return (maxScrollValue == 0 || maxScrollValue!=-minScrollValue) //for some reason sth (after first show more) maxScrollValue is 1 and minScrollValue is -1 (and P2R doesn't work)
                && (scrollDelta == 0 || (scrollDelta > 0 && maxScrollValue > 0) || (scrollDelta < 0 && minScrollValue > 0));
    }

    /**
     * Checks if {@code scrollStackElement} can scroll by given deltaX and deltaY
     * @param scrollStackElement which we want to check if can scroll by
     * @param deltaX horizontal delta of motion event
     * @param deltaY vertical delta of motion event
     * @return true if given scroll can be scrolled by deltaX or deltaY
     */
    private boolean canScrollByWithDownEventTest(
            ScrollStackElement scrollStackElement, float deltaX, float deltaY) {

        boolean resultVertical = false;
        boolean resultHorizontal = false;
        //some hot fix for hover effect on AGButtonView
        //Logger.v(this, "canScrollByWithDownEventTest", "scrollStackElement value:" + scrollStackElement.getScroll().toString());

        int rangeTop = scrollStackElement.getFreeSpaceTop();
        int rangeBottom = scrollStackElement.getFreeSpaceBottom();
        int rangeLeft = scrollStackElement.getFreeSpaceLeft();
        int rangeRight = scrollStackElement.getFreeSpaceRight();
        

//        Logger.v(this,"canScrollByWithDownEventTest", scrollStackElement.getScroll().getTag() + ": canScrollByWithDownEventTest rangeTop:" + rangeTop
//                + " rangeBottom:" + rangeBottom + " deltaY:" + deltaY + " rangeLeft:" + rangeLeft
//                + " rangeRight:" + rangeRight + " deltaX:" + deltaX);

        if (canScroll(deltaY, rangeTop, rangeBottom)) {
        	if(deltaY == 0){
        		resultVertical = true;
        	} else {
        		resultVertical = scrollStackElement.getScroll().getScrollType() == ScrollType.VERTICAL ||
        				scrollStackElement.getScroll().getScrollType() == ScrollType.FREESCROLL;
        	}
        }

        if (canScroll(deltaX, rangeLeft, rangeRight)) {
        	if(deltaX == 0){
        		resultHorizontal = true;
        	} else {
        		resultHorizontal = scrollStackElement.getScroll().getScrollType() == ScrollType.HORIZONTAL ||
        				scrollStackElement.getScroll().getScrollType() == ScrollType.FREESCROLL;
        	}
        }

        //Logger.v("TouchManager", scrollStackElement.getScroll().getTag() + ": canScrollByWithDownEventTest resultVertical:" + resultVertical + " resultHorizontal: " + resultHorizontal + "\n\n");
        return resultVertical && resultHorizontal;
    }

    /**
     * @return horizontal delta for last motion event
     */
    public float getMotionEventDeltaY() {
        float result = 0.0f;
        if (mPreviousMotionEvent != null && mCurrentMotionEvent != null) {
            result = (mCurrentMotionEvent.getRawY() - mPreviousMotionEvent.getRawY()) * 1 / sDensity;
            return result;
        }
        return result;
    }

    /**
     * @return vertical delta for last motion event
     */
    public float getMotionEventDeltaX() {
        float result = 0.0f;
        if (mPreviousMotionEvent != null && mCurrentMotionEvent != null) {
            result = (mCurrentMotionEvent.getRawX() - mPreviousMotionEvent.getRawX()) * 1 / sDensity;
            return result;
        }
        return result;
    }

}
