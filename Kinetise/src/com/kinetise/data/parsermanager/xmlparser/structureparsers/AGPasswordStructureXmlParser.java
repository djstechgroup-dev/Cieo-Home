package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGPasswordDataDesc;
import com.kinetise.data.descriptors.types.EncryptionType;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGTextInputXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGPasswordStructureXmlParser extends
		AGTextInputStructureXmlParser {

	public final static String NODE_NAME = AGXmlNodes.CONTROL_PASSWORD;
	
	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		return new AGPasswordDataDesc(id);
	}

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}

	@Override
	protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
		AGPasswordDataDesc desc = (AGPasswordDataDesc) descriptor;
		if (super.parseNodeAttribute(descriptor, id, value)) {
			return true;
		} else if (id.equals(AGTextInputXmlAttributes.ENCRYPTION_TYPE)) {
			desc.setEncryptionType(EncryptionType.parseFromString(value));
			return true;
		}
		return false;
	}

}
