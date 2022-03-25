package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.TableDescriptionDataDesc;
import com.kinetise.data.descriptors.TablesDescriptionDataDesc;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGTablesDescriptionStructXmlParser extends
        AbstractStructureXmlParser<TablesDescriptionDataDesc> {
    private static final String NODE_NAME = AGXmlNodes.TABLES;

    @Override
    protected boolean parseNodeValue(TablesDescriptionDataDesc desc) {
        return true;
    }

    @Override
    protected TablesDescriptionDataDesc createDescriptor(String id) {
        return new TablesDescriptionDataDesc();
    }

    @Override
    protected boolean parseNodeAttribute(
            TablesDescriptionDataDesc descriptor, String id, String value) {
        return true;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName,
                                         TablesDescriptionDataDesc desc) {

        if (nodeName.equals(AGXmlNodes.TABLE)) {
            AGTableDescriptionStructXmlParser tableDescriptionStructXmlParser = new AGTableDescriptionStructXmlParser();
            TableDescriptionDataDesc tableDescriptionDataDesc = tableDescriptionStructXmlParser.parseStructure();
            desc.addTableDescription(tableDescriptionDataDesc);
        }
    }

    private String getValue() {
        return AGXmlParserHelper.loadXmlNodeValue();
    }

}
