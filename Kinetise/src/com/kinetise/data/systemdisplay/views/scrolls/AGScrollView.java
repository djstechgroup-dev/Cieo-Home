package com.kinetise.data.systemdisplay.views.scrolls;

import android.graphics.Rect;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.OverScroller;

import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.views.AGContainerView;
import com.kinetise.data.systemdisplay.views.IScrollable;
import com.kinetise.support.scrolls.scrollManager.EventDirection;
import com.kinetise.support.scrolls.scrollManager.ScrollManager;
import com.kinetise.support.scrolls.scrollManager.ScrollType;

import java.util.List;

public class AGScrollView extends AGContainerView<AbstractAGContainerDataDesc> implements IScrollable {
    static final int ANIMATED_SCROLL_GAP = 250;
    static final float MAX_SCROLL_FACTOR = 0.5f;

    private final Rect mTempRect = new Rect();

    protected ScrollType mScrollType;

    private long mLastScroll;
    private OverScroller mScroller;
    private float mLastMotion = -1;
    private VelocityTracker mVelocityTracker;
    private boolean mSmoothScrollingEnabled = true;

    public AGScrollView(SystemDisplay display, AbstractAGContainerDataDesc desc, ScrollType scrollType) {
        super(display, desc);
        mScrollType = scrollType;
        mScroller = new OverScroller(getContext());
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setWillNotDraw(false);
        setClipToPadding(true);
    }


    @Override
    public int getScrollXValue() {
        return getScrollX();
    }

    @Override
    public int getScrollYValue() {
        return getScrollY();
    }

    @Override
    public int getViewPortWidth() {
        return getWidth();
    }

    @Override
    public int getViewPortHeight() {
        return getHeight();
    }

    @Override
    public int getContentWidth() {
        return getMaxChildRightPosition() + getPaddingRight() + (int)mCalcDesc.getBorder().getRight();
    }

    @Override
    public int getContentHeight() {
        return getMaxChildBottomPosition() + getPaddingBottom();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = super.onInterceptTouchEvent(ev);

        int actionType = ev.getAction();
        switch (actionType) {
            case MotionEvent.ACTION_DOWN:
                ScrollManager.getInstance().setUpdate(this, ev);
                onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:

                ScrollManager scrollManager = ScrollManager.getInstance();
                scrollManager.setUpdate(this, ev);

                if (scrollManager.getEventDirection() == getEventDirectionForScrollType()) {
                    float deltaY = 0.0f;
                    float deltaX = 0.0f;

                    switch (getEventDirectionForScrollType()) {
                        case UNKNOWN:
                            deltaX = scrollManager.getMotionEventDeltaX();
                            deltaY = scrollManager.getMotionEventDeltaY();
                            break;
                        case VERTICAL:
                            deltaY = scrollManager.getMotionEventDeltaY();
                            break;
                        case HORIZONTAL:
                            deltaX = scrollManager.getMotionEventDeltaX();
                            break;
                        default:
                            break;
                    }

                    result = scrollManager.anyChildCanScroll(this, deltaX, deltaY);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return result;
    }

    @Override
    public String getTag() {
        return "ScrollView";
    }

    public void setScrollY(int value) {
        setDescriptorScrolls(getScrollX(), value);
        scrollTo(getScrollX(), value);
    }

    public void setScrollX(int value) {
        setDescriptorScrolls(value, getScrollY());
        scrollTo(value, getScrollY());
    }

    protected int getChildrenMaxWidth() {
        return getMaxChildRightPosition() - getPaddingLeft();
    }

    protected int getChildrenMaxHeight() {
        return getMaxChildBottomPosition() - getPaddingTop();
    }

    protected int clamp(int n, int my, int child) {
        if (my >= child || n < 0) {
            /* my >= child is this case:
             *                    |--------------- me ---------------|
             *     |------ child ------|
             * or
             *     |--------------- me ---------------|
             *            |------ child ------|
             * or
             *     |--------------- me ---------------|
             *                                  |------ child ------|
             *
             * n < 0 is this case:
             *     |------ me ------|
             *                    |-------- child --------|
             *     |-- mScrollX --|
             */
            return 0;
        }
        if ((my + n) > child) {
            /* this case:
             *                    |------ me ------|
             *     |------ child ------|
             *     |-- mScrollX --|
             */
            return child - my;
        }
        return n;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        boolean result = super.onTouchEvent(ev);

        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            // Don't handle edge touches immediately -- they may actually belong to one of our
            // descendants.
            return false;
        }

        if (!canScroll()) {
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float pos = (mScrollType == ScrollType.HORIZONTAL) ? ev.getRawX() : ev.getRawY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                /*
                * If being flinged and user touches, stop the fling. isFinished
                * will be false if being flinged.
                */
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mScrollType == ScrollType.HORIZONTAL)
                    moveHorizontal(pos);
                else
                    moveVertical(pos);
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity());
                int initialVelocity = (int) ((mScrollType == ScrollType.HORIZONTAL) ? velocityTracker.getXVelocity() : velocityTracker.getYVelocity());

                if ((Math.abs(initialVelocity) >
                        ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity()) &&
                        getChildCount() > 0) {
                    fling(-initialVelocity);
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mLastMotion = -1;
                break;
            default:
                mLastMotion = -1;
                break;
        }

        return result;
    }

    private void moveVertical(float pos) {
        // Scroll to follow the motion event

        if (mLastMotion == -1) {
            mLastMotion = pos;
        }
        final int delta = (int) (mLastMotion - pos);
        mLastMotion = pos;

        if (delta < 0) {
            if (getScrollY() > 0) {
                final int availableToScroll = -getScrollY();
                if (availableToScroll < 0) {
                    scrollViewBy(0, Math.max(availableToScroll, delta));
                }
            }
        } else if (delta > 0) {
            final int bottomEdge = getHeight() - getPaddingBottom();
            final int availableToScroll = getMaxChildBottomPosition() - getScrollY() - bottomEdge;
            if (availableToScroll > 0) {
                scrollViewBy(0, Math.min(availableToScroll, delta));
            }
        }
    }

    private void moveHorizontal(float pos) {
        // Scroll to follow the motion event

        if (mLastMotion == -1) {
            mLastMotion = pos;
        }
        final int delta = (int) (mLastMotion - pos);
        mLastMotion = pos;

        if (delta < 0) {
            final int availableToScroll = -getScrollX();
            if (availableToScroll < 0) {
                scrollViewBy(Math.max(availableToScroll, delta), 0);
            }
        } else if (delta > 0) {
            final int rightEdge = getWidth() - getPaddingRight();
            final int availableToScroll = getMaxChildRightPosition() - getScrollX() - rightEdge;
            if (availableToScroll > 0) {
                scrollViewBy(Math.min(availableToScroll, delta), 0);
            }
        }
    }

    /**
     * @return Returns true this ScrollView can be scrolled
     */
    private boolean canScroll() {
        if (mScrollType == ScrollType.HORIZONTAL)
            return getWidth() < getMaxChildRightPosition() + getPaddingRight();
        else
            return getHeight() < getMaxChildBottomPosition() + getPaddingBottom();
    }

    @Override
    public EventDirection getEventDirectionForScrollType() {
        if (mScrollType == ScrollType.HORIZONTAL)
            return EventDirection.HORIZONTAL;
        else
            return EventDirection.VERTICAL;
    }

    @Override
    public ScrollType getScrollType() {
        return mScrollType;
    }


    @Override
    protected float getLeftFadingEdgeStrength() {
        if (mScrollType != ScrollType.HORIZONTAL)
            return super.getLeftFadingEdgeStrength();

        if (getChildCount() == 0) {
            return 0.0f;
        }

        final int length = getHorizontalFadingEdgeLength();
        if (getScrollX() < length) {
            return getScrollX() / (float) length;
        }

        return 1.0f;
    }

    @Override
    protected float getRightFadingEdgeStrength() {
        if (mScrollType != ScrollType.HORIZONTAL)
            return super.getRightFadingEdgeStrength();

        if (getChildCount() == 0) {
            return 0.0f;
        }

        final int length = getHorizontalFadingEdgeLength();
        final int rightEdge = getWidth() - getPaddingRight();
        final int span = getMaxChildRightPosition() - getScrollX() - rightEdge;
        if (span < length) {
            return span / (float) length;
        }

        return 1.0f;
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        if (mScrollType != ScrollType.VERTICAL)
            return super.getTopFadingEdgeStrength();

        if (getChildCount() == 0) {
            return 0.0f;
        }

        final int length = getVerticalFadingEdgeLength();
        if (getScrollX() < length) {
            return getScrollY() / (float) length;
        }

        return 1.0f;
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        if (mScrollType != ScrollType.VERTICAL)
            return super.getBottomFadingEdgeStrength();

        if (getChildCount() == 0) {
            return 0.0f;
        }

        final int length = getVerticalFadingEdgeLength();
        final int bottomEdge = getHeight() - getPaddingBottom();
        final int span = getMaxChildBottomPosition() - getScrollY() - bottomEdge;
        if (span < length) {
            return span / (float) length;
        }

        return 1.0f;
    }

    /**
     * @return The maximum amount this scroll view will scroll in response to
     * an arrow event.
     */
    public int getMaxScrollAmount() {
        if (mScrollType == ScrollType.HORIZONTAL)
            return (int) (MAX_SCROLL_FACTOR * (getRight() - getLeft()));
        else
            return (int) (MAX_SCROLL_FACTOR * (getBottom() - getTop()));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Let the focused view and/or our descendants get the key first
        boolean handled = super.dispatchKeyEvent(event);
        if (handled) {
            return true;
        }
        return executeKeyEvent(event);
    }

    /**
     * You can call this function yourself to have the scroll view perform
     * scrolling from a key event, just as if the event had been dispatched to
     * it by the view hierarchy.
     *
     * @param event The key event to execute.
     * @return Return true if the event was handled, else false.
     */
    public boolean executeKeyEvent(KeyEvent event) {
        mTempRect.setEmpty();

        if (!canScroll()) {
            if (isFocused()) {
                View currentFocused = findFocus();
                View nextFocused;
                if (currentFocused == this) currentFocused = null;
                if (mScrollType == ScrollType.HORIZONTAL) {
                    nextFocused = FocusFinder.getInstance().findNextFocus(this,
                            currentFocused, View.FOCUS_RIGHT);
                    return nextFocused != null && nextFocused != this &&
                            nextFocused.requestFocus(View.FOCUS_RIGHT);
                } else if (mScrollType == ScrollType.VERTICAL) {
                    nextFocused = FocusFinder.getInstance().findNextFocus(this,
                            currentFocused, View.FOCUS_DOWN);
                    return nextFocused != null && nextFocused != this &&
                            nextFocused.requestFocus(View.FOCUS_DOWN);
                }

            }
            return false;
        }

        boolean handled = false;
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mScrollType != ScrollType.VERTICAL) {
                        if (!event.isAltPressed()) {
                            handled = arrowScroll(View.FOCUS_LEFT);
                        } else {
                            handled = fullScroll(View.FOCUS_LEFT);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (mScrollType != ScrollType.VERTICAL) {
                        if (!event.isAltPressed()) {
                            handled = arrowScroll(View.FOCUS_RIGHT);
                        } else {
                            handled = fullScroll(View.FOCUS_RIGHT);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (mScrollType != ScrollType.HORIZONTAL) {
                        if (!event.isAltPressed()) {
                            handled = arrowScroll(View.FOCUS_UP);
                        } else {
                            handled = fullScroll(View.FOCUS_UP);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (mScrollType != ScrollType.HORIZONTAL) {
                        if (!event.isAltPressed()) {
                            handled = arrowScroll(View.FOCUS_DOWN);
                        } else {
                            handled = fullScroll(View.FOCUS_DOWN);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_SPACE:
                    if (mScrollType != ScrollType.HORIZONTAL)
                        pageScroll(event.isShiftPressed() ? View.FOCUS_LEFT : View.FOCUS_RIGHT);
                    if (mScrollType != ScrollType.VERTICAL)
                        pageScroll(event.isShiftPressed() ? View.FOCUS_UP : View.FOCUS_DOWN);

                    break;
                default:
                    break;
            }
        }

        return handled;
    }

    private View findFocusableViewInBounds(boolean focus, int a, int b) {
        if (mScrollType == ScrollType.HORIZONTAL)
            return findFocusableViewInBoundsHorizontal(focus, a, b);
        else
            return findFocusableViewInBoundsVertical(focus, a, b);
    }

    /**
     * <p>
     * Finds the next focusable component that fits in the specified bounds.
     * </p>
     *
     * @param topFocus look for a candidate is the one at the top of the bounds
     *                 if topFocus is true, or at the bottom of the bounds if topFocus is
     *                 false
     * @param top      the top offset of the bounds in which a focusable must be
     *                 found
     * @param bottom   the bottom offset of the bounds in which a focusable must
     *                 be found
     * @return the next focusable component in the bounds or null if none can
     * be found
     */
    private View findFocusableViewInBoundsVertical(boolean topFocus, int top, int bottom) {

        List<View> focusables = getFocusables(View.FOCUS_FORWARD);
        View focusCandidate = null;

        /*
         * A fully contained focusable is one where its top is below the bound's
         * top, and its bottom is above the bound's bottom. A partially
         * contained focusable is one where some part of it is within the
         * bounds, but it also has some part that is not within bounds.  A fully contained
         * focusable is preferred to a partially contained focusable.
         */
        boolean foundFullyContainedFocusable = false;

        for (View view : focusables) {
            int viewTop = view.getTop();
            int viewBottom = view.getBottom();

            if (top < viewBottom && viewTop < bottom) {
                /*
                 * the focusable is in the target area, it is a candidate for
                 * focusing
                 */

                final boolean viewIsFullyContained = (top < viewTop) &&
                        (viewBottom < bottom);

                if (focusCandidate == null) {
                    /* No candidate, take this one */
                    focusCandidate = view;
                    foundFullyContainedFocusable = viewIsFullyContained;
                } else {
                    final boolean viewIsCloserToBoundary =
                            (topFocus && viewTop < focusCandidate.getTop()) ||
                                    (!topFocus && viewBottom > focusCandidate.getBottom());

                    if (foundFullyContainedFocusable) {
                        if (viewIsFullyContained && viewIsCloserToBoundary) {
                            /*
                             * We're dealing with only fully contained views, so
                             * it has to be closer to the boundary to beat our
                             * candidate
                             */
                            focusCandidate = view;
                        }
                    } else {
                        if (viewIsFullyContained) {
                            /* Any fully contained view beats a partially contained view */
                            focusCandidate = view;
                            foundFullyContainedFocusable = true;
                        } else if (viewIsCloserToBoundary) {
                            /*
                             * Partially contained view beats another partially
                             * contained view if it's closer
                             */
                            focusCandidate = view;
                        }
                    }
                }
            }
        }

        return focusCandidate;
    }

    /**
     * <p>
     * Finds the next focusable component that fits in the specified bounds.
     * </p>
     *
     * @param leftFocus look for a candidate is the one at the left of the bounds
     *                  if leftFocus is true, or at the right of the bounds if
     *                  leftFocus is false
     * @param left      the left offset of the bounds in which a focusable must be
     *                  found
     * @param right     the right offset of the bounds in which a focusable must
     *                  be found
     * @return the next focusable component in the bounds or null if none can
     * be found
     */
    private View findFocusableViewInBoundsHorizontal(boolean leftFocus, int left, int right) {

        List<View> focusables = getFocusables(View.FOCUS_FORWARD);
        View focusCandidate = null;

        /*
         * A fully contained focusable is one where its left is below the bound's
         * left, and its right is above the bound's right. A partially
         * contained focusable is one where some part of it is within the
         * bounds, but it also has some part that is not within bounds.  A fully contained
         * focusable is preferred to a partially contained focusable.
         */
        boolean foundFullyContainedFocusable = false;

        for (View view : focusables) {
            int viewLeft = view.getLeft();
            int viewRight = view.getRight();

            if (left < viewRight && viewLeft < right) {
                /*
                 * the focusable is in the target area, it is a candidate for
                 * focusing
                 */

                final boolean viewIsFullyContained = (left < viewLeft) &&
                        (viewRight < right);

                if (focusCandidate == null) {
                    /* No candidate, take this one */
                    focusCandidate = view;
                    foundFullyContainedFocusable = viewIsFullyContained;
                } else {
                    final boolean viewIsCloserToBoundary =
                            (leftFocus && viewLeft < focusCandidate.getLeft()) ||
                                    (!leftFocus && viewRight > focusCandidate.getRight());

                    if (foundFullyContainedFocusable) {
                        if (viewIsFullyContained && viewIsCloserToBoundary) {
                            /*
                             * We're dealing with only fully contained views, so
                             * it has to be closer to the boundary to beat our
                             * candidate
                             */
                            focusCandidate = view;
                        }
                    } else {
                        if (viewIsFullyContained) {
                            /* Any fully contained view beats a partially contained view */
                            focusCandidate = view;
                            foundFullyContainedFocusable = true;
                        } else if (viewIsCloserToBoundary) {
                            /*
                             * Partially contained view beats another partially
                             * contained view if it's closer
                             */
                            focusCandidate = view;
                        }
                    }
                }
            }
        }

        return focusCandidate;
    }

    /**
     * <p>Handles scrolling in response to a "page up/down" shortcut press. This
     * method will scroll the view by one page left or right and give the focus
     * to the leftmost/rightmost component in the new visible area. If no
     * component is a good candidate for focus, this scrollview reclaims the
     * focus.</p>
     *
     * @param direction the scroll direction: {@link android.view.View#FOCUS_LEFT}
     *                  to go one page left or {@link android.view.View#FOCUS_RIGHT}
     *                  to go one page right
     * @return true if the key event is consumed by this method, false otherwise
     */
    public boolean pageScroll(int direction) {
        int width = getWidth();
        int height = getHeight();
        int count = getChildCount();

        switch (direction) {
            case View.FOCUS_RIGHT:
                mTempRect.left = getScrollX() + width;
                if (count > 0) {
                    if (mTempRect.left + width > getMaxChildRightPosition()) {
                        mTempRect.left = getMaxChildRightPosition() - width;
                    }
                }
                mTempRect.right = mTempRect.left + width;
                return scrollAndFocus(direction, mTempRect.left, mTempRect.right);
            case View.FOCUS_LEFT:
                mTempRect.left = getScrollX() - width;
                if (mTempRect.left < 0) {
                    mTempRect.left = 0;
                }
                mTempRect.right = mTempRect.left + width;
                return scrollAndFocus(direction, mTempRect.left, mTempRect.right);
            case View.FOCUS_UP:
                mTempRect.top = getScrollY() - height;
                if (mTempRect.top < 0) {
                    mTempRect.top = 0;
                }
                mTempRect.bottom = mTempRect.top + height;
                return scrollAndFocus(direction, mTempRect.top, mTempRect.bottom);
            case View.FOCUS_DOWN:
                mTempRect.top = getScrollY() + height;
                if (count > 0) {
                    if (mTempRect.top + height > getMaxChildBottomPosition()) {
                        mTempRect.top = getMaxChildBottomPosition() - height;
                    }
                }
                mTempRect.bottom = mTempRect.top + height;
                return scrollAndFocus(direction, mTempRect.top, mTempRect.bottom);
        }

        return scrollAndFocus(direction, mTempRect.left, mTempRect.right);
    }

    /**
     * <p>Handles scrolling in response to a "home/end" shortcut press. This
     * method will scroll the view to the left or right and give the focus
     * to the leftmost/rightmost component in the new visible area. If no
     * component is a good candidate for focus, this scrollview reclaims the
     * focus.</p>
     *
     * @param direction the scroll direction: {@link android.view.View#FOCUS_LEFT}
     *                  to go the left of the view or {@link android.view.View#FOCUS_RIGHT}
     *                  to go the right
     * @return true if the key event is consumed by this method, false otherwise
     */
    public boolean fullScroll(int direction) {
        if (mScrollType == ScrollType.HORIZONTAL) {
            boolean right = direction == View.FOCUS_RIGHT;
            int width = getWidth();

            mTempRect.left = 0;
            mTempRect.right = width;

            if (right) {
                int count = getChildCount();
                if (count > 0) {
                    mTempRect.right = getMaxChildRightPosition();
                    mTempRect.left = mTempRect.right - width;
                }
            }

            return scrollAndFocus(direction, mTempRect.left, mTempRect.right);
        } else if (mScrollType == ScrollType.VERTICAL) {
            boolean down = direction == View.FOCUS_DOWN;
            int height = getHeight();

            mTempRect.top = 0;
            mTempRect.bottom = height;

            if (down) {
                int count = getChildCount();
                if (count > 0) {
                    mTempRect.bottom = getMaxChildBottomPosition();
                    mTempRect.top = mTempRect.bottom - height;
                }
            }

            return scrollAndFocus(direction, mTempRect.top, mTempRect.bottom);
        } else {
            throw new IllegalArgumentException("Invalid scroll type");
        }
    }

    /**
     * <p>Scrolls the view to make the area defined by <code>leftOrTop</code> and
     * <code>rightOrBottom</code> visible. This method attempts to give the focus
     * to a component visible in this area. If no component can be focused in
     * the new visible area, the focus is reclaimed by this scrollview.</p>
     *
     * @param direction     the scroll direction: {@link android.view.View#FOCUS_LEFT}
     *                      to go leftOrTop {@link android.view.View#FOCUS_RIGHT} to rightOrBottom
     * @param leftOrTop     the leftOrTop offset of the new area to be made visible
     * @param rightOrBottom the rightOrBottom offset of the new area to be made visible
     * @return true if the key event is consumed by this method, false otherwise
     */
    private boolean scrollAndFocus(int direction, int leftOrTop, int rightOrBottom) {
        boolean handled = true;


        if (mScrollType == ScrollType.HORIZONTAL) {
            int width = getWidth();
            int containerLeft = getScrollX();
            int containerRight = containerLeft + width;
            boolean goLeft = direction == View.FOCUS_LEFT;


            if (leftOrTop >= containerLeft && rightOrBottom <= containerRight) {
                handled = false;
            } else {
                int delta = goLeft ? (leftOrTop - containerLeft) : (rightOrBottom - containerRight);
                doScrollX(delta);
            }
        } else {
            int height = getHeight();
            int containerTop = getScrollY();
            int containerBottom = containerTop + height;
            boolean up = direction == View.FOCUS_UP;

            if (leftOrTop >= containerTop && rightOrBottom <= containerBottom) {
                handled = false;
            } else {
                int delta = up ? (leftOrTop - containerTop) : (rightOrBottom - containerBottom);
                doScrollY(delta);
            }
        }

        return handled;
    }

    /**
     * Handle scrolling in response to a left or right arrow click.
     *
     * @param direction The direction corresponding to the arrow key that was
     *                  pressed
     * @return True if we consumed the event, false otherwise
     */
    public boolean arrowScroll(int direction) {

        View currentFocused = findFocus();
        if (currentFocused == this) currentFocused = null;

        View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction);

        final int maxJump = getMaxScrollAmount();

        if (nextFocused != null && isWithinDeltaOfScreen(nextFocused, maxJump)) {
            nextFocused.getDrawingRect(mTempRect);
            offsetDescendantRectToMyCoords(nextFocused, mTempRect);
            int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(mTempRect);
            if (mScrollType == ScrollType.HORIZONTAL)
                doScrollX(scrollDelta);
            else
                doScrollY(scrollDelta);
            nextFocused.requestFocus(direction);
        } else {
            // no new focus
            int scrollDelta = maxJump;
            if (mScrollType == ScrollType.HORIZONTAL) {
                if (direction == View.FOCUS_LEFT && getScrollX() < scrollDelta) {
                    scrollDelta = getScrollX();
                } else if (direction == View.FOCUS_RIGHT) {

                    int daRight = getMaxChildRightPosition();
                    int screenRight = getScrollX() + getWidth();

                    if (daRight - screenRight < maxJump) {
                        scrollDelta = daRight - screenRight;
                    }
                }
            } else {
                if (direction == View.FOCUS_UP && getScrollY() < scrollDelta) {
                    scrollDelta = getScrollY();
                } else if (direction == View.FOCUS_DOWN) {

                    int daBottom = getMaxChildBottomPosition();
                    int screenBottom = getScrollY() + getHeight();

                    if (daBottom - screenBottom < maxJump) {
                        scrollDelta = daBottom - screenBottom;
                    }
                }
            }
            if (scrollDelta == 0) {
                return false;
            }
            if (mScrollType == ScrollType.HORIZONTAL)
                doScrollX(direction == View.FOCUS_RIGHT ? scrollDelta : -scrollDelta);
            else
                doScrollY(direction == View.FOCUS_DOWN ? scrollDelta : -scrollDelta);
        }

        if (currentFocused != null && currentFocused.isFocused()
                && isOffScreen(currentFocused)) {
            // previously focused item still has focus and is off screen, give
            // it up (take it back to ourselves)
            // (also, need to temporarily force FOCUS_BEFORE_DESCENDANTS so we are
            // sure to
            // get it)
            final int descendantFocusability = getDescendantFocusability();  // save
            setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            requestFocus();
            setDescendantFocusability(descendantFocusability);  // restore
        }
        return true;
    }

    /**
     * @return whether the descendant of this scroll view is scrolled off
     * screen.
     */
    private boolean isOffScreen(View descendant) {
        return !isWithinDeltaOfScreen(descendant, 0);
    }

    /**
     * @return whether the descendant of this scroll view is within delta
     * pixels of being on the screen.
     */
    private boolean isWithinDeltaOfScreen(View descendant, int delta) {
        descendant.getDrawingRect(mTempRect);
        offsetDescendantRectToMyCoords(descendant, mTempRect);

        if (mScrollType == ScrollType.HORIZONTAL)
            return (mTempRect.right + delta) >= getScrollX()
                    && (mTempRect.left - delta) <= (getScrollX() + getWidth());
        else
            return (mTempRect.bottom + delta) >= getScrollY()
                    && (mTempRect.top - delta) <= (getScrollY() + getHeight());
    }

    /**
     * Smooth scroll by a X delta
     *
     * @param delta the number of pixels to scroll by on the X axis
     */
    private void doScrollX(int delta) {
        if (delta != 0) {
            if (mSmoothScrollingEnabled) {
                smoothScrollBy(delta, 0);
            } else {
                scrollViewBy(delta, 0);
            }
        }
    }

    /**
     * Smooth scroll by a Y delta
     *
     * @param delta the number of pixels to scroll by on the Y axis
     */
    private void doScrollY(int delta) {
        if (delta != 0) {
            if (mSmoothScrollingEnabled) {
                smoothScrollBy(0, delta);
            } else {
                scrollViewBy(0, delta);
            }
        }
    }

    /**
     * Like {@link View#scrollBy}, but scroll smoothly instead of immediately.
     *
     * @param dx the number of pixels to scroll by on the X axis
     * @param dy the number of pixels to scroll by on the Y axis
     */
    public final void smoothScrollBy(int dx, int dy) {
        long duration = AnimationUtils.currentAnimationTimeMillis() - mLastScroll;
        if (duration > ANIMATED_SCROLL_GAP) {
            mScroller.startScroll(getScrollX(), getScrollY(), dx, dy);
            invalidate();
        } else {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            scrollViewBy(dx, dy);
        }
        mLastScroll = AnimationUtils.currentAnimationTimeMillis();
    }

    /**
     * <p>The scroll range of a scroll view is the overall width of all of its
     * children.</p>
     */
    @Override
    protected int computeHorizontalScrollRange() {
        int count = getChildCount();
        return count == 0 ? getWidth() : getMaxChildRightPosition();
    }

    /**
     * <p>The scroll range of a scroll view is the overall height of all of its
     * children.</p>
     */
    @Override
    protected int computeVerticalScrollRange() {
        int count = getChildCount();
        return count == 0 ? getHeight() : getMaxChildBottomPosition();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            // This is called at drawing time by ViewGroup.  We don't want to
            // re-show the scrollbars at this point, which scrollTo will do,
            // so we replicate most of scrollTo here.
            //
            //         It's a little odd to call onScrollChanged from inside the drawing.
            //
            //         It is, except when you remember that computeScroll() is used to
            //         animate scrolling. So unless we want to defer the onScrollChanged()
            //         until the end of the animated scrolling, we don't really have a
            //         choice here.
            //
            //         I agree.  The alternative, which I think would be worse, is to post
            //         something and tell the subclasses later.  This is bad because there
            //         will be a window where mScrollX/Y is different from what the app
            //         thinks it is.
            //
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            if (getChildCount() > 0) {
                if (oldX != x)
                    setScrollX(clamp(x, getWidth() - getPaddingRight() - getPaddingLeft(), getChildrenMaxWidth()));
                if (oldY != y)
                    setScrollY(clamp(y, getHeight() - getPaddingBottom() - getPaddingTop(), getChildrenMaxHeight()));
            } else {
                if (oldX != x)
                    setScrollX(x);
                if (oldY != y)
                    setScrollY(y);
            }
            if (oldX != getScrollX() || oldY != getScrollY()) {
                onScrollChanged(getScrollX(), getScrollY(), oldX, oldY);
            }

            // Keep on drawing until the animation has finished.
            postInvalidate();
        }
    }

    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        if (mScrollType == ScrollType.HORIZONTAL) {
            return computeHorizontalScrollDeltaToGetChildRectOnScreen(rect);
        } else {
            return computeVerticalScrollDeltaToGetChildRectOnScreen(rect);
        }
    }

    /**
     * Compute the amount to scroll in the X direction in order to get
     * a rectangle completely on the screen (or, if taller than the screen,
     * at least the first screen size chunk of it).
     *
     * @param rect The rect.
     * @return The scroll delta.
     */
    protected int computeVerticalScrollDeltaToGetChildRectOnScreen(Rect rect) {

        int width = getWidth();
        int screenLeft = getScrollX();
        int screenRight = screenLeft + width;

        int fadingEdge = getHorizontalFadingEdgeLength();

        // leave room for left fading edge as long as rect isn't at very left
        if (rect.left > 0) {
            screenLeft += fadingEdge;
        }

        // leave room for right fading edge as long as rect isn't at very right
        if (rect.right < getMaxChildRightPosition()) {
            screenRight -= fadingEdge;
        }

        int scrollXDelta = 0;

        if (rect.right > screenRight && rect.left > screenLeft) {
            // need to move right to get it in view: move right just enough so
            // that the entire rectangle is in view (or at least the first
            // screen size chunk).

            if (rect.width() > width) {
                // just enough to get screen size chunk on
                scrollXDelta += (rect.left - screenLeft);
            } else {
                // get entire rect at right of screen
                scrollXDelta += (rect.right - screenRight);
            }

            // make sure we aren't scrolling beyond the end of our content
            int right = getMaxChildRightPosition();
            int distanceToRight = right - screenRight;
            scrollXDelta = Math.min(scrollXDelta, distanceToRight);

        } else if (rect.left < screenLeft && rect.right < screenRight) {
            // need to move right to get it in view: move right just enough so that
            // entire rectangle is in view (or at least the first screen
            // size chunk of it).

            if (rect.width() > width) {
                // screen size chunk
                scrollXDelta -= (screenRight - rect.right);
            } else {
                // entire rect at left
                scrollXDelta -= (screenLeft - rect.left);
            }

            // make sure we aren't scrolling any further than the left our content
            scrollXDelta = Math.max(scrollXDelta, -getScrollX());
        }
        return scrollXDelta;
    }

    /**
     * Compute the amount to scroll in the Y direction in order to get
     * a rectangle completely on the screen (or, if taller than the screen,
     * at least the first screen size chunk of it).
     *
     * @param rect The rect.
     * @return The scroll delta.
     */
    protected int computeHorizontalScrollDeltaToGetChildRectOnScreen(Rect rect) {

        int height = getHeight();
        int screenTop = getScrollY();
        int screenBottom = screenTop + height;

        int fadingEdge = getVerticalFadingEdgeLength();

        // leave room for top fading edge as long as rect isn't at very top
        if (rect.top > 0) {
            screenTop += fadingEdge;
        }

        // leave room for bottom fading edge as long as rect isn't at very bottom
        if (rect.bottom < getMaxChildBottomPosition()) {
            screenBottom -= fadingEdge;
        }

        int scrollYDelta = 0;

        if (rect.bottom > screenBottom && rect.top > screenTop) {
            // need to move down to get it in view: move down just enough so
            // that the entire rectangle is in view (or at least the first
            // screen size chunk).

            if (rect.height() > height) {
                // just enough to get screen size chunk on
                scrollYDelta += (rect.top - screenTop);
            } else {
                // get entire rect at bottom of screen
                scrollYDelta += (rect.bottom - screenBottom);
            }

            // make sure we aren't scrolling beyond the end of our content
            int bottom = getMaxChildBottomPosition();
            int distanceToBottom = bottom - screenBottom;
            scrollYDelta = Math.min(scrollYDelta, distanceToBottom);

        } else if (rect.top < screenTop && rect.bottom < screenBottom) {
            // need to move up to get it in view: move up just enough so that
            // entire rectangle is in view (or at least the first screen
            // size chunk of it).

            if (rect.height() > height) {
                // screen size chunk
                scrollYDelta -= (screenBottom - rect.bottom);
            } else {
                // entire rect at top
                scrollYDelta -= (screenTop - rect.top);
            }

            // make sure we aren't scrolling any further than the top our content
            scrollYDelta = Math.max(scrollYDelta, -getScrollY());
        }
        return scrollYDelta;
    }


    /**
     * When looking for focus in children of a scroll view, need to be a little
     * more careful not to give focus to something that is scrolled off screen.
     * <p/>
     * This is more expensive than the default {@link android.view.ViewGroup}
     * implementation, otherwise this behavior might have been made the default.
     */
    @Override
    protected boolean onRequestFocusInDescendants(int direction,
                                                  Rect previouslyFocusedRect) {

        // convert from forward / backward notation to up / down / left / right
        // (ugh).
        if (direction == View.FOCUS_FORWARD) {
            direction = (mScrollType == ScrollType.HORIZONTAL) ? View.FOCUS_RIGHT : View.FOCUS_DOWN;
        } else if (direction == View.FOCUS_BACKWARD) {
            direction = (mScrollType == ScrollType.HORIZONTAL) ? View.FOCUS_LEFT : View.FOCUS_UP;
        }

        final View nextFocus = previouslyFocusedRect == null ?
                FocusFinder.getInstance().findNextFocus(this, null, direction) :
                FocusFinder.getInstance().findNextFocusFromRect(this,
                        previouslyFocusedRect, direction);

        if (nextFocus == null) {
            return false;
        }

        if (isOffScreen(nextFocus)) {
            return false;
        }

        return nextFocus.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        View currentFocused = findFocus();
        if (null == currentFocused || this == currentFocused)
            return;

        final int maxJump = (mScrollType == ScrollType.HORIZONTAL) ? getRight() - getLeft() : getBottom() - getTop();

        if (isWithinDeltaOfScreen(currentFocused, maxJump)) {
            currentFocused.getDrawingRect(mTempRect);
            offsetDescendantRectToMyCoords(currentFocused, mTempRect);
            int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(mTempRect);
            if (mScrollType == ScrollType.HORIZONTAL)
                doScrollX(scrollDelta);
            else
                doScrollY(scrollDelta);
        }
    }

    /**
     * Fling the scroll view
     *
     * @param velocity The initial velocity in the X direction. Positive
     *                 numbers mean that the finger/curor is moving down the screen,
     *                 which means we want to scroll towards the left.
     */
    public void fling(int velocity) {
        if (mScrollType == ScrollType.HORIZONTAL)
            flingHorizontal(velocity);
        else
            flingVertical(velocity);
    }

    public void flingHorizontal(int velocityX) {
        int width = getWidth() - getPaddingRight() - getPaddingLeft();
        int right = getMaxChildRightPosition();

        mScroller.fling(getScrollX(), getScrollY(), velocityX, 0, 0, Math.max(0, right - width), 0, 0, width / 2, 0);

        invalidate();
    }

    /**
     * Fling the scroll view
     *
     * @param velocityY The initial velocity in the Y direction. Positive
     *                  numbers mean that the finger/curor is moving down the screen,
     *                  which means we want to scroll towards the top.
     */
    public void flingVertical(int velocityY) {
        int height = getHeight() - getPaddingBottom() - getPaddingTop();
        int bottom = getMaxChildBottomPosition();

        mScroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, 0, Math.max(0, bottom - height), 0, height / 2);

        invalidate();
    }

    private void scrollViewBy(int x, int y) {
        setDescriptorScrolls(getScrollX() + x, getScrollY() + y);
        scrollBy(x, y);
    }

    public void scrollViewTo(int x, int y) {
        scrollTo(x, y);
    }

    /**
     * Restores last position of scrolls. However, sth view layout is triggered before all children are rendered. As restore scrolls uses scrollTo that
     * updates descriptors we need to reupdate them with old values in case body layouts one more time. In other words - we should set descriptors only
     * in case user scrolls view.
     */
    public void restoreScroll() {
        int oldX = getDescriptor().getScrollX();
        int oldY = getDescriptor().getScrollY();
        scrollViewTo(oldX, oldY);
        setDescriptorScrolls(oldX, oldY);
    }

}
