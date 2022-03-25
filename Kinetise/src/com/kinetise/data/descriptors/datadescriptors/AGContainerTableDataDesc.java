package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.types.AGLayoutType;

public class AGContainerTableDataDesc extends AbstractAGContainerDataDesc {

	public AGContainerTableDataDesc(String id) {
		super(id, AGLayoutType.TABLE);
	}

	@Override
	public AbstractAGElementDataDesc createInstance() {
        return new AGContainerTableDataDesc(getId());
	}

}
