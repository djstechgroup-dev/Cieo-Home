package com.kinetise.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;

public class InterceptFrameLayout extends FrameLayout {
    private static final float CURTAIN_ALPHA = 0.75f;
    private Animation mCanceledAnimation;
    private Runnable mOnTouchAction = null;

    public InterceptFrameLayout(Context context) {
        super(context);
        setAlpha(CURTAIN_ALPHA);
    }

    public InterceptFrameLayout(Context context, Runnable onTouchAction){
        super(context);
        mOnTouchAction = onTouchAction;
        setAlpha(CURTAIN_ALPHA);
    }

    public InterceptFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAlpha(CURTAIN_ALPHA);
    }

    public InterceptFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAlpha(CURTAIN_ALPHA);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP && mOnTouchAction != null){
            mOnTouchAction.run();
        }
        return true;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        if(visibility == INVISIBLE){
           mCanceledAnimation = getAnimation();
            clearAnimation();
        }
        if(visibility == VISIBLE){
            if(mCanceledAnimation !=null){
                startAnimation(mCanceledAnimation);
            }
        }
        super.onVisibilityChanged(changedView, visibility);
    }
}
