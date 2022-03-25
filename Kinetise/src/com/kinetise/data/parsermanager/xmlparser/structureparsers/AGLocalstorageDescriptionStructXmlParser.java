package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.LocalStorageDescriptionDataDesc;
import com.kinetise.data.descriptors.TablesDescriptionDataDesc;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGLocalstorageDescriptionStructXmlParser extends
        AbstractStructureXmlParser<LocalStorageDescriptionDataDesc> {
    private static final String NODE_NAME = AGXmlNodes.LOCALSTORAGE;

    @Override
    protected boolean parseNodeValue(LocalStorageDescriptionDataDesc desc) {
        return true;
    }

    @Override
    protected LocalStorageDescriptionDataDesc createDescriptor(String id) {
        return new LocalStorageDescriptionDataDesc();
    }

    @Override
    protected boolean parseNodeAttribute(
            LocalStorageDescriptionDataDesc descriptor, String id, String value) {
        return true;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName,
                                         LocalStorageDescriptionDataDesc desc) {

        if (nodeName.equals(AGXmlNodes.TABLES)) {
            AGTablesDescriptionStructXmlParser agTableDescriptionStructXmlParser = new AGTablesDescriptionStructXmlParser();
            TablesDescriptionDataDesc tablesDescriptionDataDesc = agTableDescriptionStructXmlParser.parseStructure();
            desc.setTablesDescriptionDataDesc(tablesDescriptionDataDesc);

        }

    }

    private String getValue() {
        return AGXmlParserHelper.loadXmlNodeValue();
    }

}
