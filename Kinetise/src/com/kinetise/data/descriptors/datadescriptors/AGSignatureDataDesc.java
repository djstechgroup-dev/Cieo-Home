package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.datadescriptors.components.ITwoStateImageDescriptor;
import com.kinetise.data.descriptors.datadescriptors.components.ImageDescriptor;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.descriptors.types.GestureInfo;
import com.kinetise.data.systemdisplay.views.AGSignatureView;
import com.kinetise.data.systemdisplay.views.IValidateListener;

public class AGSignatureDataDesc extends AbstractAGViewDataDesc implements IFormControlDesc, ITwoStateImageDescriptor {

    private GestureInfo mFormValue;
    private FormDescriptor mFormDescriptor;
    private boolean mIsValid;
    private String mInvalidMessage;
    private IValidateListener mValidateListener;
    private int mStrokeColor;
    private AGSizeDesc mStrokeWidth;
    private AGSizeDesc mClearSize;

    private AGSignatureView mSignatureView;

    protected ImageDescriptor mClearImageDescriptor;
    protected ImageDescriptor mActiveClearImageDescriptor;

    public AGSignatureDataDesc(String id) {
        super(id);
        mClearImageDescriptor = new ImageDescriptor();
        mActiveClearImageDescriptor = new ImageDescriptor();
        mFormDescriptor = new FormDescriptor();
        mFormValue = new GestureInfo();
        mIsValid = true;
    }

    public void setFormValue(String value) {
    }

    public FormDescriptor getFormDescriptor() {
        return mFormDescriptor;
    }

    public void setFormDescriptor(FormDescriptor formDescriptor) {
        mFormDescriptor = formDescriptor;
    }

    @Override
    public GestureInfo getFormValue() {
        if (mSignatureView != null) {
            return mSignatureView.getSignature();
        }
        return mFormValue;
    }

    @Override
    public void clearFormValue() {
        mFormValue = null;
        if (mSignatureView != null)
            mSignatureView.clearGesture();
        if (mValidateListener != null)
            mValidateListener.setValidation(true);
    }

    @Override
    public AGSignatureDataDesc createInstance() {
        return new AGSignatureDataDesc(getId());
    }

    @Override
    public AGSignatureDataDesc copy() {
        AGSignatureDataDesc copied = (AGSignatureDataDesc) super.copy();
        copied.mFormValue = mFormValue;
        copied.setFormDescriptor(mFormDescriptor.copy(copied));
        copied.mStrokeColor = mStrokeColor;
        copied.setImageDescriptor(mClearImageDescriptor.copy(copied));
        copied.setActiveImageDescriptor(mActiveClearImageDescriptor.copy(copied));
        if (mStrokeWidth != null) {
            copied.mStrokeWidth = new AGSizeDesc(this.mStrokeWidth.getDescValue(), (mStrokeWidth.getDescUnit()));
        }
        if (mClearSize != null) {
            copied.mClearSize = new AGSizeDesc(this.mClearSize.getDescValue(), (mClearSize.getDescUnit()));
        }

        return copied;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mFormDescriptor.resolveVariable();
        mFormValue = null;
        mClearImageDescriptor.resolveVariable();
        mActiveClearImageDescriptor.resolveVariable();
    }

    @Override
    public String getFormId() {
        return mFormDescriptor.getFormId();
    }

    public void setStrokeColor(int strokeColor) {
        mStrokeColor = strokeColor;
    }

    public int getStrokeColor() {
        return mStrokeColor;
    }

    public void setStrokeWidth(AGSizeDesc size) {
        mStrokeWidth = size;
    }

    public AGSizeDesc getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setClearSize(AGSizeDesc size) {
        mClearSize = size;
    }

    public AGSizeDesc getClearSize() {
        return mClearSize;
    }

    @Override
    public ImageDescriptor getActiveImageDescriptor() {
        return mActiveClearImageDescriptor;
    }

    @Override
    public void setActiveImageDescriptor(ImageDescriptor activeImageDescriptor) {
        mActiveClearImageDescriptor = activeImageDescriptor;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return mClearImageDescriptor;
    }

    @Override
    public void setImageDescriptor(ImageDescriptor imageDescriptor) {
        mClearImageDescriptor = imageDescriptor;
    }

    public void setSignatureView(AGSignatureView signatureView) {
        mSignatureView = signatureView;
    }

    public void removeSignatureView(AGSignatureView signatureView) {
        if (mSignatureView == signatureView)
            mSignatureView = null;
    }

    public void setGestureInfo(GestureInfo gestureInfo) {
        mFormValue = gestureInfo;
        mSignatureView.setSignature(gestureInfo.getGesture());
    }


    @Override
    public boolean isFormValid() {
        if (mValidateListener != null) {
            mValidateListener.validateForm();
            return mIsValid;
        }
        return false;
    }

    public void setValid(boolean isValid, String message) {
        mIsValid = isValid;
        mInvalidMessage = message;
        if (mIsValid) {
            setCurrentBorderColor(getBorderColor());
        } else {
            setCurrentBorderColor(mFormDescriptor.getInvalidBorderColor());
        }
    }

    public String getInvalidMessage() {
        return mInvalidMessage;
    }

    @Override
    public void setValidateListener(IValidateListener listener) {
        mValidateListener = listener;
    }

    @Override
    public void removeValidateListener(IValidateListener listener) {
        if (mValidateListener == listener)
            mValidateListener = null;
    }
}
