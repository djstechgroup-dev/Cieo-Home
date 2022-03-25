package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.types.AGLayoutType;

public class AGDataFeedHorizontalDataDesc extends AbstractAGDataFeedDataDesc {

	public AGDataFeedHorizontalDataDesc(String id) {
		super(id, AGLayoutType.HORIZONTAL);
	}

	@Override
	public AGDataFeedHorizontalDataDesc createInstance() {
		return new AGDataFeedHorizontalDataDesc(getId());
	}

}
