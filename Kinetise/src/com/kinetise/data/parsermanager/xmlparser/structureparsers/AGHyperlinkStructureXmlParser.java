package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGHyperlinkDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGHyperlinkXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGHyperlinkStructureXmlParser extends
        AGTextStructureXmlParser {

    private static final String NODE_NAME = AGXmlNodes.CONTROL_HYPERLINK;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGHyperlinkDataDesc(id);
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
        AGHyperlinkDataDesc desc = (AGHyperlinkDataDesc) descriptor;

        if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else if (id
                .equals(AGHyperlinkXmlAttributes.ACTIVE_COLOR)) {
            int color = AGXmlParserHelper
                    .getColorFromHex(value);
            desc.setActiveColor(color);
            return true;
        }

        return false;
    }

}
