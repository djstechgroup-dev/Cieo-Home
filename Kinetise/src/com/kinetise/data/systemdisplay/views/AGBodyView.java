package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Region;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

import com.kinetise.components.activity.KinetiseActivity;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.feedmanager.FeedManager;
import com.kinetise.data.descriptors.AGBodyDataDesc;
import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGSectionCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.helpers.drawing.ViewDrawer;
import com.kinetise.support.scrolls.scrollManager.ScrollManager;

import java.util.List;


public class AGBodyView extends AGScrollableCollectionView implements IAGSectionView {


    private static final int ANIMATION_STANDARD_TIME = 300;
    private static final int ANIMATION_BASE_TIME = 1;

    private AGBodyDataDesc mDataDesc;

    private SystemDisplay mDisplay;
    private boolean mPulling = false;
    private PullToRefreshAnimator pullToRefreshAnimator;

    private int mWidth;
    private int mHeight;
    private float mLastTranslation = 0;

    private float mStartY;
    private float mTranslation;
    private float mMaxTranslation;
    private TranslateAnimation animation;
    private boolean mPulled;

    private int mHeaderHeight;
    private int mNaviPanelHeight;

    public AGBodyView(SystemDisplay display, AGBodyDataDesc desc) {
        super(display, desc);

        mDisplay = display;
        mDataDesc = desc;

        mMaxTranslation = Math.max(mDisplay.getWidth(), mDisplay.getHeight()) / (float) 15;
        pullToRefreshAnimator = new PullToRefreshAnimator();

        setWillNotDraw(false);
    }


    private void setHeaderHeight() {
        AGScreenDataDesc screenDataDesc = (AGScreenDataDesc) mDataDesc.getParent();
        if (screenDataDesc.getScreenHeader() != null) {
            mHeaderHeight = (int) screenDataDesc.getScreenHeader().getCalcDesc().getHeight();
        } else {
            mHeaderHeight = 0;
        }
    }

    private void setNavipanelHeight() {
        AGScreenDataDesc screenDataDesc = (AGScreenDataDesc) mDataDesc.getParent();
        if (screenDataDesc.getScreenNaviPanel() != null) {
            mNaviPanelHeight = (int) screenDataDesc.getScreenNaviPanel().getCalcDesc().getHeight();
        } else {
            mNaviPanelHeight = 0;
        }
    }

   /* @Override
    protected void onDetachedFromWindow() {
        //removeAllViews();
        super.onDetachedFromWindow();
    }*/

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
                    float deltaX = scrollManager.getMotionEventDeltaX();
                    float deltaY = scrollManager.getMotionEventDeltaY();

                    switch (getEventDirectionForScrollType()) {
                        case VERTICAL:
                            deltaX = 0.0f;
                            break;
                        case HORIZONTAL:
                            deltaY = 0.0f;
                            break;
                        default:
                            break;
                    }

                    result = scrollManager.anyChildCanScroll(this, deltaX, deltaY) && (mDataDesc.getScreenDesc().isPullToRefresh() || scrollManager.canIScrollBy(this, deltaX, deltaY));
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
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result = super.onTouchEvent(ev);
        if (!mDataDesc.getScreenDesc().isPullToRefresh())
            return result;

        int actionType = ev.getAction();
        switch (actionType) {
            case MotionEvent.ACTION_DOWN:
                stopFling();
                mStartY = -1;
                pullToRefreshAnimator.setPullToRefreshAsActiveText();
                mPulled = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                ScrollManager.getInstance().setUpdate(this, ev);
                if (getScrollY() == 0) {
                    if (mStartY < 0) {
                        mStartY = ev.getY();
                    }
                    mTranslation = ev.getY() - mStartY;
                    if (mTranslation >= mMaxTranslation) {
                        mStartY = ev.getY() - mMaxTranslation;
                        mTranslation = mMaxTranslation;
                    }

                    if (mTranslation > 0 && ScrollManager.getInstance().anyChildCanScroll(this, 0, mTranslation)) {
                        mPulling = true;
                        float translationPercent = mTranslation / mMaxTranslation;
                        pullToRefreshAnimator.updateActiveText(translationPercent);
                        animation = new PullToRefreshAlphaAnimation(mLastTranslation, mTranslation, (float) (mMaxTranslation * 0.8));
                        animation.setDuration(ANIMATION_BASE_TIME);
                        animation.setFillAfter(true);
                        startAnimation(animation);
                        mLastTranslation = mTranslation;
                        return false;
                    } else if (mTranslation == 0) {
                        return false;
                    } else {
                        mStartY = -1;
                        mTranslation = 0;
                        mLastTranslation = 0;
                        clearAnimation();
                        return result;
                    }
                } else {
                    mStartY = -1;
                    pullToRefreshAnimator.setPullToRefreshAsActiveText();
                    mTranslation = 0;
                    mLastTranslation = 0;
                    mPulling = false;
                    clearAnimation();
                    return result;
                }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (animation instanceof PullToRefreshAlphaAnimation && mTranslation >= 0 && getScrollY() == 0) {
                    float translationPercent = mTranslation / mMaxTranslation;
                    mPulled = pullToRefreshAnimator.isPulledFarEnought(translationPercent);
                    mLastTranslation = 0;
                    setRevertedAnimation();
                    clearAnimation();
                    startAnimation(animation);
                }
                mStartY = -1;
                pullToRefreshAnimator.setPullToRefreshAsActiveText();
                mTranslation = 0;
                break;
            default:
                break;
        }

        return result;
    }

    protected void stopFling() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }

    private void setRevertedAnimation() {
        animation = new RevertedPointerAnimation(mTranslation);

        pullToRefreshAnimator.setPullToRefreshAsActiveText();
        //we calculate animation duration based on current translation
        int duration = (int) ((mTranslation / mMaxTranslation) * ANIMATION_STANDARD_TIME);
        //Czasami animacje z czasem trwania 1ms nie dzialaja jak nalezy. Jest to zabezpieczenie.

        animation.setDuration(duration > 1 ? duration : 1);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPulling = false;
                if (mPulled && getScrollY() == 0) {

                    mStartY = 0;
                    KinetiseActivity activity = (KinetiseActivity) AGApplicationState.getInstance().getActivity();
                    activity.closeKeyboard();
                    FeedManager.refreshAllFeeds();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public String getTag() {
        return ((AGBodyDataDesc) getDescriptor()).getScreenDesc().getScreenId() + super.getTag();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        String activePullToRefreshText = pullToRefreshAnimator.getActiveText();
        float activeTextWidth = pullToRefreshAnimator.getActiveTextWidth();
        float activeTextHeight = pullToRefreshAnimator.getActiveTextHeight();
        if (mPulling) {
            canvas.clipRect(0, -mMaxTranslation, mWidth, mHeight, Region.Op.REPLACE);
            canvas.drawText(activePullToRefreshText, (mWidth - activeTextWidth) / 2, mHeaderHeight - (mMaxTranslation - activeTextHeight) / 2, pullToRefreshAnimator.getPullToRefreshPaint());
            canvas.drawText(activePullToRefreshText, (mWidth - activeTextWidth) / 2, mHeaderHeight - (mMaxTranslation - activeTextHeight) / 2, pullToRefreshAnimator.getPullToRefreshPaint());
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean bool, int l, int t, int r, int b) {
        AGSectionCalcDesc calcDesc = mDataDesc.getCalcDesc();
        mMaxChildBottomPosition = 0;
        mMaxChildRightPosition = 0;

        mHeight = (int) Math.round(calcDesc.getHeight());
        mWidth = (int) Math.round(calcDesc.getWidth());

        final int count = getChildCount();
        AGBodyDataDesc desc = (AGBodyDataDesc) getDescriptor();
        List<AbstractAGElementDataDesc> childDataDesc = desc.getAllControls();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            final AGViewCalcDesc childCalcDesc = ((AbstractAGViewDataDesc) childDataDesc.get(i)).getCalcDesc();
            double leftDouble = childCalcDesc.getPositionX() + childCalcDesc.getMarginLeft();
            double topDouble = childCalcDesc.getPositionY() + childCalcDesc.getMarginTop();

            int childWidth = (int) (Math.round(childCalcDesc.getWidth() + Math.round(childCalcDesc.getBorder().getHorizontalBorderWidth()) + leftDouble) - Math.round(leftDouble));
            int childHeight = (int) (Math.round(childCalcDesc.getHeight() + Math.round(childCalcDesc.getBorder().getVerticalBorderHeight()) + topDouble) - Math.round(topDouble));

            int left = (int) Math.round(leftDouble);
            int top = (int) Math.round(topDouble);
            int right = left + childWidth;
            int bottom = top + childHeight;
            child.layout(left, top, right, bottom);

            if (bottom > mMaxChildBottomPosition)
                mMaxChildBottomPosition = (int) Math.round(bottom + childCalcDesc.getMarginBottom());

            if (right > mMaxChildRightPosition)
                mMaxChildRightPosition = (int) Math.round(right + childCalcDesc.getMarginRight());

        }

        mMaxChildBottomPosition += mNaviPanelHeight;


        super.onLayout(bool, l, t, r, b);
        restoreScroll();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        pullToRefreshAnimator.setTextSize(Math.min(w, h) * 0.05f);
        setNavipanelHeight();
        setHeaderHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return null;
    }

    @Override
    public void onClick(View view) {
        // nothing to do
    }

    @Override
    public void setBackgroundBitmap(Bitmap bitmap) {

    }

    private class PullToRefreshAlphaAnimation extends TranslateAnimation {
        private float mStartAlpha;
        private float mAlphaDelta;

        public PullToRefreshAlphaAnimation(float prevToYDelta, float toYDelta, float maxTranslation) {
            super(0, 0, toYDelta, toYDelta);

            if (maxTranslation < prevToYDelta) {
                mStartAlpha = 255;
                mAlphaDelta = 0;
            } else {
                float delta = prevToYDelta - toYDelta;
                mStartAlpha = prevToYDelta / maxTranslation * 255;
                mAlphaDelta = delta / maxTranslation * 255;
            }

        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            pullToRefreshAnimator.setPaintAlpha((int) (mStartAlpha + mAlphaDelta * interpolatedTime));
        }
    }

    private class RevertedPointerAnimation extends TranslateAnimation {

        public RevertedPointerAnimation(float fromYDelta) {
            super(0, 0, Math.round(fromYDelta), Math.round(0), Animation.ZORDER_NORMAL, Math.round(fromYDelta), Animation.START_ON_FIRST_FRAME, 0);
        }
    }
}
