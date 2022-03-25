package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.TableIdentifierDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.ItemPath;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser.ItemPathStructureXmlParser;

import static com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes.CREATE_RESPONSE_PATH;
import static com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes.FIELD_PATH;

public class AGTableIdentifierStructureXmlParser extends AbstractStructureXmlParser<TableIdentifierDataDesc> {

    @Override
    protected boolean parseNodeValue(TableIdentifierDataDesc desc) {
        return true;
    }

    @Override
    protected TableIdentifierDataDesc createDescriptor(String id) {
        return new TableIdentifierDataDesc();
    }

    @Override
    protected boolean parseNodeAttribute(TableIdentifierDataDesc descriptor, String id, String value) {
        return true;
    }

    @Override
    protected String getStructureRootNodeName() {
        return AGXmlNodes.IDENTIFIER; //TODO czy to ważne???
    }

    @Override
    protected void proceedParseStructure(String nodeName, TableIdentifierDataDesc desc) {
        if (nodeName.startsWith(FIELD_PATH)) {
            ItemPathStructureXmlParser parser = (ItemPathStructureXmlParser) StructureXmlParsersFactory.getStructureParser(nodeName);
            ItemPath itemPath = parser.parseStructure();
            desc.setFieldPath(itemPath);
        } else if (nodeName.startsWith(CREATE_RESPONSE_PATH)) {
            ItemPathStructureXmlParser parser = (ItemPathStructureXmlParser) StructureXmlParsersFactory.getStructureParser(nodeName);
            ItemPath itemPath = parser.parseStructure();
            desc.setCreateResponsePath(itemPath);
        }
    }

    private String getValue() {
        return AGXmlParserHelper.loadXmlNodeValue();
    }
}
