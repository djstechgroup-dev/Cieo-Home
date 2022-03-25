package com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser;

import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Field;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGXmlCommonAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractStructureXmlParser;

import java.security.InvalidParameterException;

public class FieldStructureXmlParser extends AbstractStructureXmlParser<Field> {

	private final static String NODE_NAME = AGXmlNodes.FIELD;

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}

	@Override
	protected void proceedParseStructure(String nodeName, Field desc) {
		throw new InvalidParameterException(String.format(
				"Unexpected node '%s' in '%s' structure", nodeName, NODE_NAME));
	}

	@Override
	protected Field createDescriptor(String id) {
		return new Field(id);
	}

	@Override
	protected boolean parseNodeAttribute(Field descriptor, String id, String value) {

        return id.equals(AGXmlCommonAttributes.ID);
    }

	@Override
	protected boolean parseNodeValue(Field desc) {

		String xpath = AGXmlParserHelper.loadXmlNodeValue();
		desc.setAGXpath(xpath);

		return true;
	}

}
