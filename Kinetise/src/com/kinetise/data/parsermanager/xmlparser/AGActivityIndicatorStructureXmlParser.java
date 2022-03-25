package com.kinetise.data.parsermanager.xmlparser;

import com.kinetise.data.descriptors.datadescriptors.AGLoadingDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractAGViewStructureXmlParser;

public class AGActivityIndicatorStructureXmlParser extends AbstractAGViewStructureXmlParser {

    @Override
    protected AGLoadingDataDesc createDescriptor(String id) {
        return new AGLoadingDataDesc(id);
    }

    @Override
    protected String getStructureRootNodeName() {
        return AGXmlNodes.CONTROL_ACTIVITY_INDICATOR;
    }


}
