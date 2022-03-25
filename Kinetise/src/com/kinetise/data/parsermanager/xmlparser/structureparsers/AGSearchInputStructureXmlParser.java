package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.MultiActionDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGSearchInputDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGSearchInputAttributes;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

import static com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper.getStringOrNullIfNone;
import static com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser.createMultiAction;

public class AGSearchInputStructureXmlParser extends AGTextInputStructureXmlParser {

	private final static String NODE_NAME = AGXmlNodes.CONTROL_SEARCH_INPUT;
	
	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		return new AGSearchInputDataDesc(id);
	}

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}

	@Override
	protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
		AGSearchInputDataDesc desc = (AGSearchInputDataDesc) descriptor;
		if (super.parseNodeAttribute(descriptor, id, value)) {
			return true;
		} else if (id.equals(AGSearchInputAttributes.ON_ACCEPT)) {
			value = getStringOrNullIfNone(value);
			MultiActionDataDesc multiAction = createMultiAction(value, desc);
			desc.setOnAcceptActionDesc(multiAction);
			return true;
		}
		return false;
	}
}
