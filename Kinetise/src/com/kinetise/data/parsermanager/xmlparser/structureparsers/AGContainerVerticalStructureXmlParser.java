package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGContainerVerticalDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGContainerVerticalStructureXmlParser extends
		AbstractAGContainerStructureXmlPraser {

	private static final String NODE_NAME = AGXmlNodes.CONTAINER_VERTICAL;

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}
	
	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		return new AGContainerVerticalDataDesc(id);
	}

}
