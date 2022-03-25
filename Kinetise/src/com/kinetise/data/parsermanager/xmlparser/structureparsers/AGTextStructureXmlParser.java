package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGTextDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGTextStructureXmlParser extends AbstractAGTextStructureXmlParser {

    private static final String NODE_NAME = AGXmlNodes.CONTROL_TEXT;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGTextDataDesc(id);
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
        if (super.parseNodeAttribute(descriptor, id, value))
            return true;

        return false;
    }

}
