package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.actions.StringVariableDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.types.FormString;
import com.kinetise.data.systemdisplay.views.IValidateListener;
import com.kinetise.data.systemdisplay.views.OnStateChangedListener;
import com.kinetise.helpers.asynccaller.AsyncCaller;

public class AGGetPhoneContactDataDesc extends AGButtonDataDesc implements IFormControlDesc<FormString> {

    private FormString mFormValue;
    private FormDescriptor mFormDescriptor;
    private boolean isPhoneContactSet = false;
    private OnStateChangedListener mListener;
    private VariableDataDesc mWatermark;
    private int mWatermarkColor;
    private int mTextColor;
    private boolean mIsValid;
    private String mInvalidMessage;
    private IValidateListener mValidateListener;

    public AGGetPhoneContactDataDesc(String id) {
        super(id);
        mFormDescriptor = new FormDescriptor();
        mIsValid = true;
    }


    public void initValue() {
        String initValue = mFormDescriptor.getInitValue().getStringValue();
        if (initValue != null && !initValue.equals("")) {
            setText(mFormDescriptor.getInitValue());
            setFormValue(initValue);
        } else {
            setWatermarkAsText();
        }
    }

    public void setFormValue(String formValue) {
        mFormValue = new FormString(formValue);
        setText(new StringVariableDataDesc(formValue));
    }

    public FormDescriptor getFormDescriptor() {
        return mFormDescriptor;
    }

    public void setFormDescriptor(FormDescriptor formDescriptor) {
        mFormDescriptor = formDescriptor;
    }

    private void setText(VariableDataDesc text) {
        getTextDescriptor().setText(text);
        getTextDescriptor().setTextColor(mTextColor);
    }

    public VariableDataDesc getWatermark() {
        return mWatermark;
    }

    public int getWatermarkColor() {
        return mWatermarkColor;
    }

    public void setWatermarkColor(int color) {
        mWatermarkColor = color;
    }

    public void setWatermark(VariableDataDesc watermark) {
        mWatermark = watermark;
    }

    public void setWatermarkAsText() {
        getTextDescriptor().setText(getWatermark());
        getTextDescriptor().setTextColor(getWatermarkColor());
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    @Override
    public FormString getFormValue() {
        if (isPhoneContactSet)
            return mFormValue;
        else
            return null;
    }

    @Override
    public void clearFormValue() {
        setIsPhoneContact(false);
        mFormValue = null;
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mListener != null)
                    mListener.onStateChanged();
            }
        });
        if (mValidateListener != null)
            mValidateListener.setValidation(true);
    }

    @Override
    public AGGetPhoneContactDataDesc createInstance() {
        return new AGGetPhoneContactDataDesc(getId());
    }

    @Override
    public AGGetPhoneContactDataDesc copy() {
        AGGetPhoneContactDataDesc copied = (AGGetPhoneContactDataDesc) super.copy();
        copied.mFormValue = mFormValue;
        copied.setFormDescriptor(mFormDescriptor.copy(copied));
        return copied;
    }

    public boolean isPhoneContactSet() {
        return isPhoneContactSet;
    }

    public void setIsPhoneContact(boolean isPhoneContactSet) {
        this.isPhoneContactSet = isPhoneContactSet;
    }

    public void setStateChangeListener(OnStateChangedListener listener) {
        mListener = listener;
    }

    public void removeStateChangeListener(OnStateChangedListener listener) {
        if (mListener == listener)
            mListener = null;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mFormDescriptor.resolveVariable();
        mWatermark.resolveVariable();
        initValue();
    }

    @Override
    public String getFormId() {
        return mFormDescriptor.getFormId();
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
