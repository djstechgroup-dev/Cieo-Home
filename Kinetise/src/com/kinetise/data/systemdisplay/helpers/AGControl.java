package com.kinetise.data.systemdisplay.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.kinetise.components.activity.KinetiseActivity;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.NullVariableDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.BackgroundImageDescriptor;
import com.kinetise.data.descriptors.datadescriptors.components.ImageDescriptor;
import com.kinetise.data.sourcemanager.AbstractGetSourceCommand;
import com.kinetise.data.sourcemanager.ImageSource;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageChangeListener;
import com.kinetise.data.systemdisplay.views.IAGView;
import com.kinetise.data.systemdisplay.views.OnUpdateListener;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.drawing.BackgroundSetterCommandCallback;
import com.kinetise.helpers.drawing.ViewDrawer;
import com.kinetise.support.logger.Logger;

public abstract class AGControl<descriptorType extends AbstractAGElementDataDesc> extends FrameLayout implements IAGView, BackgroundSetterCommandCallback, OnUpdateListener {

    protected descriptorType mDescriptor;
    protected ImageSource mBackgroundSource;
    protected ViewDrawer<AGControl> mDrawer;
    protected SystemDisplay mDisplay;
    protected boolean isButtonPressed = false;

    public AGControl(SystemDisplay systemDisplay, descriptorType descriptor) {
        super(systemDisplay.getActivity());
        mDescriptor = descriptor;
        mDisplay = systemDisplay;
        mDrawer = new ViewDrawer(this);
        setWillNotDraw(false);
        setAlwaysDrawnWithCacheEnabled(false);
        if (mDescriptor instanceof AbstractAGViewDataDesc) {
            ImageDescriptor imageDescriptor = new BackgroundImageDescriptor(((AbstractAGViewDataDesc) mDescriptor).getBackground());
            mBackgroundSource = new ImageSource(imageDescriptor, new BackgroundChangeListener());
        }
    }

    public void addBackgroundImageChangeListener(ImageChangeListener listener) {
        mBackgroundSource.addImageChangeListener(listener);
    }

    @Override
    public SystemDisplay getSystemDisplay() {
        return mDisplay;
    }

    private AGControl(Context context) {
        super(context);
        setWillNotDraw(false);
        setAlwaysDrawnWithCacheEnabled(false);
    }

    public AGControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        setAlwaysDrawnWithCacheEnabled(false);
    }

    public AGControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        setAlwaysDrawnWithCacheEnabled(false);
    }

    @Override
    public void onUpdated() {
        refreshDrawer();
        invalidate();
    }

    public void refreshDrawer() {
        mDrawer.refresh();
    }

    @Override
    public void draw(Canvas canvas) {
        ViewDrawer drawer = getViewDrawer();
        if (drawer != null) {
            drawer.draw(canvas);
        }
        super.draw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        ViewDrawer drawer = getViewDrawer();
        if (drawer != null) {
            drawer.onAfterDispatchDraw(canvas);
        }
    }

    /**
     * Each AGControl has dimension decided by calculates. We should do nothing in onMeasure but only use provide spec.
     * However, we need to call super. as View class has some internal logic that forces layout in case dimension changes. We need this to run.
     * This should be overriden in container views where measure() of children needs to be called.
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // AGControl in abstract sense does not have children. Specif subclasses may have
        ViewDrawer drawer = getViewDrawer();
        if (drawer != null)
            drawer.refresh();
    }

    @Override
    public descriptorType getDescriptor() {
        return mDescriptor;
    }

    @Override
    public IAGView getAGViewParent() {
        return null;
    }

    @Override
    public void loadAssets() {
    }

    public void cancelCommand(AbstractGetSourceCommand command) {
        if (command != null)
            command.cancel();
    }

    @Override
    public boolean accept(IViewVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        mDescriptor = (descriptorType) descriptor;
        requestLayout(); //used by adapters to redraw controls
    }

    @Override
    public void onClick(View v) {
        if (!isEnabled()) {
            return;
        }
        if (AGApplicationState.getInstance().getActivity() != null && !(v instanceof EditText))
            ((KinetiseActivity) AGApplicationState.getInstance().getActivity()).closeKeyboard();
        if (SystemDisplay.isScreenBlocked())
            return;

        executeClick(v);
    }

    protected void executeClick(View v) {
        AbstractAGViewDataDesc desc = (mDescriptor instanceof AbstractAGViewDataDesc) ? (AbstractAGViewDataDesc) mDescriptor : null;
        if (desc != null) {
            VariableDataDesc action = desc.getOnClickActionDesc();
            if (action != null && !(action instanceof NullVariableDataDesc)) {
                action.resolveVariable();
            } else {
                passOnClickToParent(v);
            }
        } else {
            passOnClickToParent(v);
        }
    }

    protected void passOnClickToParent(View v) {
        ViewParent parent = getParent();
        if (parent instanceof IAGView) {
            ((IAGView) parent).onClick(v);
        }
    }

    public class BackgroundChangeListener implements ImageChangeListener {
        @Override
        public void onImageChanged(Bitmap bitmap) {
            setBackgroundBitmap(bitmap);
        }
    }

    @Override
    public void setBackgroundBitmap(Bitmap bitmap) {
        mDrawer.setBackgroundBitmap(bitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        boolean result = super.onTouchEvent(motionEvent);
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

    protected void setActiveState() {
        if (isButtonPressed)
            return;
        isButtonPressed = true;
    }

    protected void setInactiveState() {
        if (!isButtonPressed)
            return;
        isButtonPressed = false;
    }
}
