package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.calcdescriptors.AGCompoundButtonCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.components.ITwoStateImageDescriptor;
import com.kinetise.data.descriptors.datadescriptors.components.ImageDescriptor;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.descriptors.types.AGVAlignType;
import com.kinetise.data.systemdisplay.views.OnStateChangedListener;
import com.kinetise.helpers.asynccaller.AsyncCaller;

/**
 * Abstract class for all chechboxes and radiobuttons
 */
public abstract class AbstractAGCompoundButtonDataDesc extends AGTextDataDesc implements ITwoStateImageDescriptor {
    private AGSizeDesc mCheckWidth;
    private AGSizeDesc mCheckHeight;
    private AGVAlignType mCheckedVAlign;
    private ImageDescriptor mUncheckedImageDescriptor;
    private ImageDescriptor mCheckedImageDescriptor;
    protected OnStateChangedListener mStateChangeListener;
    private boolean mShowLoading = true;

    private AGSizeDesc mInnerSpace;

    private boolean mChecked;

    public AbstractAGCompoundButtonDataDesc(String id) {
        super(id);
        mUncheckedImageDescriptor = new ImageDescriptor();
        mCheckedImageDescriptor = new ImageDescriptor();
    }

    public void setStateChangeListener(OnStateChangedListener listener) {
        mStateChangeListener = listener;
    }

    public void removeStateChangeListener(OnStateChangedListener listener) {
        if (mStateChangeListener == listener)
            mStateChangeListener = null;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mUncheckedImageDescriptor.resolveVariable();
        mCheckedImageDescriptor.resolveVariable();
    }

    @Override
    public ImageDescriptor getActiveImageDescriptor() {
        return mCheckedImageDescriptor;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return mUncheckedImageDescriptor;
    }

    @Override
    public void setImageDescriptor(ImageDescriptor imageDescriptor) {
        mUncheckedImageDescriptor = imageDescriptor;
    }

    @Override
    public void setActiveImageDescriptor(ImageDescriptor activeImageDescriptor) {
        mCheckedImageDescriptor = activeImageDescriptor;
    }

    public void setShowLoading(boolean showLoading) {
        mShowLoading = showLoading;
    }

    public boolean getShowLoading() {
        return mShowLoading;
    }

    public AGSizeDesc getCheckHeight() {
        return mCheckHeight;
    }

    public void setCheckHeight(AGSizeDesc checkHeight) {
        this.mCheckHeight = checkHeight;
    }

    public AGVAlignType getCheckVAlign() {
        return mCheckedVAlign;
    }

    public void setCheckVAlign(AGVAlignType checkVAlign) {
        mCheckedVAlign = checkVAlign;
    }

    public AGSizeDesc getCheckWidth() {
        return mCheckWidth;
    }

    public void setCheckWidth(AGSizeDesc checkWidth) {
        mCheckWidth = checkWidth;
    }

    /**
     * @return Space between image and text
     */
    public AGSizeDesc getInnerSpace() {
        return mInnerSpace;
    }

    /**
     * Sets space between image and text
     *
     * @param innerSpace space from image to text
     */
    public void setInnerSpace(AGSizeDesc innerSpace) {
        mInnerSpace = innerSpace;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean check) {
        if (mChecked != check) {
            mChecked = check;
            AsyncCaller.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mStateChangeListener != null)
                        mStateChangeListener.onStateChanged();
                }
            });

        }
    }

    @Override
    public AGCompoundButtonCalcDesc getCalcDesc() {
        if (mCalcDescriptor == null) {
            mCalcDescriptor = new AGCompoundButtonCalcDesc();
        }

        return (AGCompoundButtonCalcDesc) mCalcDescriptor;
    }


    @Override
    public AbstractAGCompoundButtonDataDesc copy() {
        AbstractAGCompoundButtonDataDesc copied = (AbstractAGCompoundButtonDataDesc) super.copy();
        copied.mCheckedImageDescriptor = mCheckedImageDescriptor.copy(copied);
        copied.mUncheckedImageDescriptor = mUncheckedImageDescriptor.copy(copied);

        if (getCheckHeight() != null) {
            copied.setCheckHeight(new AGSizeDesc(getCheckHeight().getDescValue(), getCheckHeight().getDescUnit()));
        }

        if (getCheckWidth() != null) {
            copied.setCheckWidth(new AGSizeDesc(getCheckWidth().getDescValue(), getCheckWidth().getDescUnit()));
        }

        if (mCheckedVAlign != null) {
            copied.mCheckedVAlign = mCheckedVAlign;
        }

        if (mInnerSpace != null) {
            copied.mInnerSpace = new AGSizeDesc(getInnerSpace().getDescValue(), getInnerSpace().getDescUnit());
        }

        copied.mChecked = this.mChecked;
        return copied;
    }

}
