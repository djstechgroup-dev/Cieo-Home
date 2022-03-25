package com.kinetise.data.descriptors.types;

import com.kinetise.data.descriptors.IFormValue;

public class FormString implements IFormValue {

    String mOriginalValue;

    public FormString(String value) {
        mOriginalValue = value;
    }

    public void setOriginalValue(String originalValue) {
        mOriginalValue = originalValue;
    }

    public String getOriginalValue() {
        return mOriginalValue;
    }

    public FormString copy() {
        FormString result = new FormString(getOriginalValue());
        return result;
    }

    @Override
    public String toString() {
        return mOriginalValue;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FormString))
            return false;

        if ((((FormString) o).getOriginalValue() == null && mOriginalValue == null) || ((FormString) o).getOriginalValue().equals(mOriginalValue)) {
            return true;
        }

        return super.equals(o);
    }
}
