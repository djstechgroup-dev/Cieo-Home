package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGPinchImageDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

/**
 * Created by Kuba Komorowski on 2014-12-11.
 */
public class AGPinchImageStructureXmlParser extends AbstractAGImageStructureXmlParser {

    @Override
    protected String getStructureRootNodeName() {
        return AGXmlNodes.CONTROL_PINCH_IMAGE;
    }

    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGPinchImageDataDesc(id);
    }
}
