package com.kinetise.data.application.overalymanager;

import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import com.kinetise.data.application.feedmanager.FeedManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGElementCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.OverlayDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.views.IAGView;
import com.kinetise.views.InterceptFrameLayout;

public class OverlayAnimator {
    private final static int ANIMATION_DURATION = 200;
    public static final float SEMI_TRANSPARENT = 0.75f;

    private View mOverlayView;
    private View mInterceptView;
    private OverlayDataDesc mOverlayDataDesc;
    private SystemDisplay mDisplay;

    public OverlayAnimator(View overlayView, InterceptFrameLayout interceptView, OverlayDataDesc overlayDataDesc) {
        mOverlayView = overlayView;
        mInterceptView = interceptView;
        mOverlayDataDesc = overlayDataDesc;
    }

    public void updateViewsPositions(final SystemDisplay systemDisplay, final ViewGroup mainDisplayView) {
        AbstractAGElementDataDesc overlayViewDesc = ((IAGView)mOverlayView).getDescriptor();
        systemDisplay.runCalcManager(overlayViewDesc);


        AGElementCalcDesc calcDesc = overlayViewDesc.getCalcDesc();
        int width = (int) calcDesc.getWidth();
        int height = (int) calcDesc.getHeight();
        mOverlayView.measure(makeMeasureSpec(width), makeMeasureSpec(height));
        mOverlayView.requestLayout();

        if(mOverlayDataDesc.isMoveScreen()) {
            Point mainDisplayPosition = getFinalMainDisplayPosition();
            mainDisplayView.setX(mainDisplayPosition.x);
            mainDisplayView.setY(mainDisplayPosition.y);
        }

        setOverlayPositionForStaticOverlay(systemDisplay);
    }

    public int makeMeasureSpec(int width) {
        return View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
    }


    public void addAndAnimateViews(final SystemDisplay systemDisplay, final ViewGroup rootView, final ViewGroup mainDisplayView) {
        mDisplay = systemDisplay;

        addInterceptViewAndSetAnimation(mainDisplayView);


        boolean isMoveScreen = mOverlayDataDesc.isMoveScreen();
        boolean isMoveOverlay = mOverlayDataDesc.isMoveOverlay();
        if (!isMoveOverlay && !isMoveScreen) {
            setOverlayPositionForStaticOverlay(systemDisplay);
            addOverlayViewToRootView(rootView);
        } else if (isMoveOverlay && !isMoveScreen) {
            setOverlayPositionForMovingOverlay(systemDisplay);
            addOverlayViewToRootView(rootView);
            setUpOverlayShowAnimation(systemDisplay);
        } else if (!isMoveOverlay && isMoveScreen) {
            setOverlayPositionForStaticOverlay(systemDisplay);
            addOverlayViewToRootView(rootView, 0);
            setUpMainDisplayShowAnimation(systemDisplay, mainDisplayView);
        } else if (isMoveOverlay && isMoveScreen) {
            setOverlayPositionForMovingOverlay(systemDisplay);
            addOverlayViewToRootView(rootView);
            setUpOverlayShowAnimation(systemDisplay);
            setUpMainDisplayShowAnimation(systemDisplay, mainDisplayView);
        }
    }

    protected void addInterceptViewAndSetAnimation(final ViewGroup mainDisplayView) {
         mDisplay.getActivity().runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 mainDisplayView.addView(mInterceptView);
                 mInterceptView.animate().setDuration(ANIMATION_DURATION).alpha(SEMI_TRANSPARENT);
             }
         });
    }

    protected void addOverlayViewToRootView(final ViewGroup rootView, final int index) {
        mDisplay.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rootView.addView(mOverlayView, index);
            }
        });
    }

    protected void addOverlayViewToRootView(final ViewGroup rootView) {

        mDisplay.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rootView.addView(mOverlayView);
            }
        });

    }

    protected void setUpOverlayShowAnimation(SystemDisplay systemDisplay) {
        final Point finalOverlayPosition = getFinalOverlayPosition(systemDisplay);

        animateOverlayViewOnUiThread(systemDisplay, finalOverlayPosition);
    }

    protected void setUpMainDisplayShowAnimation(SystemDisplay systemDisplay, final ViewGroup mainDisplayView) {
        final Point mainViewFinalPosition = getFinalMainDisplayPosition();
        animateViewOnUiThread(systemDisplay, mainDisplayView, mainViewFinalPosition);
    }

    protected void animateOverlayViewOnUiThread(SystemDisplay systemDisplay, final Point translation) {
        systemDisplay.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mOverlayView.animate().cancel();
                mOverlayView.animate().setDuration(ANIMATION_DURATION).translationX(translation.x).translationY(translation.y);
            }
        });
    }

    protected void animateViewOnUiThread(SystemDisplay systemDisplay, final View view, final Point translation) {
        systemDisplay.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.animate().cancel();
                view.animate().setDuration(ANIMATION_DURATION).translationX(translation.x).translationY(translation.y);
            }
        });
    }

    private void setOverlayPositionForMovingOverlay(SystemDisplay systemDisplay) {
        Point startOverlayPosition = getStartingOverlayPosition(systemDisplay);
        mOverlayView.setX(startOverlayPosition.x);
        mOverlayView.setY(startOverlayPosition.y);
    }

    private void setOverlayPositionForStaticOverlay(SystemDisplay systemDisplay) {
        Point overlayPosition = getFinalOverlayPosition(systemDisplay);
        mOverlayView.setX(overlayPosition.x);
        mOverlayView.setY(overlayPosition.y);
    }

    private Point getStartingOverlayPosition(SystemDisplay systemDisplay) {
        Point result = new Point();
        int width = (int) mOverlayDataDesc.getMainViewDesc().getCalcDesc().getWidth();
        int height = (int) mOverlayDataDesc.getMainViewDesc().getCalcDesc().getHeight();
        switch (mOverlayDataDesc.getAnimationType()) {
            case LEFT:
                result.set(-width, 0);
                break;
            case RIGHT:
                result.set(systemDisplay.getWidth(), 0);
                break;
            case TOP:
                result.set(0, -height);
                break;
            case BOTTOM:
                result.set(0, systemDisplay.getHeight());
                break;
        }
        return result;
    }

    private Point getFinalOverlayPosition(SystemDisplay systemDisplay) {
        Point result = new Point();
        int width = (int) mOverlayDataDesc.getMainViewDesc().getCalcDesc().getWidth();
        int height = (int) mOverlayDataDesc.getMainViewDesc().getCalcDesc().getHeight();
        switch (mOverlayDataDesc.getAnimationType()) {
            case RIGHT:
                result.set(systemDisplay.getWidth() - width, 0);
                break;
            case BOTTOM:
                result.set(0, systemDisplay.getHeight() - height);
                break;
            default:
                result.set(0, 0);
        }
        return result;
    }

    private Point getFinalMainDisplayPosition() {
        Point result = new Point();
        switch (mOverlayDataDesc.getAnimationType()) {
            case LEFT:
                result.set((int) mOverlayDataDesc.getMainViewDesc().getCalcDesc().getWidth(), 0);
                break;
            case RIGHT:
                result.set(- (int) mOverlayDataDesc.getMainViewDesc().getCalcDesc().getWidth(), 0);
                break;
            case TOP:
                result.set(0, (int) mOverlayDataDesc.getMainViewDesc().getCalcDesc().getHeight());
                break;
            case BOTTOM:
                result.set(0, -(int) mOverlayDataDesc.getMainViewDesc().getCalcDesc().getHeight());
                break;
        }
        return result;
    }

    public void animateAndRemoveViews(SystemDisplay systemDisplay, ViewGroup mainDisplayView) {
        boolean isMoveScreen = mOverlayDataDesc.isMoveScreen();
        boolean isMoveOverlay = mOverlayDataDesc.isMoveOverlay();

        mInterceptView.animate().setDuration(ANIMATION_DURATION).alpha(0f);
        if (!isMoveOverlay && !isMoveScreen) {
            removeViewFromParent(mInterceptView);
            removeViewFromParent(mOverlayView);
            mOverlayView = null;
            mInterceptView = null;
        } else if (isMoveOverlay && !isMoveScreen) {
            setUpOverlayHideAnimation(systemDisplay).setListener(new OverlayAnimationListener(mInterceptView, mOverlayView));
        } else if (!isMoveOverlay && isMoveScreen) {
            setUpMainViewHideAnimation(mainDisplayView).setListener(new OverlayAnimationListener(mInterceptView, mOverlayView));
        } else if (isMoveOverlay && isMoveScreen) {
            setUpOverlayHideAnimation(systemDisplay);

            setUpMainViewHideAnimation(mainDisplayView);
        }

        mOverlayView = null;
        mInterceptView = null;
        mOverlayDataDesc = null;
    }


    public View getOverlayView()
    {
        return mOverlayView;
    }

    private ViewPropertyAnimator setUpOverlayHideAnimation(SystemDisplay systemDisplay) {
        Point startingOverlayPosition = getStartingOverlayPosition(systemDisplay);
        mOverlayView.animate().cancel();
        return mOverlayView.animate().setDuration(ANIMATION_DURATION).x(startingOverlayPosition.x).y(startingOverlayPosition.y);
    }

    private ViewPropertyAnimator setUpMainViewHideAnimation(ViewGroup mainDisplayView) {
        mainDisplayView.animate().cancel();
        return mainDisplayView.animate().setDuration(ANIMATION_DURATION).x(0).y(0).setListener(new OverlayAnimationListener(mInterceptView, mOverlayView));
    }

    private void removeViewFromParent(View view) {
        ((ViewGroup) view.getParent()).removeView(view);
    }
}
