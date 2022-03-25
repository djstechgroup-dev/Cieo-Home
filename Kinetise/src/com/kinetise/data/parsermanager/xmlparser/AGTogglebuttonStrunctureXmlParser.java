package com.kinetise.data.parsermanager.xmlparser;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGToggleButtonDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AGCheckBoxStructureXmlParser;

public class AGTogglebuttonStrunctureXmlParser extends AGCheckBoxStructureXmlParser {
    private static final String NODE_NAME = AGXmlNodes.CONTROL_TOGGLEBUTTON;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGToggleButtonDataDesc(id);
    }
}
