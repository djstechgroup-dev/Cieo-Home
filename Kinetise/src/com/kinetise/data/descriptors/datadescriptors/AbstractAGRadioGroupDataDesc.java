package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.types.AGLayoutType;
import com.kinetise.data.descriptors.types.FormString;
import com.kinetise.data.systemdisplay.views.IValidateListener;
import com.kinetise.data.systemdisplay.views.OnStateChangedListener;

import java.security.InvalidParameterException;
import java.util.List;

public abstract class AbstractAGRadioGroupDataDesc extends AbstractAGContainerDataDesc implements IFormControlDesc<FormString> {

    private FormDescriptor mFormDescriptor;
    private int mCheckedIndex;
    private boolean mIsValid;
    private String mInvalidMessage;
    private OnStateChangedListener mOnStateChangedListener;
    private IValidateListener mValidateListener;

    public AbstractAGRadioGroupDataDesc(String id, AGLayoutType layoutType) {
        super(id, layoutType);
        mFormDescriptor = new FormDescriptor();
        mIsValid = true;
    }

    public void clearFormValue() {
        initValue();
        if (mValidateListener != null)
            mValidateListener.setValidation(true);
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


    private void initValue() {
        List<AbstractAGElementDataDesc> children = getAllControls();
        if (children.size() <= 0) {
            return;
        }
        String initValue = mFormDescriptor.getInitValue().getValue().toString();
        for (AbstractAGElementDataDesc child : children) {
            AGRadioButtonDataDesc radioButton = ((AGRadioButtonDataDesc) child);
            if (radioButton.getValue().toString().equals(initValue)) {
                mCheckedIndex = radioButton.getRadioGroupIndex();
                radioButton.setChecked(true);
                return;
            }
        }
        mCheckedIndex = 0;
        for (AbstractAGElementDataDesc child : children) {
            AGRadioButtonDataDesc radioButton = ((AGRadioButtonDataDesc) child);
            if (radioButton.getRadioGroupIndex() == 0) {
                radioButton.setChecked(true);
            }
        }
    }

    public void checkAt(int index) {
        if (index < 0 || index >= getAllControls().size())
            throw new IllegalArgumentException("No child at index [" + index + "]");

        mCheckedIndex = index;

        List<AbstractAGElementDataDesc> children = getAllControls();
        for (AbstractAGElementDataDesc child : children) {
            AGRadioButtonDataDesc radioButton = ((AGRadioButtonDataDesc) child);
            if (radioButton.getRadioGroupIndex() != index) {
                radioButton.setChecked(false);
            }
        }

        setFormValue(null);
    }

    @Override
    public void addControl(AbstractAGElementDataDesc control) {
        if (!(control instanceof AGRadioButtonDataDesc)) {
            throw new InvalidParameterException("RadioGroup cannot contain child which is not instance of AGRadioButtonDataDesc");
        }

        AGRadioButtonDataDesc radioButton = (AGRadioButtonDataDesc) control;
        radioButton.setParentRadioGroup(this);
        int indexForNewControl = getAllControls().size();
        radioButton.setRadioGroupIndex(indexForNewControl);
        super.addControl(radioButton);
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mFormDescriptor.resolveVariable();
        initValue();
    }

    @Override
    public FormString getFormValue() {
        return ((AGRadioButtonDataDesc) getAllControls().get(mCheckedIndex)).getValue();
    }

    @Override
    public void setFormValue(String value) {
        if (mOnStateChangedListener != null) {
            mOnStateChangedListener.onStateChanged();
        }
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        mOnStateChangedListener = listener;
    }

    public void removeOnStateChangedListener(OnStateChangedListener listener) {
        if (mOnStateChangedListener == listener)
            mOnStateChangedListener = null;
    }

    public int getCheckedIndex() {
        return mCheckedIndex;
    }

    @Override
    public AbstractAGContainerDataDesc copy() {
        AbstractAGRadioGroupDataDesc copy = (AbstractAGRadioGroupDataDesc) super.copy();
        copy.setFormDescriptor(mFormDescriptor.copy(copy));
        return copy;
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
