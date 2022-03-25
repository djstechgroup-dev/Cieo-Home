package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGTextImageDataDesc;
import com.kinetise.data.descriptors.types.TextPosition;
import com.kinetise.data.sourcemanager.ImageSource;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageChangeListener;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.drawing.ViewDrawer;
import com.kinetise.helpers.drawing.BackgroundSetterCommandCallback;
import com.kinetise.helpers.http.RedirectMap;

import java.security.InvalidParameterException;


public class AGTextImageView<T extends AGTextImageDataDesc> extends AGControl<T> implements IAGView, View.OnClickListener, View.OnTouchListener, BackgroundSetterCommandCallback {
    protected BasicImageView mImageView;
    protected BasicTextView mTextView;
    protected ImageSource imageSource;
    AGLoadingView mLoadingView;

    public AGTextImageView(SystemDisplay display, T desc) {
        super(display, desc);

        BasicImageView imageView = createInnerImageView();
        addView(imageView);
        mImageView = imageView;
        ImageSource.LoadingStartedListener loadingStartedListener = new ImageSource.LoadingStartedListener() {
            @Override
            public void loadingStarted() {
                onLoadingStarted();
            }
        };
        ImageChangeListener imageSetCallback = new ImageChangeListener() {
            @Override
            public void onImageChanged(Bitmap bitmap) {
                if (mLoadingView != null)
                    removeView(mLoadingView);
                mImageView.setImageBitmap(bitmap);
            }
        };
        imageSource = new ImageSource(mDescriptor.getImageDescriptor(), imageSetCallback, loadingStartedListener);

        BasicTextView textView = createInnerTextView();

        if (textView != null) {
            addView(textView);
            mTextView = textView;
        } else {
            mImageView.setClickable(true);
            mImageView.setOnTouchListener(this);
        }
    }


    public void loadAssets() {
        super.loadAssets();
        String baseUrl = mDescriptor.getFeedBaseAdress();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        mBackgroundSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
        imageSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    private boolean hasImageView() {
        return mImageView != null;
    }

    private boolean hasTextView() {
        return mTextView != null;
    }

    protected BasicImageView createInnerImageView() {
        BasicImageView view = new BasicImageView(mDisplay.getActivity());
        view.setSizeMode(mDescriptor.getImageDescriptor().getSizeMode());
        view.setSoundEffectsEnabled(false);
        setOnClickListener(this);
        return view;
    }

    private BasicTextView createInnerTextView() {
        if (mDescriptor.getTextDescriptor().getText().getStringValue() != null) {
            BasicTextView view = new BasicTextView(mDisplay.getActivity());
            view.setTextDescriptor(mDescriptor.getTextDescriptor());
            view.setSoundEffectsEnabled(false);
            view.setClickable(true);
            view.setOnTouchListener(this);
            return view;
        } else {
            return null;
        }
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

    public void onLoadingStarted() {
        mImageView.setImageBitmap(null);

        if (mDescriptor.getShowLoading()) {
            String imageSource = mDescriptor.getImageDescriptor().getImageSource();
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

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        AGTextImageDataDesc textImageDataDesc = (AGTextImageDataDesc) descriptor;
        super.setDescriptor(descriptor);
        imageSource.setImageDescriptor(textImageDataDesc.getImageDescriptor());
        mImageView.setSizeMode(textImageDataDesc.getImageDescriptor().getSizeMode());
        if (hasTextView()) {
            mTextView.setTextDescriptor(textImageDataDesc.getTextDescriptor());
        } else {
            BasicTextView textView = createInnerTextView();
            if (textView != null) {
                addView(textView);
                mTextView = textView;
            }
        }
    }

    @Override
    protected void onLayout(boolean bool, int l, int t, int r, int b) {
        super.onLayout(bool, l, t, r, b);
        AGViewCalcDesc textImageCalcDesc = getDescriptor().getCalcDesc();
        int top = (int) Math.round(textImageCalcDesc.getBorder().getTop() + textImageCalcDesc.getPaddingTop());
        int left = (int) Math.round(textImageCalcDesc.getBorder().getLeft() + textImageCalcDesc.getPaddingLeft());
        int imagePosition = 0;
        if (mDescriptor.getTextPosition() == TextPosition.ABOVE) {
            imagePosition = (int) Math.round(mDescriptor.getTextDescriptor().getCalcDescriptor().getTextHeight());
        }

        if (hasImageView()) {
            int imageRight = left + mImageView.getMeasuredWidth();
            int imageTop = top + imagePosition;
            int imageBottom = imageTop + mImageView.getMeasuredHeight();
            mImageView.layout(left, imageTop, imageRight, imageBottom);
        }

        if (mLoadingView != null) {
            int width = r - l;
            int height = b - t;

            int childLeft = (width - mLoadingView.getMeasuredWidth()) / 2;
            int childTop = (height - mLoadingView.getMeasuredHeight()) / 2;
            int childRight = childLeft + mLoadingView.getMeasuredWidth();
            int childBottom = childTop + mLoadingView.getMeasuredHeight();
            mLoadingView.layout(childLeft, childTop, childRight, childBottom);
        }

        if (hasTextView()) {
            int textRight = left + mTextView.getMeasuredWidth();
            int textBottom = top + mTextView.getMeasuredHeight();
            mTextView.layout(left, top, textRight, textBottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        TextPosition textPosition = mDescriptor.getTextPosition();
        int contentHeight = (int) Math.round(mDescriptor.getCalcDesc().getContentSpaceHeight());
        int remainingContentSpace = contentHeight;
        if (textPosition == TextPosition.ABOVE || textPosition == TextPosition.BELOW)
            remainingContentSpace -= mDescriptor.getTextDescriptor().getCalcDescriptor().getTextHeight();
        if (remainingContentSpace < 0)
            remainingContentSpace = 0;
        int width = (int) Math.round(mDescriptor.getCalcDesc().getContentSpaceWidth());
        int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int imageHeightSpec = MeasureSpec.makeMeasureSpec(remainingContentSpace, MeasureSpec.EXACTLY);
        int textHeightSpec = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY);
        if (mLoadingView != null)
            mLoadingView.measure(widthSpec, imageHeightSpec);
        if (hasImageView())
            mImageView.measure(widthSpec, imageHeightSpec);
        if (hasTextView()) {
            mTextView.measure(widthSpec, textHeightSpec);
        }
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        if (mLoadingView != null)
            mLoadingView.requestLayout();
        if (hasImageView())
            mImageView.requestLayout();
        if (hasTextView()) {
            mTextView.requestLayout();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        onTouchEvent(event);
        return false;
    }
}
