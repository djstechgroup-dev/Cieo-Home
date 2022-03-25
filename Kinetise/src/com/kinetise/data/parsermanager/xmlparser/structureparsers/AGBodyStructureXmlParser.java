package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AGBodyDataDesc;
import com.kinetise.data.descriptors.AbstractAGSectionDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGBodyStructureXmlParser extends
        AbstractAGSectionStructureXmlParser {

    private static final String NODE_NAME = AGXmlNodes.BODY;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected AbstractAGSectionDataDesc createDescriptor(String id) {

        return new AGBodyDataDesc();
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGSectionDataDesc descriptor, String id, String value) {
        if (!super.parseNodeAttribute(descriptor, id, value)) {
            throw new IllegalStateException(String.format(
                    "Cannot find definition to parse '%s' attribute",
                    id));
        }

        return true;
    }

    @Override
    protected boolean parseNodeValue(AbstractAGSectionDataDesc desc) {
        return true;
    }

}
