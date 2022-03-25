package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGRadioGroupTableDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGRadioGroupTableStructureXmlParser extends
		AbstractAGRadioGroupStructureXmlParser {

	private static final String NODE_NAME = AGXmlNodes.CONTROL_RADIO_GROUP_TABLE;

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}

	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		AGRadioGroupTableDataDesc desc = new AGRadioGroupTableDataDesc(id);
		AGRadioButtonStructureXmlParser.setLastCreatedRadioGroup(desc);
		return desc;
	}

}
