package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewParent;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGTextDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.IImageDescriptor;
import com.kinetise.data.sourcemanager.ImageSource;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageChangeListener;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.drawing.BackgroundSetterCommandCallback;
import com.kinetise.helpers.drawing.ViewDrawer;
import com.kinetise.helpers.http.RedirectMap;

import java.security.InvalidParameterException;

public class AGImageView<T extends AGTextDataDesc & IImageDescriptor> extends AGControl<T> implements IAGView, View.OnClickListener, BackgroundSetterCommandCallback {
    protected ImageSource imageSource;
    private final BasicImageView mImageView;
    private AGLoadingView mLoadingView;

    public AGImageView(SystemDisplay display, T desc) {
        super(display, desc);
        ImageSource.LoadingStartedListener loadingStartedListener = new ImageSource.LoadingStartedListener() {
            @Override
            public void loadingStarted() {
                onLoadingStarted();
            }
        };
        ImageChangeListener imageSetCallback = new ImageChangeListener() {
            @Override
            public void onImageChanged(Bitmap bitmap) {
                mImageView.setImageBitmap(bitmap);
            }
        };
        imageSource = new ImageSource(desc.getImageDescriptor(), imageSetCallback, loadingStartedListener);
        mDescriptor = desc;
        mImageView = new BasicImageView(mDisplay.getActivity());
        mImageView.setSizeMode(mDescriptor.getImageDescriptor().getSizeMode());
        addView(mImageView);

        setOnClickListener(this);
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        super.setDescriptor(descriptor);
        IImageDescriptor imageDescriptor = (IImageDescriptor) descriptor;
        imageSource.setImageDescriptor(imageDescriptor.getImageDescriptor());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mLoadingView != null) {
            mLoadingView.measure(widthMeasureSpec, heightMeasureSpec);
        }
        if (mImageView != null) {
            mImageView.measure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //Sprawdzamy czy Loading jest dodany do widoku.
        if (mLoadingView != null) {
            int width = right - left;
            int height = bottom - top;

            int childLeft = (width - mLoadingView.getMeasuredWidth()) / 2;
            int childTop = (height - mLoadingView.getMeasuredHeight()) / 2;
            int childRight = childLeft + mLoadingView.getMeasuredWidth();
            int childBottom = childTop + mLoadingView.getMeasuredHeight();
            mLoadingView.layout(childLeft, childTop, childRight, childBottom);
        }
        mImageView.layout(0, 0, right - left, bottom - top);
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
    public boolean accept(IViewVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        String baseUrl = mDescriptor.getFeedBaseAdress();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        int width = (int) Math.round(calcDesc.getWidth());
        int height = (int) Math.round(calcDesc.getHeight());
        mBackgroundSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
        imageSource.refresh(baseUrl, width, height);
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null)
            removeLoadingImage();
        mImageView.setImageBitmap(bitmap);
    }

    private void removeLoadingImage() {
        // Only call "removeView" when view is in hierarchy because it will cause unnecessary layout pass
        if (mLoadingView != null) {
            mLoadingView.clearAnimation();
            removeView(mLoadingView);
            mLoadingView = null;
        }
    }

    private AGViewCalcDesc getCalcDesc() {
        return mDescriptor.getCalcDesc();
    }

    public void onLoadingStarted() {
        setImageBitmap(null);
        String imageSource = mDescriptor.getImageDescriptor().getImageSource();
        if (!RedirectMap.getInstance().isCached(imageSource)) { //RedirectMap? vs BitmapCache
            if (mLoadingView == null) {
                mLoadingView = new AGLoadingView(mDisplay.getActivity(), (int) getCalcDesc().getContentWidth(), (int) getCalcDesc().getContentHeight());
                addView(mLoadingView);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        imageSource.cancelDownload();
        removeLoadingImage();
    }

}