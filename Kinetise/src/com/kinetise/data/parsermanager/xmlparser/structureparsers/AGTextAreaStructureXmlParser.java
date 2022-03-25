package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGTextAreaDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGTextAreaXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGTextAreaStructureXmlParser extends
		AGTextInputStructureXmlParser {

	private final static String NODE_NAME = AGXmlNodes.CONTROL_TEXTAREA;
	
	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		return new AGTextAreaDataDesc(id);
	}

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}

	@Override
	protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
		AGTextAreaDataDesc desc = (AGTextAreaDataDesc) descriptor;
		
		if(super.parseNodeAttribute(descriptor, id, value)){
			return true;
		} else if(id.equals(AGTextAreaXmlAttributes.ROWS)){
			desc.setRows(AGXmlParserHelper.getIntOrNegativeIfNone(value));
			return true;
		}
		
		return false;
	}



}
