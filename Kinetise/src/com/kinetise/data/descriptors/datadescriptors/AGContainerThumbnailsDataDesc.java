package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.types.AGLayoutType;

public class AGContainerThumbnailsDataDesc extends AbstractAGContainerDataDesc {

    public AGContainerThumbnailsDataDesc(String id) {
        super(id, AGLayoutType.THUMBNAILS);
    }

    @Override
    public AGContainerThumbnailsDataDesc createInstance() {
        return new AGContainerThumbnailsDataDesc(String.valueOf(getId()));
    }

}
