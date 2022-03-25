package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGRadioGroupVerticalDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGRadioGroupVerticalStructureXmlParser extends
		AbstractAGRadioGroupStructureXmlParser {

	private static final String NODE_NAME = AGXmlNodes.CONTROL_RADIO_GROUP_VERTICAL;

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}

	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		AGRadioGroupVerticalDataDesc desc = new AGRadioGroupVerticalDataDesc(id);
		AGRadioButtonStructureXmlParser.setLastCreatedRadioGroup(desc);
		return desc;
	}

}
