package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;

public class AGLoadingDataDesc extends AbstractAGViewDataDesc {
    private final static String ID = "loading";

    public AGLoadingDataDesc() {
        super(ID);
    }

    public AGLoadingDataDesc(String id) {
        super(id);
    }

    @Override
    public AbstractAGElementDataDesc createInstance() {
        return new AGLoadingDataDesc(getId());
    }

    @Override
    public void resolveVariables() {
    }
}
