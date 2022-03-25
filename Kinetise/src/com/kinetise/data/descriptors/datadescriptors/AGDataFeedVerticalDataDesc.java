package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.types.AGLayoutType;

public class AGDataFeedVerticalDataDesc extends AbstractAGDataFeedDataDesc {

    public AGDataFeedVerticalDataDesc(String id) {
        super(id, AGLayoutType.VERTICAL);
    }

    @Override
    public AGDataFeedVerticalDataDesc createInstance() {
        return new AGDataFeedVerticalDataDesc(getId());
    }

}
