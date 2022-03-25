package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.types.AGLayoutType;

public class AGContainerHorizontalDataDesc extends AbstractAGContainerDataDesc {

	public AGContainerHorizontalDataDesc(String id) {
		super(id, AGLayoutType.HORIZONTAL);
	}

	@Override
	public AbstractAGElementDataDesc createInstance() {
		return new AGContainerHorizontalDataDesc(getId());
	}
}
