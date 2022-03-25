package com.kinetise.data.systemdisplay.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewParent;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.components.ITextDescriptor;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.drawing.BackgroundSetterCommandCallback;
import com.kinetise.helpers.drawing.ViewDrawer;

import java.security.InvalidParameterException;

public class AGTextView<T extends AbstractAGViewDataDesc & ITextDescriptor> extends AGControl<T> implements IAGView, View.OnClickListener, BackgroundSetterCommandCallback, OnUpdateListener {

    private static final String ASSETS_PREFIX = "assets://";
    private BasicTextView mBasicTextView;

    public AGTextView(SystemDisplay display, T desc) {
        super(display, desc);
        mBasicTextView = createBasicTextView(display.getActivity());
        mBasicTextView.setTextDescriptor(mDescriptor.getTextDescriptor());
        mBasicTextView.setSoundEffectsEnabled(false);
        mBasicTextView.setOnClickListener(this);
        mBasicTextView.setClickable(true);
        addView(mBasicTextView);
        setOnClickListener(this);
    }

    protected BasicTextView createBasicTextView(Context context) {
        return new BasicTextView(context);
    }

    public void setText(VariableDataDesc textVariable) {
        getDescriptor().getTextDescriptor().setText(textVariable);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDescriptor.setOnUpdateListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDescriptor.setOnUpdateListener(null);
    }

    @Override
    public void onUpdated() {
        mBasicTextView.setTextColor(getDescriptor().getTextDescriptor().getTextColor());
        super.onUpdated();
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        String baseUrl = mDescriptor.getFeedBaseAdress();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        mBackgroundSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        super.setDescriptor(descriptor);
        mBasicTextView.setTextDescriptor(((ITextDescriptor) descriptor).getTextDescriptor());
    }

    @Override
    public T getDescriptor() {
        return mDescriptor;
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
    protected void onLayout(boolean bool, int l, int t, int r, int b) {
        super.onLayout(bool, l, t, r, b);
        AGViewCalcDesc textImageCalcDesc = getDescriptor().getCalcDesc();

        int top = (int) Math.round(textImageCalcDesc.getBorder().getTop() + textImageCalcDesc.getPaddingTop());
        int left = (int) Math.round(textImageCalcDesc.getBorder().getLeft() + textImageCalcDesc.getPaddingLeft());
        int right = (r - l) - ((int) Math.round(textImageCalcDesc.getBorder().getRight() + textImageCalcDesc.getPaddingRight()));
        int bottom = (b - t) - ((int) Math.round(textImageCalcDesc.getBorder().getBottom() + textImageCalcDesc.getPaddingBottom()));
        mBasicTextView.layout(left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBasicTextView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        if (mBasicTextView != null)
            mBasicTextView.requestLayout();
    }


    protected BasicTextView getBasicTextView() {
        return mBasicTextView;
    }
}
