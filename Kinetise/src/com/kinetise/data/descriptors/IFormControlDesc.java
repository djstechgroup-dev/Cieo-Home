package com.kinetise.data.descriptors;

import com.kinetise.data.descriptors.datadescriptors.FormDescriptor;
import com.kinetise.data.systemdisplay.views.IValidateListener;

public interface IFormControlDesc<T extends IFormValue> {

    T getFormValue();

    void setFormValue(String value);

    void clearFormValue();

    String getFormId();

    FormDescriptor getFormDescriptor();

    boolean isFormValid();

    void setValidateListener(IValidateListener listener);

    void removeValidateListener(IValidateListener listener);

}
