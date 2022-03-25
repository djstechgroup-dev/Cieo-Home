package com.kinetise.data.descriptors.desctriptorvisitors;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;

public class ValidateFormVisitor implements IDataDescVisitor {

    private boolean isFormValid;

    public ValidateFormVisitor() {
        isFormValid = true;
    }

    @Override
    public boolean visit(AbstractAGElementDataDesc elemDesc) {
        if (elemDesc instanceof IFormControlDesc) {
            if (((IFormControlDesc) elemDesc).isFormValid() == false) {
                isFormValid = false;
            }
            return false;
        }
        return false;
    }

    public boolean isFormValid() {
        return isFormValid;
    }

    public void setFormValid(boolean formValid) {
        isFormValid = formValid;
    }
}
