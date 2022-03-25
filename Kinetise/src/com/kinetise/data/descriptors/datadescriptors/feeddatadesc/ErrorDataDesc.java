package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;

import com.kinetise.data.descriptors.datadescriptors.AbstractAGTemplateDataDesc;

public class ErrorDataDesc extends AbstractAGTemplateDataDesc {

    @Override
    public AbstractAGTemplateDataDesc createInstance() {
        return new ErrorDataDesc();
    }

    @Override
    public ErrorDataDesc copy() {
        return (ErrorDataDesc) super.copy();
    }
}
