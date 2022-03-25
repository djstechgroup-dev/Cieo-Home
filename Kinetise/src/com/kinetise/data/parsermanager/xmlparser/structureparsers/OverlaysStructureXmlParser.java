package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.datadescriptors.OverlayDataDesc;
import com.kinetise.data.parsermanager.xmlparser.OverlayStructureXmlParser;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class OverlaysStructureXmlParser extends
        AbstractStructureXmlParser<Map<String, OverlayDataDesc>> {
    @Override
    protected boolean parseNodeValue(Map<String, OverlayDataDesc> desc) {
        return true;
    }

    @Override
    protected Map<String, OverlayDataDesc> createDescriptor(String id) {
        return new HashMap<>();
    }

    @Override
    protected boolean parseNodeAttribute(Map<String, OverlayDataDesc> descriptor, String id, String value) {
        return true;
    }

    @Override
    protected String getStructureRootNodeName() {
        return AGXmlNodes.OVERLAYS;
    }

    @Override
    protected void proceedParseStructure(String nodeName, Map<String, OverlayDataDesc> desc) {
        OverlayStructureXmlParser parser = (OverlayStructureXmlParser) StructureXmlParsersFactory
                .getStructureParser(nodeName);

        if (nodeName.equals(AGXmlNodes.OVERLAY)) {
            OverlayDataDesc overlayDesc = parser.parseStructure();
            desc.put(overlayDesc.getId(), overlayDesc);
        } else {
            throw new InvalidParameterException(String.format(
                    "Unexpected node '%s' in '%s' structure", nodeName,
                    AGXmlNodes.OVERLAYS));
        }
    }
}
