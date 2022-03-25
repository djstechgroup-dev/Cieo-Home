package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.types.AGLayoutType;

public class AGRadioGroupVerticalDataDesc extends AbstractAGRadioGroupDataDesc {

    public AGRadioGroupVerticalDataDesc(String id) {
        super(id, AGLayoutType.VERTICAL);
    }

    @Override
    public AGRadioGroupVerticalDataDesc createInstance() {
        return new AGRadioGroupVerticalDataDesc(getId());
    }
}
