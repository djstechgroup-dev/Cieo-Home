package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGRadioGroupThumbnailsDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGRadioGroupThumbnailsStructureXmlParser extends
		AbstractAGRadioGroupStructureXmlParser {

	private static final String NODE_NAME = AGXmlNodes.CONTROL_RADIO_GROUP_THUMBNAILS;

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}

	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		AGRadioGroupThumbnailsDataDesc desc = new AGRadioGroupThumbnailsDataDesc(id);
		AGRadioButtonStructureXmlParser.setLastCreatedRadioGroup(desc);
		return desc;
	}

}
