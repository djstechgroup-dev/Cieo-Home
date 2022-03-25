package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.TableIdentifierDataDesc;
import com.kinetise.data.descriptors.TableIdentifiersDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.ItemPath;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser.ItemPathStructureXmlParser;

import static com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes.CREATE_RESPONSE_PATH;
import static com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes.FIELD_PATH;
import static com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes.IDENTIFIER;
import static com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes.IDENTIFIERS;

public class AGTableIdentifiersStructureXmlParser extends AbstractStructureXmlParser<TableIdentifiersDataDesc> {

    @Override
    protected boolean parseNodeValue(TableIdentifiersDataDesc desc) {
        return true;
    }

    @Override
    protected TableIdentifiersDataDesc createDescriptor(String id) {
        return new TableIdentifiersDataDesc();
    }

    @Override
    protected boolean parseNodeAttribute(TableIdentifiersDataDesc descriptor, String id, String value) {
        return true;
    }

    @Override
    protected String getStructureRootNodeName() {
        return AGXmlNodes.IDENTIFIERS; //TODO czy to ważne???
    }

    @Override
    protected void proceedParseStructure(String nodeName, TableIdentifiersDataDesc desc) {
        if (nodeName.startsWith(IDENTIFIER)) {
            AGTableIdentifierStructureXmlParser parser = new AGTableIdentifierStructureXmlParser();
            TableIdentifierDataDesc tableIdentifierDataDesc = parser.parseStructure();
            desc.addIdentifier(tableIdentifierDataDesc);
        }
    }

    private String getValue() {
        return AGXmlParserHelper.loadXmlNodeValue();
    }
}
