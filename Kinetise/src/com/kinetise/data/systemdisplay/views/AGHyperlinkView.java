package com.kinetise.data.systemdisplay.views;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGHyperlinkDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;

public class AGHyperlinkView extends AGTextView<AGHyperlinkDataDesc> implements View.OnTouchListener {

    private MotionEvent mLastActionDownEvent;
    private int activeTextColor;
    private int inactiveTextColor;

    public AGHyperlinkView(SystemDisplay display, AGHyperlinkDataDesc desc) {
        super(display, desc);
        getTextColorsFromDescriptor();
        getBasicTextView().setOnTouchListener(this);
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        super.setDescriptor(descriptor);
        getTextColorsFromDescriptor();
    }

    private void getTextColorsFromDescriptor() {
        activeTextColor = mDescriptor.getActiveColor();
        inactiveTextColor = mDescriptor.getTextDescriptor().getTextColor();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean result = false;
        Rect outRect = new Rect();
        getDrawingRect(outRect);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_MOVE:

                if (!outRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    ((ViewGroup) getParent()).onInterceptTouchEvent(mLastActionDownEvent);
                    ((ViewGroup) getParent()).onInterceptTouchEvent(motionEvent);
                    onActionUp();
                    return false;
                } else {
                    onActionDown();
                    result = true;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (outRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    mLastActionDownEvent = motionEvent;
                    onActionDown();
                    result = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (outRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    onActionUp();
                    onClick(this);
                    result = true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                onActionUp();
                result = true;
                break;

            default:
                break;
        }

        return result;
    }

    private void onActionDown() {
        getBasicTextView().setTextColor(activeTextColor);
    }

    private void onActionUp() {
        getBasicTextView().setTextColor(inactiveTextColor);
    }
}
