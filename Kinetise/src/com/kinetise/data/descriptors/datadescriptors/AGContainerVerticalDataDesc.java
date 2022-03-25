package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.types.AGLayoutType;

public class AGContainerVerticalDataDesc extends AbstractAGContainerDataDesc {

    public AGContainerVerticalDataDesc(String id) {
        super(id, AGLayoutType.VERTICAL);
    }

    @Override
    public AGContainerVerticalDataDesc createInstance() {
        return new AGContainerVerticalDataDesc(getId());
    }

}
