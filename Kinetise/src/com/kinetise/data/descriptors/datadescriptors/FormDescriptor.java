package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.application.formdatautils.FormValidation;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;

import java.io.Serializable;

public class FormDescriptor implements Serializable {
    private VariableDataDesc mInitValue;
    private VariableDataDesc mFormId;
    private FormValidation mFormValidationRules;
    private int mInvalidColor;
    private int mInvalidBorderColor;

    public VariableDataDesc getInitValue() {
        return mInitValue;
    }

    public void setInitValue(VariableDataDesc initValue) {
        mInitValue = initValue;
    }

    public String getFormId() {
        return mFormId.getStringValue();
    }

    public void setFormId(VariableDataDesc formId) {
        mFormId = formId;
    }

    public FormValidation getFormValidation() {
        return mFormValidationRules;
    }

    public void setFormValidationRule(FormValidation formValidationRules) {
        mFormValidationRules = formValidationRules;
    }

    public int getInvalidColor() {
        return mInvalidColor;
    }

    public void setInvalidColor(int invalidColor) {
        mInvalidColor = invalidColor;
    }

    public int getInvalidBorderColor() {
        return mInvalidBorderColor;
    }

    public void setInvalidBorderColor(int invalidBorderColor) {
        mInvalidBorderColor = invalidBorderColor;
    }

    public FormDescriptor copy(AbstractAGElementDataDesc parent) {
        FormDescriptor copy = new FormDescriptor();
        if (mInitValue != null)
            copy.mInitValue = mInitValue.copy(parent);
        copy.mFormId = mFormId.copy(parent);
        copy.mInvalidColor = mInvalidColor;
        copy.mInvalidBorderColor = mInvalidBorderColor;
        copy.mFormValidationRules = mFormValidationRules.copy();
        return copy;
    }

    public void resolveVariable() {
        if (mFormId != null)
            mFormId.resolveVariable();
        if (mInitValue != null)
            mInitValue.resolveVariable();
    }

}
