package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AGHeaderDataDesc;
import com.kinetise.data.descriptors.AbstractAGSectionDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGHeaderStrucutreXmlParser extends AbstractAGSectionStructureXmlParser {

    private static final String NODE_NAME = AGXmlNodes.HEADER;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected AbstractAGSectionDataDesc createDescriptor(String id) {

        return new AGHeaderDataDesc();
    }

    @Override
    protected boolean parseNodeValue(AbstractAGSectionDataDesc desc) {
        return true;
    }

}
