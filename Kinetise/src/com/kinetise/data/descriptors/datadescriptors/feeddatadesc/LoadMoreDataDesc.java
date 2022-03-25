package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;

import com.kinetise.data.descriptors.datadescriptors.AbstractAGTemplateDataDesc;

public class LoadMoreDataDesc extends AbstractAGTemplateDataDesc {

    @Override
    public AbstractAGTemplateDataDesc createInstance() {
        return new LoadMoreDataDesc();
    }

    @Override
    public LoadMoreDataDesc copy() {
        return (LoadMoreDataDesc) super.copy();
    }
}
