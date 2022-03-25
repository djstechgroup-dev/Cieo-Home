package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.types.AGLayoutType;

public class AGRadioGroupTableDataDesc extends AbstractAGRadioGroupDataDesc {

	public AGRadioGroupTableDataDesc(String id) {
		super(id, AGLayoutType.TABLE);
	}

	@Override
	public AGRadioGroupTableDataDesc createInstance() {
		return new AGRadioGroupTableDataDesc(getId());
	}
}
