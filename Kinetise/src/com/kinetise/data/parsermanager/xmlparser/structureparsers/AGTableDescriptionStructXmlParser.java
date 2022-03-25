package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.InitDescriptionDataDesc;
import com.kinetise.data.descriptors.SynchronizationDescriptionDataDesc;
import com.kinetise.data.descriptors.TableDescriptionDataDesc;
import com.kinetise.data.descriptors.TableIdentifierDataDesc;
import com.kinetise.data.descriptors.TableIdentifiersDataDesc;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

import static com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes.IDENTIFIER;
import static com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes.IDENTIFIERS;

public class AGTableDescriptionStructXmlParser extends
        AbstractStructureXmlParser<TableDescriptionDataDesc> {
    private static final String NODE_NAME = AGXmlNodes.TABLE;

    @Override
    protected boolean parseNodeValue(TableDescriptionDataDesc desc) {
        return true;
    }

    @Override
    protected TableDescriptionDataDesc createDescriptor(String id) {
        return new TableDescriptionDataDesc();
    }

    @Override
    protected boolean parseNodeAttribute(TableDescriptionDataDesc descriptor, String id, String value) {
        return true;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName, TableDescriptionDataDesc desc) {
        if (nodeName.equals(AGXmlNodes.LS_NAME)) {
            desc.setTableName(getValue());
        } else if (nodeName.equals(AGXmlNodes.INIT)) {
            AGInitDescriptionStructXmlParser tableParser = new AGInitDescriptionStructXmlParser();
            InitDescriptionDataDesc init = tableParser.parseStructure();
            desc.setInitDescriptionDataDesc(init);
        } else if (nodeName.equals(AGXmlNodes.SYNCHRONIZATION)) {
            AGsynchronizationDescriptionStructXmlParser synchronizationParser = new AGsynchronizationDescriptionStructXmlParser();
            SynchronizationDescriptionDataDesc synchronizationDescriptionDataDesc = synchronizationParser.parseStructure();
            desc.setSynchronizationDescriptionDataDesc(synchronizationDescriptionDataDesc);
        } else if (nodeName.equals(IDENTIFIERS)) {
            AGTableIdentifiersStructureXmlParser parser = new AGTableIdentifiersStructureXmlParser();
            TableIdentifiersDataDesc tableIdentifierDataDesc = parser.parseStructure();
            desc.setTableIdentifiersDataDesc(tableIdentifierDataDesc);
        }
    }

    private String getValue() {
        return AGXmlParserHelper.loadXmlNodeValue();
    }

}
