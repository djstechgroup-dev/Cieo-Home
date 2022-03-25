package com.kinetise.data.descriptors.types;

import com.kinetise.data.descriptors.IFormValue;

public class FormBoolean implements IFormValue {
    Boolean mOriginalValue = null;

    public FormBoolean(boolean value) {
        mOriginalValue = new Boolean(value);
    }

    public Boolean getOriginalValue() {
        return mOriginalValue;
    }

    public void setOriginalValue(Boolean originalValue) {
        mOriginalValue = originalValue;
    }

    public FormBoolean copy() {
        FormBoolean result = new FormBoolean(getOriginalValue());
        return result;
    }

    @Override
    public String toString() {
        return mOriginalValue.toString();
    }
}
