package com.kinetise.data.systemdisplay.views;

import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.formdatautils.FormValidationRule;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGSignatureDataDesc;
import com.kinetise.data.descriptors.types.AGSizeModeType;
import com.kinetise.data.descriptors.types.GestureInfo;
import com.kinetise.data.sourcemanager.ImageSource;
import com.kinetise.data.systemdisplay.LayoutHelper;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageChangeListener;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.helpers.drawing.ViewDrawer;
import com.kinetise.helpers.http.RedirectMap;
import com.kinetise.support.logger.Logger;
import com.kinetise.support.scrolls.scrollManager.EventDirection;
import com.kinetise.support.scrolls.scrollManager.ScrollManager;
import com.kinetise.support.scrolls.scrollManager.ScrollType;

public class AGSignatureView extends AGControl<AGSignatureDataDesc> implements IScrollable, IFormView, IValidateListener, OnUpdateListener {
    public static final double CLEARVIEW_MARGIN_PERCENTAGE = 0.1;
    public static final String CLEAR_BUTTON_CONTENT_DESCRIPTION = "clearButton";

    private final BasicImageView mClearView;
    private final ImageSource activeImageSource;
    private final InvalidView mInvalidView;
    private GestureOverlayView mGestureView;
    protected ImageSource imageSource;
    protected AGLoadingView mLoadingView;
    protected boolean isButtonPressed = false;

    public AGSignatureView(SystemDisplay display, AGSignatureDataDesc desc) {
        super(display, desc);

        mGestureView = createGestureView();
        mClearView = createClearImageView();
        mInvalidView = createInvalidView();
        hideInvalidView();

        addView(mGestureView);
        addView(mClearView);
        addView(mInvalidView);
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
                mClearView.setImageBitmap(bitmap);
            }
        };
        imageSource = new ImageSource(mDescriptor.getImageDescriptor(), imageSetCallback, loadingStartedListener);
        activeImageSource = new ImageSource(mDescriptor.getActiveImageDescriptor(), null);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDescriptor.setSignatureView(this);
        mDescriptor.setValidateListener(this);
        mDescriptor.setOnUpdateListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDescriptor.removeSignatureView(this);
        mDescriptor.removeValidateListener(this);
        mDescriptor.setOnUpdateListener(null);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        requestFocus();
    }

    @Override
    public void loadAssets() {
        String baseUrl = mDescriptor.getFeedBaseAdress();
        super.loadAssets();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        mBackgroundSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
        imageSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
        activeImageSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
    }

    private GestureOverlayView createGestureView() {
        GestureOverlayView gestureView = new GestureOverlayView(AGApplicationState.getInstance().getContext());
        gestureView.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
        gestureView.setGesture(new Gesture());
        gestureView.setGestureColor(mDescriptor.getStrokeColor());
        gestureView.setUncertainGestureColor(mDescriptor.getStrokeColor());
        gestureView.setGestureStrokeWidth((float) mDescriptor.getStrokeWidth().inPixels());
        gestureView.setHandleGestureActions(true);
        gestureView.addOnGestureListener(new GestureOverlayView.OnGestureListener() {
            @Override
            public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
                setValid(true);
            }

            @Override
            public void onGesture(GestureOverlayView overlay, MotionEvent event) {

            }

            @Override
            public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {

            }

            @Override
            public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {

            }
        });
        return gestureView;
    }

    private BasicImageView createClearImageView() {
        BasicImageView view = new BasicImageView(mDisplay.getActivity());
        view.setSoundEffectsEnabled(false);
        view.setContentDescription(CLEAR_BUTTON_CONTENT_DESCRIPTION);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearGesture();
                if (!mDescriptor.isFormValid()) {
                    showInvalidMessageToast();
                }
            }
        });
        view.setSizeMode(AGSizeModeType.STRETCH);
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                boolean result = v.onTouchEvent(motionEvent);
                Rect outRect = new Rect();
                getDrawingRect(outRect);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (!outRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            Logger.v(this, "onTouchEvent", "Action move: outside");
                            setInactiveState();
                            return false;
                        } else {
                            Logger.i(this, "Action move: inside");
                            setActiveState();
                            result = true;
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Logger.v(this, "onTouchEvent", "Action down");
                        int x = (int) motionEvent.getX();
                        int y = (int) motionEvent.getY();
                        if (outRect.contains(x, y)) {
                            setActiveState();
                            result = true;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        Logger.v(this, "onTouchEvent", "Action up");
                        if (outRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            setInactiveState();
                            result = true;
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Logger.v(this, "onTouchEvent", "Action cancel");
                        setInactiveState();
                        break;
                    case MotionEvent.ACTION_OUTSIDE:
                        Logger.v(this, "onTouchEvent", "Action outside");
                        setInactiveState();
                        break;

                    default:
                        break;
                }

                return result;
            }
        });
        return view;
    }

    public void clearGesture() {
        mGestureView.cancelClearAnimation();
        mGestureView.clear(false);
        mGestureView.setGesture(new Gesture());
        forceLayout();
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        super.setDescriptor(descriptor);
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return mDrawer;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        AGViewCalcDesc calcDesc = getDescriptor().getCalcDesc();

        View gestureView = getChildAt(0);
        LayoutHelper.layoutFill(gestureView, calcDesc);

        int right = (r - l) - ((int) Math.round(calcDesc.getBorder().getRight() + calcDesc.getPaddingRight()));
        int bottom = (b - t) - ((int) Math.round(calcDesc.getBorder().getBottom() + calcDesc.getPaddingBottom()));

        View clearView = getChildAt(1);
        int clearViewBottom = bottom - (int) (CLEARVIEW_MARGIN_PERCENTAGE * clearView.getMeasuredHeight());
        int clearViewRight = right - (int) (CLEARVIEW_MARGIN_PERCENTAGE * clearView.getMeasuredWidth());
        int clearViewLeft = (clearViewRight - clearView.getMeasuredWidth());
        int clearViewTop = (clearViewBottom - clearView.getMeasuredHeight());
        clearView.layout(clearViewLeft, clearViewTop, clearViewRight, clearViewBottom);

        int rightWithoutPadding = (r - l) - ((int) Math.round(calcDesc.getBorder().getRight()));
        InvalidView invalidView = (InvalidView) getChildAt(2);
        invalidView.layoutInvalidView(calcDesc, rightWithoutPadding);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();

        View gestureView = getChildAt(0);
        LayoutHelper.measureFill(gestureView, calcDesc);

        View clearView = getChildAt(1);
        LayoutHelper.measureExactly(clearView, (int) mDescriptor.getClearSize().inPixels(), (int) mDescriptor.getClearSize().inPixels());

        InvalidView invalidView = (InvalidView) getChildAt(2);
        invalidView.measure();
    }

    public void onLoadingStarted() {
        mClearView.setImageBitmap(null);

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

    protected void setActiveState() {
        if (isButtonPressed)
            return;
        isButtonPressed = true;

        mClearView.setImageBitmap(activeImageSource.getBitmap());
    }

    protected void setInactiveState() {
        if (!isButtonPressed)
            return;
        isButtonPressed = false;

        mClearView.setImageBitmap(imageSource.getBitmap());
    }

    @Override
    public ScrollType getScrollType() {
        return ScrollType.FREESCROLL;
    }

    @Override
    public int getScrollXValue() {
        return Integer.MAX_VALUE / 2;
    }

    @Override
    public int getScrollYValue() {
        return Integer.MAX_VALUE / 2;
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
        return Integer.MAX_VALUE;
    }

    @Override
    public int getContentHeight() {
        return Integer.MAX_VALUE;
    }

    @Override
    public EventDirection getEventDirectionForScrollType() {
        return EventDirection.UNKNOWN;
    }

    @Override
    public void scrollViewTo(int x, int y) {

    }

    @Override
    public void restoreScroll() {

    }

    @Override
    public String getTag() {
        return "AGSignatureView" + super.getTag();
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

                result = true;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        Logger.d("TouchManager", "onInterceptTouchEvent: event[" + ev.getAction() + "], result[" + result + "], desc[" +
                getDescriptor().toString() + "]");

        return super.onInterceptTouchEvent(ev);
    }

    public GestureInfo getSignature() {
        GestureInfo gestureInfo = new GestureInfo();
        gestureInfo.setGesture(mGestureView.getGesture());
        gestureInfo.setPaint(mGestureView.getGesturePaint());
        gestureInfo.setWidth(mGestureView.getMeasuredWidth());
        gestureInfo.setHeight(mGestureView.getMeasuredHeight());

        return gestureInfo;
    }

    public void setSignature(Gesture gesture) {
        if (gesture != null) {
            mGestureView.setGesture(gesture, false);
        }
    }

    @Override
    public void validateForm() {
        setValid(true);
        for (FormValidationRule rule : mDescriptor.getFormDescriptor().getFormValidation().getRules()) {
            if (rule.getType().equals(FormValidationRule.TYPE_REQUIRED)) {
                if (mGestureView.getGesture() == null || mGestureView.getGesture().getStrokes().size() == 0) {
                    setValid(false, rule.getMessage());
                }
            }
        }
    }

    @Override
    public void setValidation(boolean isValid) {
        setValid(isValid);
    }

    private void setValid(boolean isValid) {
        setValid(isValid, null);
    }

    private void setValid(boolean isValid, String message) {
        mDescriptor.setValid(isValid, message);
        if (isValid) {
            hideInvalidView();
        } else {
            showInvalidView();
        }
        mDrawer.refresh();
    }

    @Override
    public void showInvalidMessageToast() {
        PopupManager.showInvalidFormToast(mDescriptor.getInvalidMessage());
    }

    @Override
    public void hideInvalidView() {
        mInvalidView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showInvalidView() {
        mInvalidView.setVisibility(View.VISIBLE);
    }

    private InvalidView createInvalidView() {
        return InvalidView.createInvalidView(mDisplay.getActivity(), mDescriptor.getFormDescriptor().getInvalidBorderColor(), this);
    }

}
