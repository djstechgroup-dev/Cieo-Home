package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGTextInputDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGTextInputXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.FormControlStructureParser;

public class AGTextInputStructureXmlParser extends
		AbstractAGTextStructureXmlParser {

	private final static String NODE_NAME = AGXmlNodes.CONTROL_TEXTINPUT;
	
	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		return new AGTextInputDataDesc(id);
	}

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}

	@Override
	protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
		AGTextInputDataDesc desc = (AGTextInputDataDesc) descriptor;
		DecoratorParser decoratorParser = new DecoratorParser();
		if (super.parseNodeAttribute(descriptor, id, value)) {
			return true;
		} else if (decoratorParser.parseNodeAttribute(desc.getDecoratorDescriptor(),id, value, desc)) {
			return true;
		} else if (id.equals(AGTextInputXmlAttributes.FORM_VALUE)) {
			desc.setFormValue(AGXmlParserHelper.getStringOrNullIfNone(value));
			return true;
		} else if (FormControlStructureParser.parseNodeAttribute(desc, id, value)) {
			return true;
		} else if (id.equals((AGTextInputXmlAttributes.WATERMARK_COLOR))) {
			desc.setWatermarkColor(AGXmlParserHelper.getColorFromHex(value) + 0xFF000000);
			return true;
		} else if (id.equals(AGTextInputXmlAttributes.KEYBOARD)) {
			desc.setKeyboard(AGXmlParserHelper.getStringOrNullIfNone(value));
			return true;
		}
		return false;
	}

	@Override
	protected boolean parseNodeValue(AbstractAGViewDataDesc descriptor) {
		AGTextInputDataDesc desc = (AGTextInputDataDesc) descriptor;
		String nodeValue = AGXmlParserHelper.loadXmlNodeValue();
		VariableDataDesc variable = AGXmlActionParser.createVariable(nodeValue, descriptor);
		desc.setWatermark(variable);
		return true;
	}
}
