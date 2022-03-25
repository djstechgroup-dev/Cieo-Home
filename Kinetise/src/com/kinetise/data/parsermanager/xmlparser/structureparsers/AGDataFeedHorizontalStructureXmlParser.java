package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGDataFeedHorizontalDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGDataFeedHorizontalStructureXmlParser extends
		AbstractAGDataFeedStructureXmlParser {

	private static final String NODE_NAME = AGXmlNodes.CONTROL_DATA_FEED_HORIZONTAL;

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}

	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		return new AGDataFeedHorizontalDataDesc(id);
	}

}
