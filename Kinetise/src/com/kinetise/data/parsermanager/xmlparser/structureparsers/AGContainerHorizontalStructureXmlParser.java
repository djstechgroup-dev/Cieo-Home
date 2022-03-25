package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGContainerHorizontalDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGContainerHorizontalStructureXmlParser extends
		AbstractAGContainerStructureXmlPraser {

	private static final String NODE_NAME = AGXmlNodes.CONTAINER_HORIZONTAL;

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}

	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		return new AGContainerHorizontalDataDesc(id);
	}

}
