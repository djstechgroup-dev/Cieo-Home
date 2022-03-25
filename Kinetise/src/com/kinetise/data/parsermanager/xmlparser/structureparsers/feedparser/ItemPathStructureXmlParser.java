package com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser;

import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.ItemPath;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractStructureXmlParser;

import java.security.InvalidParameterException;

import static com.kinetise.data.parsermanager.xmlparser.structureparsers.AGPasswordStructureXmlParser.NODE_NAME;

public class ItemPathStructureXmlParser extends AbstractStructureXmlParser<ItemPath> {

	private final String mNodeName;

	public ItemPathStructureXmlParser(String nodeName) {
		mNodeName = nodeName;
	}

	@Override
	protected String getStructureRootNodeName() {
		return mNodeName;
	}

	@Override
	protected void proceedParseStructure(String nodeName, ItemPath desc) {
		throw new InvalidParameterException(String.format(
				"Unexpected node '%s' in '%s' structure", nodeName, mNodeName));
	}

	@Override
	protected ItemPath createDescriptor(String id) {
		return new ItemPath();
	}

	@Override
	protected boolean parseNodeAttribute(ItemPath descriptor, String id, String value) {
		return false;
	}

	@Override
	protected boolean parseNodeValue(ItemPath desc) {
		String text = AGXmlParserHelper.loadXmlNodeValue();
		desc.setAGXpath(text);
		return true;
	}
}
