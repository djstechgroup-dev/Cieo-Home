package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGGalleryDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGGalleryStructureXmlParser extends AbstractAGDataFeedViewStructureXmlParser {

    private final static String NODE_NAME = AGXmlNodes.CONTROL_GALLERY;

    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGGalleryDataDesc(id);
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

}
