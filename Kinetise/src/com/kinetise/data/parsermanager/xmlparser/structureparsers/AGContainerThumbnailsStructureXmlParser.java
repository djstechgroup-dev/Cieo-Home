package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGContainerThumbnailsDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGContainerThumbnailsStructureXmlParser extends
		AbstractAGContainerStructureXmlPraser {

	private static final String NODE_NAME = AGXmlNodes.CONTAINER_THUMBNAILS;

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}
	
	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		return new AGContainerThumbnailsDataDesc(id);
	}
}
