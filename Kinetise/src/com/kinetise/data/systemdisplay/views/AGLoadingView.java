package com.kinetise.data.systemdisplay.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGLoadingDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.RWrapper;
import com.kinetise.helpers.calcmanagerhelper.CalcManagerHelper;
import com.kinetise.helpers.drawing.ViewDrawer;
import com.kinetise.support.logger.Logger;

import java.security.InvalidParameterException;

public class AGLoadingView extends ImageView implements IAGView {

    static int[] loadingSizesDown = new int[]{200,150,100,60,40,30,20};
    private final int LOADING_MAX_SIZE = (int)Math.round(getPXfromKPX(150));
    AGLoadingDataDesc mDescriptor;
    private int mWidth = LOADING_MAX_SIZE;
    private int mHeight = LOADING_MAX_SIZE;
    private SystemDisplay mSystemDisplay;
    private ViewGroup.LayoutParams mParams;
    public static int isAgLoadingViewVisible = 0;
    public AGLoadingView(SystemDisplay systemDisplay, AGLoadingDataDesc dataDesc) {
        super(systemDisplay.getActivity());
        mDescriptor = dataDesc;
        mSystemDisplay = systemDisplay;
        init();
    }

    public AGLoadingView(Context context, AttributeSet options) {
        super(context, options);
        init();
    }

    public AGLoadingView(Context context, int width, int height) {
        super(context);
        mWidth = width;
        mHeight = height;
        init();
    }

    public AGLoadingView(Context context) {
        super(context);
        init();
    }

    public static int getLoadingPlaceholderSize(int width, int height){
        int maxLoadingSize = (int) Math.round(CalcManagerHelper.KPXtoPixels(150));

        int sufix = Math.min(width,height);
        int matchedSufix = loadingSizesDown[loadingSizesDown.length-1];
        for(int size:loadingSizesDown) {
            if (sufix > size && size < maxLoadingSize) {
                matchedSufix = size;
                break;
            }
        }
        return matchedSufix;
    }

    public static int getLoadingAnimationResourceId(int width, int height){
        int matchingSize = getLoadingPlaceholderSize(width,height);
        switch (matchingSize){

            case 30:
                return RWrapper.drawable.animation30;
            case 40:
                return RWrapper.drawable.animation40;
            case 60:
                return RWrapper.drawable.animation60;
            case 100:
                return RWrapper.drawable.animation100;
            case 150:
                return RWrapper.drawable.animation150;
            case 200:
                return RWrapper.drawable.animation200;
            default:
            case 20:
                return RWrapper.drawable.animation20;
        }
    }

    @Override
    public AbstractAGElementDataDesc getDescriptor() {
        return mDescriptor;
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        mDescriptor = (AGLoadingDataDesc) descriptor;
    }

    private double getPXfromKPX(int kpxValue) {
        double providedWidth = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);

        double px = ((kpxValue * providedWidth) / CalcManagerHelper.UNITSCALE);
        return (px == 0 && kpxValue > 0) ? 1 : px;
    }

    @Override
    protected void onAttachedToWindow(){
        isAgLoadingViewVisible++;
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        isAgLoadingViewVisible--;
        clearAnimation();
        super.onDetachedFromWindow();
    }

    @Override
    public ViewGroup.LayoutParams getLayoutParams() {
        return mParams;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        mParams = params;
        super.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mDescriptor != null) {
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        } else {
            int measuredWidth;
            int measuredHeight;
            int biggerSize = Math.max(mWidth, mHeight);
            measuredHeight = measuredWidth = Math.min(biggerSize, LOADING_MAX_SIZE);
            setMeasuredDimension(measuredWidth, measuredHeight);
        }

    }

    private void init(){
        mParams = new FrameLayout.LayoutParams(mWidth,mHeight, Gravity.CENTER);
        setImageResource(AGLoadingView.getLoadingAnimationResourceId(mWidth, mHeight));
    }

    private void start() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                ((AnimationDrawable) getDrawable()).start();
                Logger.d(this,"start","Animation is being started.");
            }
        }
        ,300);

    }

    public void stop(){
        if(getDrawable() instanceof AnimationDrawable){
            ((AnimationDrawable)getDrawable()).stop();
        }
    }

    @Override
    public String toString() {
        String base = super.toString();
        if (mDescriptor != null) {
            base += mDescriptor.toString();
        }
        return base;
    }

    @Override
    public SystemDisplay getSystemDisplay() {
        return mSystemDisplay;
    }

    @Override
    public IAGView getAGViewParent() {
        ViewParent parent = getParent();
        if (!(parent instanceof IAGView)) {
            throw new InvalidParameterException("Parent of IAGView object have to implement IAGView interface");
        }

        return (IAGView) parent;
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return null;
    }

    @Override
    public void loadAssets() {}

    @Override
    public boolean accept(IViewVisitor visitor) {
        return false;
    }

    @Override
    public void onClick(View v) {}
}
