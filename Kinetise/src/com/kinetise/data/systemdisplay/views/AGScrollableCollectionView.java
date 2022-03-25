package com.kinetise.data.systemdisplay.views;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.OverScroller;
import com.kinetise.data.descriptors.AbstractAGSectionDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.support.logger.Logger;
import com.kinetise.support.scrolls.scrollManager.EventDirection;
import com.kinetise.support.scrolls.scrollManager.ScrollManager;
import com.kinetise.support.scrolls.scrollManager.ScrollType;

import java.util.List;

public abstract class AGScrollableCollectionView extends AbstractAGCollectionView implements IScrollable {

    private static final int ANIMATED_SCROLL_GAP = 250;
    private static final float MAX_SCROLL_FACTOR = 0.5f;

    protected int mMaxChildRightPosition = 0;
    protected int mMaxChildBottomPosition = 0;

    private final Rect mTempRect = new Rect();
    private long mLastScroll;
    protected OverScroller mScroller;

    /**
     * Position of the last motion event.
     */
    private float mLastMotionY =-1;

    /**
     * The child to give focus to in the event that a child has requested focus while the
     * layout is dirty. This prevents the scroll from being wrong if the child has not been
     * laid out before requesting focus.
     */
    private View mChildToScrollTo = null;

    /**
     * Determines speed during touch scrolling
     */
    private VelocityTracker mVelocityTracker;

    /**
     * When set to true, the scroll view measure its child to make it fill the currently
     * visible area.
     */
    private boolean mFillViewport;

    /**
     * Whether arrow scrolling is animated.
     */
    private boolean mSmoothScrollingEnabled = true;

    public AGScrollableCollectionView(SystemDisplay display, AbstractAGSectionDataDesc desc) {
        super(display, desc);
        initScroll();
    }

    private void initScroll() {

        mScroller = new OverScroller(getContext());
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setWillNotDraw(false);
        setClipToPadding(true);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // Give a child focus if it needs it
        if (mChildToScrollTo != null && isViewDescendantOf(mChildToScrollTo, this)) {
            scrollToChild(mChildToScrollTo);
        }
        mChildToScrollTo = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        boolean result = true;

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
        final float y = ev.getY();

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
                // Scroll to follow the motion event
            	if(mLastMotionY == -1){
            		mLastMotionY = y;
            	}
                final int deltaY = (int) (mLastMotionY - y);
                mLastMotionY = y;

                if (deltaY < 0) {
                    if (getScrollY() > 0) {
                        final int availableToScroll = -getScrollY();
                        if (availableToScroll < 0) {
                            scrollViewBy(0, Math.max(availableToScroll, deltaY));
                        }
                    }
                } else if (deltaY > 0) {
                    final int bottomEdge = getHeight() - getPaddingBottom();
                    final int availableToScroll = getMaxChildBottomPosition() - getScrollY() - bottomEdge;
                    if (availableToScroll > 0) {
                        scrollViewBy(0, Math.min(availableToScroll, deltaY));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity());
                int initialVelocity = (int) velocityTracker.getYVelocity();

                if ((Math.abs(initialVelocity) >
                        ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity()) &&
                        getChildCount() > 0) {
                    fling(-initialVelocity);
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mLastMotionY = -1;
                break;
            case MotionEvent.ACTION_CANCEL:
            	mLastMotionY = -1;
                break;
            default:
                break;
        }

        return result;
    }

    private boolean canScroll() {
        return getHeight() < getMaxChildBottomPosition() + getPaddingBottom();
    }

    @Override
    public EventDirection getEventDirectionForScrollType() {
        return EventDirection.VERTICAL;
    }

    @Override
    public ScrollType getScrollType() {
        return ScrollType.VERTICAL;
    }

    @Override
    protected float getTopFadingEdgeStrength() {
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
     *         an arrow event.
     */
    public int getMaxScrollAmount() {
        return (int) (MAX_SCROLL_FACTOR * (getBottom() - getTop()));
    }

    /**
     * Indicates whether this ScrollView's content is stretched to fill the viewport.
     *
     * @return True if the content fills the viewport, false otherwise.
     */
    public boolean isFillViewport() {
        return mFillViewport;
    }

    /**
     * Indicates this ScrollView whether it should stretch its content height to fill
     * the viewport or not.
     *
     * @param fillViewport True to stretch the content's height to the viewport's
     *                     boundaries, false otherwise.
     */
    public void setFillViewport(boolean fillViewport) {
        if (fillViewport != mFillViewport) {
            mFillViewport = fillViewport;
            requestLayout();
        }
    }

    /**
     * @return Whether arrow scrolling will animate its transition.
     */
    public boolean isSmoothScrollingEnabled() {
        return mSmoothScrollingEnabled;
    }

    /**
     * Set whether arrow scrolling will animate its transition.
     *
     * @param smoothScrollingEnabled whether arrow scrolling will animate its transition
     */
    public void setSmoothScrollingEnabled(boolean smoothScrollingEnabled) {
        mSmoothScrollingEnabled = smoothScrollingEnabled;
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
                if (currentFocused == this) currentFocused = null;
                View nextFocused = FocusFinder.getInstance().findNextFocus(this,
                        currentFocused, View.FOCUS_DOWN);
                return nextFocused != null
                        && nextFocused != this
                        && nextFocused.requestFocus(View.FOCUS_DOWN);
            }
            return false;
        }

        boolean handled = false;
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (!event.isAltPressed()) {
                        handled = arrowScroll(View.FOCUS_UP);
                    } else {
                        handled = fullScroll(View.FOCUS_UP);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (!event.isAltPressed()) {
                        handled = arrowScroll(View.FOCUS_DOWN);
                    } else {
                        handled = fullScroll(View.FOCUS_DOWN);
                    }
                    break;
                case KeyEvent.KEYCODE_SPACE:
                    pageScroll(event.isShiftPressed() ? View.FOCUS_UP : View.FOCUS_DOWN);
                    break;
                default:
                    break;
            }
        }

        return handled;
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
     *         be found
     */
    private View findFocusableViewInBounds(boolean topFocus, int top, int bottom) {

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

        int count = focusables.size();
        for (int i = 0; i < count; i++) {
            View view = focusables.get(i);
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
                                    (!topFocus && viewBottom > focusCandidate
                                            .getBottom());

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
     * method will scroll the view by one page up or down and give the focus
     * to the topmost/bottommost component in the new visible area. If no
     * component is a good candidate for focus, this scrollview reclaims the
     * focus.</p>
     *
     * @param direction the scroll direction: {@link android.view.View#FOCUS_UP}
     *                  to go one page up or
     *                  {@link android.view.View#FOCUS_DOWN} to go one page down
     * @return true if the key event is consumed by this method, false otherwise
     */
    public boolean pageScroll(int direction) {
        boolean down = direction == View.FOCUS_DOWN;
        int height = getHeight();

        if (down) {
            mTempRect.top = getScrollY() + height;
            int count = getChildCount();
            if (count > 0) {
                if (mTempRect.top + height > getMaxChildBottomPosition()) {
                    mTempRect.top = getMaxChildBottomPosition() - height;
                }
            }
        } else {
            mTempRect.top = getScrollY() - height;
            if (mTempRect.top < 0) {
                mTempRect.top = 0;
            }
        }
        mTempRect.bottom = mTempRect.top + height;

        return scrollAndFocus(direction, mTempRect.top, mTempRect.bottom);
    }

    /**
     * <p>Handles scrolling in response to a "home/end" shortcut press. This
     * method will scroll the view to the top or bottom and give the focus
     * to the topmost/bottommost component in the new visible area. If no
     * component is a good candidate for focus, this scrollview reclaims the
     * focus.</p>
     *
     * @param direction the scroll direction: {@link android.view.View#FOCUS_UP}
     *                  to go the top of the view or
     *                  {@link android.view.View#FOCUS_DOWN} to go the bottom
     * @return true if the key event is consumed by this method, false otherwise
     */
    public boolean fullScroll(int direction) {
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
    }

    /**
     * <p>Scrolls the view to make the area defined by <code>top</code> and
     * <code>bottom</code> visible. This method attempts to give the focus
     * to a component visible in this area. If no component can be focused in
     * the new visible area, the focus is reclaimed by this scrollview.</p>
     *
     * @param direction the scroll direction: {@link android.view.View#FOCUS_UP}
     *                  to go upward
     *                  {@link android.view.View#FOCUS_DOWN} to downward
     * @param top       the top offset of the new area to be made visible
     * @param bottom    the bottom offset of the new area to be made visible
     * @return true if the key event is consumed by this method, false otherwise
     */
    private boolean scrollAndFocus(int direction, int top, int bottom) {
        boolean handled = true;

        int height = getHeight();
        int containerTop = getScrollY();
        int containerBottom = containerTop + height;
        boolean up = direction == View.FOCUS_UP;

        View newFocused = findFocusableViewInBounds(up, top, bottom);
        if (newFocused == null) {
            newFocused = this;
        }

        if (top >= containerTop && bottom <= containerBottom) {
            handled = false;
        } else {
            int delta = up ? (top - containerTop) : (bottom - containerBottom);
            doScrollY(delta);
        }

        if (newFocused != findFocus()) {
        	newFocused.requestFocus(direction);
        }

        return handled;
    }

    /**
     * Handle scrolling in response to an up or down arrow click.
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
            doScrollY(scrollDelta);
            nextFocused.requestFocus(direction);
        } else {
            // no new focus
            int scrollDelta = maxJump;

            if (direction == View.FOCUS_UP && getScrollY() < scrollDelta) {
                scrollDelta = getScrollY();
            } else if (direction == View.FOCUS_DOWN) {

                int daBottom = getMaxChildBottomPosition();

                int screenBottom = getScrollY() + getHeight();

                if (daBottom - screenBottom < maxJump) {
                    scrollDelta = daBottom - screenBottom;
                }
            }
            if (scrollDelta == 0) {
                return false;
            }
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
     *         screen.
     */
    private boolean isOffScreen(View descendant) {
        return !isWithinDeltaOfScreen(descendant, 0);
    }

    /**
     * @return whether the descendant of this scroll view is within delta
     *         pixels of being on the screen.
     */
    private boolean isWithinDeltaOfScreen(View descendant, int delta) {
        descendant.getDrawingRect(mTempRect);
        offsetDescendantRectToMyCoords(descendant, mTempRect);

        return (mTempRect.bottom + delta) >= getScrollY()
                && (mTempRect.top - delta) <= (getScrollY() + getHeight());
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
     * Like {@link #scrollTo}, but scroll smoothly instead of immediately.
     *
     * @param x the position where to scroll on the X axis
     * @param y the position where to scroll on the Y axis
     */
    public final void smoothScrollTo(int x, int y) {
        smoothScrollBy(x - getScrollX(), y - getScrollY());
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
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();

        int childWidthMeasureSpec;
        int childHeightMeasureSpec;

        childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft()
                + getPaddingRight(), lp.width);

        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin
                        + widthUsed, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                               getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin + heightUsed, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {

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

    /**
     * Scrolls the view to the given child.
     *
     * @param child the View to scroll to
     */
    private void scrollToChild(View child) {
        child.getDrawingRect(mTempRect);

        /* Offset from child's local coordinates to ScrollView coordinates */
        offsetDescendantRectToMyCoords(child, mTempRect);

        int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(mTempRect);

        if (scrollDelta != 0) {
            scrollBy(0, scrollDelta);
        }
    }

    /**
     * Compute the amount to scroll in the Y direction in order to get
     * a rectangle completely on the screen (or, if taller than the screen,
     * at least the first screen size chunk of it).
     *
     * @param rect The rect.
     * @return The scroll delta.
     */
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {

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
        return true;
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle,
                                                 boolean immediate) {
        // offset into coordinate space of this scroll view
        rectangle.offset(child.getLeft() - child.getScrollX(),
                child.getTop() - child.getScrollY());

        return false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        View currentFocused = findFocus();
        if (null == currentFocused || this == currentFocused)
            return;

        final int maxJump = getBottom() - getTop();

        if (isWithinDeltaOfScreen(currentFocused, maxJump)) {
            currentFocused.getDrawingRect(mTempRect);
            offsetDescendantRectToMyCoords(currentFocused, mTempRect);
            int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(mTempRect);
            doScrollY(scrollDelta);
        }
    }

    /**
     * Return true if child is an descendant of parent, (or equal to the parent).
     */
    private boolean isViewDescendantOf(View child, View parent) {
        if (child == parent) {
            return true;
        }

        final ViewParent theParent = child.getParent();
        return (theParent instanceof ViewGroup) && isViewDescendantOf((View) theParent, parent);
    }

    /**
     * Fling the scroll view
     *
     * @param velocityY The initial velocity in the Y direction. Positive
     *                  numbers mean that the finger/curor is moving down the screen,
     *                  which means we want to scroll towards the top.
     */
    public void fling(int velocityY) {
        int height = getHeight() - getPaddingBottom() - getPaddingTop();
        int bottom = getMaxChildBottomPosition();

        mScroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, 0, Math.max(0, bottom - height), 0, height / 2);
        final boolean movingDown = velocityY > 0;

        View newFocused = this;

        if (newFocused != findFocus()) {
        	newFocused.requestFocus(movingDown ? View.FOCUS_DOWN : View.FOCUS_UP);
        }

        invalidate();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>This version also clamps the scrolling to the bounds of our child.
     */
    public void scrollTo(int x, int y) {

        // we rely on the fact the View.scrollBy calls scrollTo.
        if (getChildCount() > 0) {
            x = clamp(x, getWidth() - getPaddingRight() - getPaddingLeft(), getChildrenMaxWidth());
            y = clamp(y, getHeight() - getPaddingBottom() - getPaddingTop(), getChildrenMaxHeight());
            if (x != getScrollX() || y != getScrollY()) {
                setDescriptorScrolls(x, y);
                super.scrollTo(x, y);
            }
        }
    }

    public void scrollToDeepChild(View child) {
        Point childOffset = new Point();

        getDeepChildOffset(child.getParent(), child, childOffset);


        Rect childRect = new Rect(childOffset.x, childOffset.y, childOffset.x + child.getWidth(), childOffset.y + child.getHeight());
        int deltay = computeScrollDeltaToGetChildRectOnScreen(childRect);
        smoothScrollBy(0, deltay);
    }

    private void getDeepChildOffset(ViewParent nextParent, View nextChild, Point accumulatedOffset) {
        ViewGroup parent = (ViewGroup) nextParent;
        accumulatedOffset.x += nextChild.getLeft();
        accumulatedOffset.y += nextChild.getTop();
        if (parent == this) {
            return;
        }
        getDeepChildOffset(parent.getParent(), parent, accumulatedOffset);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = false;

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
                    }

                    result = scrollManager.anyChildCanScroll(this, deltaX, deltaY) && (int) deltaY != 0 && (int) deltaX != 0;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        Logger.v(this, "onInterceptTouchEvent", "Event[" + ev.getAction() + "], result[" + result + "], desc[" + getDescriptor().toString() + "]");

        return result;
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
        return getMaxChildRightPosition() + getPaddingRight();
    }

    @Override
    public int getContentHeight() {
        return getMaxChildBottomPosition() + getPaddingBottom();
    }

    @Override
    public String getTag() {
        return "ScrollView";
    }

    public void setScrollY(int value) {
        scrollTo(getScrollX(), value);
    }

    public void setScrollX(int value) {
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

    public int getMaxChildRightPosition() {
        return mMaxChildRightPosition;
    }

    public int getMaxChildBottomPosition() {
        return mMaxChildBottomPosition;
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
