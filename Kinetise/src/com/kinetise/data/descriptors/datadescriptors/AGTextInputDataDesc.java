package com.kinetise.data.descriptors.datadescriptors;

import android.text.InputType;

import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGTextInputCalcDescriptor;
import com.kinetise.data.descriptors.datadescriptors.components.DecoratorDescriptor;
import com.kinetise.data.descriptors.types.FormString;
import com.kinetise.data.systemdisplay.views.IValidateListener;
import com.kinetise.data.systemdisplay.views.OnStateChangedListener;
import com.kinetise.helpers.asynccaller.AsyncCaller;

/**
 * In general this class implements form-sending related options like form name, form value, and watermark(hint on inputs)
 */
public class AGTextInputDataDesc<T extends FormString> extends AGTextDataDesc implements IFormControlDesc<T> {

    protected T mValue;
    private FormDescriptor mFormDescriptor;
    private OnStateChangedListener mOnStateChangedListener;
    private VariableDataDesc mWatermark;
    private int mWatermarkColor;
    protected String mKeyboard;
    private boolean mIsValid;
    private String mInvalidMessage;
    private IValidateListener mValidateListener;
    private DecoratorDescriptor mDecoratorDescriptor;

    public AGTextInputDataDesc(String id) {
        super(id);
        mValue = createValue();
        mFormDescriptor = new FormDescriptor();
        mDecoratorDescriptor = new DecoratorDescriptor();
        mIsValid = true;
    }

    @Override
    public AGTextInputCalcDescriptor getCalcDesc() {
        if (mCalcDescriptor == null) {
            mCalcDescriptor = new AGTextInputCalcDescriptor();
        }

        return (AGTextInputCalcDescriptor) mCalcDescriptor;
    }

    protected T createValue() {
        return (T) new FormString(null);
    }

    public AGTextInputDataDesc createInstance() {
        return new AGTextInputDataDesc(getId());
    }

    public FormDescriptor getFormDescriptor() {
        return mFormDescriptor;
    }

    public void setFormDescriptor(FormDescriptor formDescriptor) {
        mFormDescriptor = formDescriptor;
    }

    public DecoratorDescriptor getDecoratorDescriptor() {
        return mDecoratorDescriptor;
    }

    public void setDecoratorDescriptor(DecoratorDescriptor decoratorDescriptor) {
        mDecoratorDescriptor = decoratorDescriptor;
    }

    public String getWatermark() {
        return mWatermark.getStringValue();
    }

    public int getWatermarkColor() {
        return mWatermarkColor;
    }

    public void setWatermarkColor(int color) {
        mWatermarkColor = color;
    }

    public void setWatermark(VariableDataDesc mWatermark) {
        this.mWatermark = mWatermark;
    }

    public int getKeyboard() {
        if (mKeyboard != null) {
            if (mKeyboard.equals("text")) {
                return InputType.TYPE_CLASS_TEXT;
            } else if (mKeyboard.equals("url")) {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI;
            } else if (mKeyboard.equals("email")) {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
            } else if (mKeyboard.equals("number")) {
                return InputType.TYPE_CLASS_NUMBER;
            } else if (mKeyboard.equals("phone")) {
                return InputType.TYPE_CLASS_PHONE;
            } else if (mKeyboard.equals("decimal")) {
                return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
            }
        }
        return InputType.TYPE_CLASS_TEXT;
    }

    public void setKeyboard(String keyboard) {
        this.mKeyboard = keyboard;
    }

    @Override
    public String getFormId() {
        return mFormDescriptor.getFormId();
    }

    @Override
    public T getFormValue() {
        return mValue;
    }

    @Override
    public void setFormValue(String formValue) {
        setFormValue(formValue, true);
    }

    public void setFormValue(String formValue, boolean callback) {
        if (mValue.getOriginalValue() == null || !mValue.getOriginalValue().equals(formValue)) {
            mValue.setOriginalValue(formValue);
            if (callback)
                notifyOnStateChangedListener();
        }
    }

    protected void notifyOnStateChangedListener() {
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mOnStateChangedListener != null)
                    mOnStateChangedListener.onStateChanged();
            }
        });
    }

    public void clearFormValue() {
        initValue(mFormDescriptor.getInitValue().getStringValue());
        if (mValidateListener != null)
            mValidateListener.setValidation(true);
    }

    public void initValue(String stringValue) {
        setFormValue(stringValue);
    }

    public void setStateChangeListener(OnStateChangedListener onStateChangedListener) {
        mOnStateChangedListener = onStateChangedListener;
    }

    public void removeStateChangeListener(OnStateChangedListener onStateChangedListener) {
        if (mOnStateChangedListener == onStateChangedListener)
            mOnStateChangedListener = null;
    }

    @Override
    public AGTextDataDesc copy() {
        AGTextInputDataDesc copy = (AGTextInputDataDesc) super.copy();
        copy.setWatermark(mWatermark.copy(copy));
        copy.setWatermarkColor(mWatermarkColor);
        copy.setKeyboard(mKeyboard);
        copy.setFormValue(mValue.getOriginalValue());
        copy.setFormDescriptor(mFormDescriptor.copy(copy));
        copy.mDecoratorDescriptor = mDecoratorDescriptor.copy(copy);
        return copy;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mWatermark.resolveVariable();
        mFormDescriptor.resolveVariable();
        initValue(mFormDescriptor.getInitValue().getStringValue());
    }

    public String getInputValue() {
        return mValue.getOriginalValue();
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
