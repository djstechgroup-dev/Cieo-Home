package com.kinetise.data.descriptors.desctriptorvisitors;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;

public class ClearFormValuesVisitor implements IDataDescVisitor {
    @Override
    public boolean visit(AbstractAGElementDataDesc elemDesc) {
        if (elemDesc instanceof IFormControlDesc) {
            ((IFormControlDesc) elemDesc).clearFormValue();
        }
        return false;
    }
}
