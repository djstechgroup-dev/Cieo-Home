package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;

import com.kinetise.data.descriptors.datadescriptors.AbstractAGTemplateDataDesc;

public class LoadingDataDesc extends AbstractAGTemplateDataDesc {

    @Override
    public AbstractAGTemplateDataDesc createInstance() {
        return new LoadingDataDesc();
    }

    @Override
    public LoadingDataDesc copy() {
        return (LoadingDataDesc) super.copy();
    }
}
