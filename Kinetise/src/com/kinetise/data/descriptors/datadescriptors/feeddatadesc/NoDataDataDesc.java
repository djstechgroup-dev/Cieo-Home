package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;

import com.kinetise.data.descriptors.datadescriptors.AbstractAGTemplateDataDesc;

public class NoDataDataDesc extends AbstractAGTemplateDataDesc {

    @Override
    public AbstractAGTemplateDataDesc createInstance() {
        return new NoDataDataDesc();
    }

    @Override
    public NoDataDataDesc copy() {
        return (NoDataDataDesc) super.copy();
    }
}
