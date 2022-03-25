package com.kinetise.data.descriptors.datadescriptors;

import android.text.InputType;

import com.kinetise.data.descriptors.types.EncryptionType;
import com.kinetise.data.descriptors.types.PasswordString;

public class AGPasswordDataDesc extends AGTextInputDataDesc<PasswordString> {

    public AGPasswordDataDesc(String id) {
        super(id);
    }

    @Override
    protected PasswordString createValue() {
        return new PasswordString(null);
    }

    public void setEncryptionType(EncryptionType encryptionType) {
        mEncryptionType = encryptionType;
        mValue.setEncryptionType(encryptionType);
    }

    public EncryptionType mEncryptionType;

    @Override
    public AGPasswordDataDesc createInstance() {
        return new AGPasswordDataDesc(getId());
    }

    @Override
    public int getKeyboard() {
        if (mKeyboard != null) {
            if (mKeyboard.equals("text")) {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }
        }
        return super.getKeyboard();
    }

    @Override
    public PasswordString getFormValue() {
        return mValue;
    }

    @Override
    public void setFormValue(String formValue) {
        setFormValue(formValue, true);
    }
}
