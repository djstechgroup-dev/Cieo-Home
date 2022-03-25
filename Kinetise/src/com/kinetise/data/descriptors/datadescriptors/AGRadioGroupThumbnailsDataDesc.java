package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.types.AGLayoutType;

public class AGRadioGroupThumbnailsDataDesc extends AbstractAGRadioGroupDataDesc {
	public AGRadioGroupThumbnailsDataDesc(String id) {
		super(id, AGLayoutType.THUMBNAILS);
	}

	@Override
	public AGRadioGroupThumbnailsDataDesc createInstance() {
		return new AGRadioGroupThumbnailsDataDesc(getId());
	}
}
