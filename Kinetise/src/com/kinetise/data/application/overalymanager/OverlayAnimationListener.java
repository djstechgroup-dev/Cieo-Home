package com.kinetise.data.application.overalymanager;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;

import com.kinetise.components.activity.KinetiseActivity;

public class OverlayAnimationListener implements Animator.AnimatorListener {
    private View mInterceptView;
    private View mOverlayView;

    public OverlayAnimationListener(View interceptView, View overlayView) {
        mInterceptView = interceptView;
        mOverlayView = overlayView;
    }


    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        removeViewFromParent(mInterceptView);
        removeViewFromParent(mOverlayView);

        //AnimationListener moze byc czasami trzymany przez propertyAnimator
        //W zwiazku z tym sa tez trzymane widoki overlaya, co drastycznie zwieksza uzycie
        //pamieci. Trzymane sa wtedy 2 widoki tego samego overlaya.
        mInterceptView = null;
        mOverlayView = null;
    }

    public void removeViewFromParent(View view) {
        try {
            ViewGroup viewGroup = ((ViewGroup) view.getParent());
            View lastScreenView = viewGroup.getChildAt(KinetiseActivity.mLastScreenIndex);
            viewGroup.removeView(view);
            if (lastScreenView != null)
                KinetiseActivity.mLastScreenIndex = viewGroup.indexOfChild(lastScreenView);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        //AnimationListener moze byc czasami trzymany przez propertyAnimator
        //W zwiazku z tym sa tez trzymane widoki overlaya, co drastycznie zwieksza uzycie
        //pamieci. Trzymane sa wtedy 2 widoki tego samego overlaya.
        mInterceptView = null;
        mOverlayView = null;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
