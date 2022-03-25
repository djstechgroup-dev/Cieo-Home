package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGCustomControlDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGCustomControlStructureXmlParser extends AbstractAGViewStructureXmlParser {

    private static final String NODE_NAME = AGXmlNodes.CONTROL_CUSTOM;
    private final String mControlName;

    public AGCustomControlStructureXmlParser(String controlName) {
        mControlName = controlName;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {

        AGCustomControlDataDesc desc = (AGCustomControlDataDesc) descriptor;

        if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else {
            desc.addAttribute(id, value);
            return true;
        }
    }

    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGCustomControlDataDesc(mControlName, id);
    }

}
