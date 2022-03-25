package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGPinchImageDataDesc;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.data.sourcemanager.BitmapCache;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageSetterCommandCallback;
import com.kinetise.data.systemdisplay.bitmapsettercommands.BitmapSetterCommand;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.drawing.ViewDrawer;
import com.kinetise.helpers.drawing.BackgroundSetterCommandCallback;
import com.oritz.touch.TouchImageView;

import java.security.InvalidParameterException;

/**
 * Created by Kuba Komorowski on 2014-12-11.
 */
public class AGPinchImageView extends AGControl<AGPinchImageDataDesc> implements IAGView, View.OnClickListener, ImageSetterCommandCallback, BackgroundSetterCommandCallback {

    private AGLoadingView mLoadingView;
    private TouchImageView mImageView;
    private AGViewCalcDesc mCalcDescriptor;
    private Rect mContentRect = new Rect();
    private int mWidth;
    private int mHeight;
    private String mCurrentImageSource;
    private BitmapSetterCommand mBitmapSetterCommand;

    public AGPinchImageView(SystemDisplay systemDisplay, AGPinchImageDataDesc desc){
        super(systemDisplay, desc);
        mDescriptor = desc;
        mCalcDescriptor = mDescriptor.getCalcDesc();

        mImageView = new TouchImageView(AGApplicationState.getInstance().getContext());
        mImageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
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
        return mDrawer;
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        refreshDrawableState();
        refreshBackground();
        refreshSource(mDescriptor);
    }

    protected void refreshBackground() {
        String baseUrl = mDescriptor.getFeedBaseAdress();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        mBackgroundSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
    }

    private void refreshSource(AGPinchImageDataDesc descriptor) {
        String baseUrl = mDescriptor.getFeedBaseAdress();
        String source = descriptor.getImageDescriptor().getImageSource();
        if (source!=null && !source.equals("") && (mCurrentImageSource==null || !source.equals(mCurrentImageSource))) {
            if(mBitmapSetterCommand != null)
                mBitmapSetterCommand.cancel();
            BitmapCache.getInstance().removeFromLruCache(source, 0, 0); //TODO to inaczej powinno działać a teraz po prostu na pałę kasuje z cache'a
            mBitmapSetterCommand = new BitmapSetterCommand(baseUrl,descriptor.getImageDescriptor(), this, 0, 0);
            AssetsManager.getInstance().getAsset(mBitmapSetterCommand, AssetsManager.ResultType.IMAGE, mDescriptor.getImageDescriptor().getHeaders(), mDescriptor.getImageDescriptor().getHttpParams(),null);
            mCurrentImageSource = source;
        }
    }


    @Override
    public boolean accept(IViewVisitor visitor) {
        return visitor.visit(this);
    }



    @Override
    public void loadingStarted() {
        removeAllViews();
        boolean showLoading = true;
        if(mDescriptor != null){
            showLoading = ((AGPinchImageDataDesc)mDescriptor).getShowLoading();
        }

        if(showLoading) {
            mLoadingView = new AGLoadingView(AGApplicationState.getInstance().getContext());
            addView(mLoadingView);
        }
    }

    @Override
    public void setImageSrc(Bitmap b) {
        removeAllViews();
        mLoadingView = null;
        addView(mImageView);
        mImageView.setImageBitmap(b);
    }

    @Override
    public void onClick(View v) {
        //nothing to do
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mImageView.measure(widthMeasureSpec,heightMeasureSpec);
        if (mLoadingView != null) {
            mLoadingView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mCalcDescriptor = mDescriptor.getCalcDesc();
        mWidth = (int) Math.round(mCalcDescriptor.getBlockWidth() - mCalcDescriptor.getMarginRight() - mCalcDescriptor.getMarginLeft());
        mHeight = (int) Math.round(mCalcDescriptor.getBlockHeight() - mCalcDescriptor.getMarginTop() - mCalcDescriptor.getMarginBottom());

        if (mCalcDescriptor != null) {
            double leftDouble, topDouble, rightDouble, bottomDouble;
            leftDouble = mCalcDescriptor.getPaddingLeft() + mCalcDescriptor.getBorder().getLeft();
            topDouble = mCalcDescriptor.getPaddingTop() + mCalcDescriptor.getBorder().getTop();
            rightDouble = mCalcDescriptor.getPaddingRight() + mCalcDescriptor.getBorder().getRight();
            bottomDouble = mCalcDescriptor.getPaddingBottom() + mCalcDescriptor.getBorder().getBottom();

            mContentRect.left = (int) Math.round(leftDouble);
            mContentRect.top = (int) Math.round(topDouble);
            mContentRect.right = (int) Math.round(mWidth - rightDouble);
            mContentRect.bottom = (int) Math.round(mHeight - bottomDouble);

            mImageView.layout(mContentRect.left, mContentRect.top, mContentRect.right, mContentRect.bottom);
        }

        if (mLoadingView != null) {
            int childLeft, childTop, childRight, childBottom;
            int width = right-left;
            int height = bottom-top;

            childLeft = (width-mLoadingView.getMeasuredWidth())/2;
            childTop = (height - mLoadingView.getMeasuredHeight())/2;
            childRight = childLeft + mLoadingView.getMeasuredWidth();
            childBottom = childTop + mLoadingView.getMeasuredHeight();
            mLoadingView.layout(childLeft, childTop, childRight, childBottom);
        }
    }
}
