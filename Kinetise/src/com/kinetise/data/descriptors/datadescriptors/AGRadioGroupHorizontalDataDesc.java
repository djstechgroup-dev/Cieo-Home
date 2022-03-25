package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.types.AGLayoutType;

public class AGRadioGroupHorizontalDataDesc extends AbstractAGRadioGroupDataDesc {

    public AGRadioGroupHorizontalDataDesc(String id) {
        super(id, AGLayoutType.HORIZONTAL);
    }

    @Override
    public AGRadioGroupHorizontalDataDesc createInstance() {
        return new AGRadioGroupHorizontalDataDesc(getId());
    }
}


