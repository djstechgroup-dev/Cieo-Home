package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.datadescriptors.components.ImageDescriptor;
import com.kinetise.data.descriptors.types.PhotoInfo;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.data.systemdisplay.views.IValidateListener;
import com.kinetise.data.systemdisplay.views.OnStateChangedListener;

import java.util.Locale;

public class AGPhotoDataDesc extends AGButtonDataDesc implements IFormControlDesc {

    private PhotoInfo mFormValue;
    private FormDescriptor mFormDescriptor;
    private ImageDescriptor mPhotoDescriptor;
    private OnStateChangedListener mOnStateChangedListener;
    private boolean mIsValid;
    private String mInvalidMessage;
    private IValidateListener mValidateListener;

    public AGPhotoDataDesc(String id) {
        super(id);
        mPhotoDescriptor = new ImageDescriptor();
        mFormDescriptor = new FormDescriptor();
        mFormValue = new PhotoInfo();
        mIsValid = true;
    }

    public ImageDescriptor getPhotoDescriptor() {
        return mPhotoDescriptor;
    }

    public FormDescriptor getFormDescriptor() {
        return mFormDescriptor;
    }

    public void setFormDescriptor(FormDescriptor formDescriptor) {
        mFormDescriptor = formDescriptor;
    }

    public void setFormValue(String imagePath) {
        mPhotoDescriptor.setImageSrc(String.format(Locale.US, "%s%s", AssetsManager.PREFIX_SDCARD, imagePath));
        mFormValue.setPath(imagePath);
        if (mOnStateChangedListener != null)
            mOnStateChangedListener.onStateChanged();
    }

    @Override
    public PhotoInfo getFormValue() {
        return mFormValue.copy();
    }

    @Override
    public String getFormId() {
        return mFormDescriptor.getFormId();
    }

    @Override
    public void clearFormValue() {
        mFormValue.clear();
        if (mOnStateChangedListener != null)
            mOnStateChangedListener.onStateChanged();
        if (mValidateListener != null)
            mValidateListener.setValidation(true);
    }

    @Override
    public AGPhotoDataDesc createInstance() {
        return new AGPhotoDataDesc(getId());
    }

    @Override
    public AGPhotoDataDesc copy() {
        AGPhotoDataDesc copied = (AGPhotoDataDesc) super.copy();
        copied.mFormValue = mFormValue.copy();
        copied.setFormDescriptor(mFormDescriptor.copy(copied));
        return copied;
    }

    public boolean isPhotoTaken() {
        return mFormValue.isPhotoTaken();
    }

    public void setStateChangeListener(OnStateChangedListener listener) {
        mOnStateChangedListener = listener;
    }

    public void removeStateChangeListener(OnStateChangedListener listener) {
        if (mOnStateChangedListener == listener)
            mOnStateChangedListener = null;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mFormValue.clear();
        mFormDescriptor.resolveVariable();
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
