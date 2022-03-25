package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.view.ViewParent;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGCompoundButtonCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.TextCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGCompoundButtonDataDesc;
import com.kinetise.data.descriptors.types.Quad;
import com.kinetise.data.sourcemanager.ImageSource;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageChangeListener;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.helpers.drawing.BackgroundSetterCommandCallback;
import com.kinetise.helpers.drawing.ViewDrawer;
import com.kinetise.helpers.http.RedirectMap;

import java.security.InvalidParameterException;

public abstract class AbstractAGCompoundView<T extends AbstractAGCompoundButtonDataDesc> extends AGControl<T> implements IAGView, OnStateChangedListener, BackgroundSetterCommandCallback {
    protected final BasicTextView mTextView;
    protected final BasicImageView mImageView;
    protected ImageSource mCheckedImageSource;
    protected ImageSource mUncheckedImageSource;
    AGLoadingView mLoadingView;

    public AbstractAGCompoundView(SystemDisplay display, T desc) {
        super(display, desc);


        ImageSource.LoadingStartedListener checkedLoadingStartedListener = new ImageSource.LoadingStartedListener() {
            @Override
            public void loadingStarted() {
                onCheckedLoadingStarted();
            }
        };
        ImageChangeListener checkedImageSetCallback = new ImageChangeListener() {
            @Override
            public void onImageChanged(Bitmap bitmap) {
                if (mDescriptor.isChecked()) {
                    /*if(mCheckedLoadingView!=null)
                        removeView(mCheckedLoadingView);*///TODO
                    mImageView.setImageBitmap(bitmap);
                }
            }
        };
        ImageSource.LoadingStartedListener uncheckedLoadingStartedListener = new ImageSource.LoadingStartedListener() {
            @Override
            public void loadingStarted() {
                onUncheckedLoadingStarted();
            }
        };
        ImageChangeListener uncheckedImageSetCallback = new ImageChangeListener() {
            @Override
            public void onImageChanged(Bitmap bitmap) {
                if (!mDescriptor.isChecked()) {
                /*if(mCheckedLoadingView!=null)
                    removeView(mCheckedLoadingView);*///TODO
                    mImageView.setImageBitmap(bitmap);
                }
            }
        };

        mCheckedImageSource = new ImageSource(mDescriptor.getActiveImageDescriptor(), checkedImageSetCallback, checkedLoadingStartedListener);
        mUncheckedImageSource = new ImageSource(mDescriptor.getImageDescriptor(), uncheckedImageSetCallback, uncheckedLoadingStartedListener);

        mImageView = new BasicImageView(display.getActivity());
        mImageView.setSizeMode(mDescriptor.getImageDescriptor().getSizeMode());
        mTextView = new BasicTextView(display.getActivity());
        mTextView.setTextDescriptor(mDescriptor.getTextDescriptor());
        addView(mImageView);
        addView(mTextView);

        setOnClickListener(this);
        mDescriptor.setStateChangeListener(this);
    }

    public void onCheckedLoadingStarted() {
        if (mDescriptor.isChecked()) {
            mImageView.setImageBitmap(null);
            if (mDescriptor.getShowLoading()) {
                String imageSource = mDescriptor.getActiveImageDescriptor().getImageSource();
                if (!RedirectMap.getInstance().isCached(imageSource)) { //RedirectMap? vs BitmapCache
                    AGViewCalcDesc calcDesc = getDescriptor().getCalcDesc();
                    if (mLoadingView != null) {
                        removeView(mLoadingView);
                    }
                    mLoadingView = new AGLoadingView(mDisplay.getActivity(), (int) calcDesc.getContentWidth(), (int) calcDesc.getContentHeight());
                    addView(mLoadingView);
                }
            }
        }
    }

    public void onUncheckedLoadingStarted() {
        if (!mDescriptor.isChecked()) {
            mImageView.setImageBitmap(null);
            if (mDescriptor.getShowLoading()) {
                String imageSource = mDescriptor.getActiveImageDescriptor().getImageSource();
                if (!RedirectMap.getInstance().isCached(imageSource)) { //RedirectMap? vs BitmapCache
                    AGViewCalcDesc calcDesc = getDescriptor().getCalcDesc();
                    if (mLoadingView != null) {
                        removeView(mLoadingView);
                    }
                    mLoadingView = new AGLoadingView(mDisplay.getActivity(), (int) calcDesc.getContentWidth(), (int) calcDesc.getContentHeight());
                    addView(mLoadingView);
                }
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDescriptor.setStateChangeListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDescriptor.removeStateChangeListener(this);
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
    public void loadAssets() {
        super.loadAssets();
        String baseUrl = mDescriptor.getFeedBaseAdress();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        mBackgroundSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
        mCheckedImageSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
        mUncheckedImageSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
    }

    private int calculateCheckVPosition(int bottom, int top) {
        Quad boardedSize = mDescriptor.getCalcDesc().getBorder();
        int paddingTop = (int) mDescriptor.getCalcDesc().getPaddingTop();
        int paddingBottom = (int) mDescriptor.getCalcDesc().getPaddingBottom();
        int additionalFreeSpace = (int) Math.round(bottom - top - mDescriptor.getCalcDesc().getCheckedHeight());
        int middleOfTheDrawingSurface = (bottom - top - paddingTop - paddingBottom) / 2 + paddingTop;
        switch (mDescriptor.getCheckVAlign()) {
            case TOP:
                return (int) boardedSize.getTop() + paddingTop;
            case BOTTOM:
                return additionalFreeSpace - (int) boardedSize.getBottom() - paddingBottom;
            case CENTER:
            default:
                return middleOfTheDrawingSurface - (int) mDescriptor.getCalcDesc().getCheckedHeight() / 2;
        }
    }

    private int calculateTextVPosition(int bottom, int top) {
        Quad boardedSize = mDescriptor.getCalcDesc().getBorder();
        int paddingTop = (int) mDescriptor.getCalcDesc().getPaddingTop();
        int paddingBottom = (int) mDescriptor.getCalcDesc().getPaddingBottom();
        TextCalcDesc textCalcDesc = mDescriptor.getTextDescriptor().getCalcDescriptor();
        int additionalFreeSpace = (int) Math.round(bottom - top - textCalcDesc.getTextHeight());
        int middleOfTheDrawingSurface = (bottom - top - paddingTop - paddingBottom) / 2 + paddingTop;
        switch (mDescriptor.getTextDescriptor().getTextVAlign()) {
            case TOP:
                return (int) boardedSize.getTop() + paddingTop;
            case BOTTOM:
                return additionalFreeSpace - (int) boardedSize.getBottom() - paddingBottom;
            case CENTER:
            default:
                return middleOfTheDrawingSurface - (int) mDescriptor.getCalcDesc().getCheckedHeight() / 2;
        }
    }

    @Override
    protected void onLayout(boolean bool, int l, int t, int r, int b) {
        super.onLayout(bool, l, t, r, b);

        int spaceLeft = (int) Math.round(mDescriptor.getCalcDesc().getBorder().getLeft() + mDescriptor.getCalcDesc().getPaddingLeft());

        int imageVPosition = calculateCheckVPosition(b, t);
        int textVPosition = calculateTextVPosition(b, t);

        double imageWidth = mDescriptor.getCalcDesc().getCheckedWidth();
        int textLeft = (int) Math.round(spaceLeft + imageWidth + mDescriptor.getCalcDesc().getInnerSpace());
        mImageView.layout(spaceLeft, imageVPosition,
                (int) Math.round(spaceLeft + imageWidth), (int) Math.round(imageVPosition + mDescriptor.getCalcDesc().getCheckedHeight()));

        mTextView.layout(textLeft, textVPosition, Math.round(textLeft + mTextView.getMeasuredWidth()), Math.round(textVPosition + mTextView.getMeasuredHeight()));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        AGCompoundButtonCalcDesc calcDesc = getDescriptor().getCalcDesc();
        int specWidth = MeasureSpec.makeMeasureSpec(calcDesc.getViewWidth(), MeasureSpec.EXACTLY);
        int specHeight = MeasureSpec.makeMeasureSpec(calcDesc.getViewHeight(), MeasureSpec.EXACTLY);
        setMeasuredDimension(specWidth, specHeight);

        int imageWidthSpec = MeasureSpec.makeMeasureSpec((int) Math.round(calcDesc.getCheckedWidth()), MeasureSpec.EXACTLY);
        int imageHeightSpec = MeasureSpec.makeMeasureSpec((int) Math.round(calcDesc.getCheckedWidth()), MeasureSpec.EXACTLY);
        mImageView.measure(imageWidthSpec, imageHeightSpec);

        TextCalcDesc textCalcDesc = getDescriptor().getTextDescriptor().getCalcDescriptor();
        int textWidthSpec = MeasureSpec.makeMeasureSpec((int) Math.round(textCalcDesc.getTextWidth()), MeasureSpec.EXACTLY);
        int textHeightSpec = MeasureSpec.makeMeasureSpec((int) Math.round(calcDesc.getContentHeight()), MeasureSpec.EXACTLY);
        mTextView.measure(textWidthSpec, textHeightSpec);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        if (mImageView != null)
            mImageView.requestLayout();
        if (mTextView != null)
            mTextView.requestLayout();
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        mDescriptor.setStateChangeListener(null);
        super.setDescriptor(descriptor);
        mTextView.setTextDescriptor(mDescriptor.getTextDescriptor());
        mDescriptor.setStateChangeListener(this);
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return mDrawer;
    }
}
