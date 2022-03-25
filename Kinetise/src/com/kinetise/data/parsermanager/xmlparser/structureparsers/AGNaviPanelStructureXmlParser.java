package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AGNaviPanelDataDesc;
import com.kinetise.data.descriptors.AbstractAGSectionDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGNaviPanelStructureXmlParser extends
        AbstractAGSectionStructureXmlParser {

    private static final String NODE_NAME = AGXmlNodes.NAVIPANEL;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected AbstractAGSectionDataDesc createDescriptor(String id) {

        return new AGNaviPanelDataDesc();
    }

    @Override
    protected boolean parseNodeValue(AbstractAGSectionDataDesc desc) {
        return true;
    }

}
