package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.types.FormBoolean;
import com.kinetise.data.systemdisplay.views.IValidateListener;

import java.util.Locale;

public class AGCheckBoxDataDesc extends AbstractAGCompoundButtonDataDesc implements IFormControlDesc<FormBoolean> {

    protected FormDescriptor mFormDescriptor;
    private boolean mIsValid;
    private String mInvalidMessage;
    private IValidateListener mValidateListener;

    public AGCheckBoxDataDesc(String id) {
        super(id);
        mFormDescriptor = new FormDescriptor();
        mIsValid = true;
    }

    public void clearFormValue() {
        resolveVariables();
        if (mValidateListener != null)
            mValidateListener.setValidation(true);
    }

    @Override
    public AbstractAGCompoundButtonDataDesc createInstance() {
        return new AGCheckBoxDataDesc(String.valueOf(getId()));
    }

    public FormDescriptor getFormDescriptor() {
        return mFormDescriptor;
    }

    public void setFormDescriptor(FormDescriptor formDescriptor) {
        mFormDescriptor = formDescriptor;
    }

    @Override
    public AbstractAGCompoundButtonDataDesc copy() {
        AGCheckBoxDataDesc copy = (AGCheckBoxDataDesc) super.copy();
        copy.setFormDescriptor(mFormDescriptor.copy(copy));
        return copy;
    }

    @Override
    public FormBoolean getFormValue() {
        return new FormBoolean(isChecked());
    }

    @Override
    public void setFormValue(String value) {
        checkIfTrue(value);
    }

    public Boolean getFormValueAsBoolean() {
        return isChecked();
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mFormDescriptor.resolveVariable();
        if (mFormDescriptor.getInitValue() != null) {
            checkIfTrue(mFormDescriptor.getInitValue().getStringValue());
        }
    }

    protected void checkIfTrue(String value) {
        if (value != null && value.toLowerCase(Locale.US).equals("true"))
            setChecked(true);
        else
            setChecked(false);
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
