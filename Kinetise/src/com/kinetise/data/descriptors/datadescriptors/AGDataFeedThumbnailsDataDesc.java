package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.types.AGLayoutType;

public class AGDataFeedThumbnailsDataDesc extends AbstractAGDataFeedDataDesc {

    public AGDataFeedThumbnailsDataDesc(String id) {
        super(id, AGLayoutType.THUMBNAILS);
    }

    @Override
    public AGDataFeedThumbnailsDataDesc createInstance() {
        return new AGDataFeedThumbnailsDataDesc(getId());
    }

}
