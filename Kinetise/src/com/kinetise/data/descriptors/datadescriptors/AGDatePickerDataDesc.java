package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.actions.StringVariableDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.DecoratorDescriptor;
import com.kinetise.data.descriptors.types.AGDatePickerModeType;
import com.kinetise.data.descriptors.types.FormString;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.data.systemdisplay.views.IValidateListener;
import com.kinetise.data.systemdisplay.views.OnStateChangedListener;
import com.kinetise.helpers.time.DateParser;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

public class AGDatePickerDataDesc extends AGButtonDataDesc implements IFormControlDesc<FormString> {

    private FormDescriptor mFormDescriptor;
    private OnStateChangedListener mOnStateChangedListener;
    private VariableDataDesc mWatermark;
    private int mWatermarkColor;
    private int mTextColor;

    private FormString mValue;
    private String mFormat;
    private AGDatePickerModeType mDatePickerMode;
    private VariableDataDesc mMinDateDesc;
    private VariableDataDesc mMaxDateDesc;
    private Date mMinDate;
    private Date mMaxDate;
    private Date mDateValue;
    private boolean mIsValid;
    private String mInvalidMessage;
    private IValidateListener mValidateListener;
    private DecoratorDescriptor mDecoratorDescriptor;

    public AGDatePickerDataDesc(String id) {
        super(id);
        mFormDescriptor = new FormDescriptor();
        mDecoratorDescriptor = new DecoratorDescriptor();
        mIsValid = true;
    }

    public int getTextColor() {
        return mTextColor;
    }

    @Override
    public AGButtonDataDesc createInstance() {
        return new AGDatePickerDataDesc(getId());
    }

    @Override
    public FormString getFormValue() {
        return mValue;
    }

    @Override
    public void clearFormValue() {
        initValue();
        if (mValidateListener != null)
            mValidateListener.setValidation(true);
    }

    public void initValue() {
        mDateValue = null;

        String initValue = mFormDescriptor.getInitValue().getStringValue();
        if (initValue != null && !initValue.equals("")) {
            try {
                setDate(getDateInRange(DateParser.tryParseDate(initValue), mMinDate, mMaxDate));
            } catch (ParseException e) {
                e.printStackTrace();
                String invalidDateFormat = LanguageManager.getInstance().getString(LanguageManager.INVALID_DATE_FORMAT);
                setText(new StringVariableDataDesc(invalidDateFormat));
                setFormValue(invalidDateFormat);
                mDateValue = null;
            }
        } else {
            setWatermarkAsText();
        }
    }

    public void setDate(Date date) {
        mDateValue = date;
        String formattedDateString = DateParser.getFormattedDateString(mDateValue, mFormat, TimeZone.getDefault(), false);
        setText(new StringVariableDataDesc(formattedDateString));
        setFormValue(DateParser.getDateAsRFC3339(mDateValue));
    }


    @Override
    public void setFormValue(String formValue) {
        if (mValue == null || !formValue.equals(mValue.toString())) {
            mValue = new FormString(formValue);
            if (mOnStateChangedListener != null) {
                mOnStateChangedListener.onStateChanged();
            }
        }
    }

    @Override
    public String getFormId() {
        return mFormDescriptor.getFormId();
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

    public void setStateChangeListener(OnStateChangedListener onStateChangedListener) {
        mOnStateChangedListener = onStateChangedListener;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mFormDescriptor.resolveVariable();
        mWatermark.resolveVariable();
        mMinDateDesc.resolveVariable();
        mMaxDateDesc.resolveVariable();
        mMinDate = getDateFromVariable(mMinDateDesc);
        mMaxDate = getDateFromVariable(mMaxDateDesc);
        initValue();
    }

    @Override
    public AGDatePickerDataDesc copy() {
        AGDatePickerDataDesc agDatePickerDataDesc = (AGDatePickerDataDesc) super.copy();
        agDatePickerDataDesc.setFormDescriptor(mFormDescriptor.copy(agDatePickerDataDesc));
        agDatePickerDataDesc.setWatermarkColor(mWatermarkColor);
        agDatePickerDataDesc.setTextColor(mTextColor);
        agDatePickerDataDesc.setWatermark(mWatermark.copy(agDatePickerDataDesc));
        agDatePickerDataDesc.setMinDateDesc(mMinDateDesc.copy(agDatePickerDataDesc));
        agDatePickerDataDesc.setMaxDateDesc(mMaxDateDesc.copy(agDatePickerDataDesc));
        agDatePickerDataDesc.setFormat(mFormat);
        agDatePickerDataDesc.setDatePickerMode(mDatePickerMode);
        agDatePickerDataDesc.mDecoratorDescriptor = mDecoratorDescriptor.copy(agDatePickerDataDesc);
        return agDatePickerDataDesc;
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
        setFormValue(getWatermark().getStringValue());
    }


    private void setText(VariableDataDesc text) {
        getTextDescriptor().setText(text);
        getTextDescriptor().setTextColor(mTextColor);
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public void removeOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        if (mOnStateChangedListener == onStateChangedListener)
            mOnStateChangedListener = null;
    }

    public AGDatePickerModeType getDatePickerMode() {
        return mDatePickerMode;
    }

    public void setDatePickerMode(AGDatePickerModeType datePickerMode) {
        mDatePickerMode = datePickerMode;
    }

    public void setMinDateDesc(VariableDataDesc minDateDesc) {
        mMinDateDesc = minDateDesc;
    }

    public void setMaxDateDesc(VariableDataDesc maxDateDesc) {
        mMaxDateDesc = maxDateDesc;
    }

    public String getFormat() {
        return mFormat;
    }

    public void setFormat(String format) {
        mFormat = format;
    }

    public VariableDataDesc getWatermark() {
        return mWatermark;
    }

    public Date getDateValue() {
        return mDateValue;
    }

    public Date getMinDate() {
        return mMinDate;
    }

    public Date getMaxDate() {
        return mMaxDate;
    }

    public static Date getDateInRange(Date date, Date min, Date max) {
        if (min != null && date.compareTo(min) < 0) {
            date = min;
        } else if (max != null && date.compareTo(max) > 0) {
            date = max;
        }
        return date;
    }

    public static Date getDateFromVariable(VariableDataDesc variable) {
        Date date = null;
        try {
            date = DateParser.tryParseDate(variable.getStringValue());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
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
