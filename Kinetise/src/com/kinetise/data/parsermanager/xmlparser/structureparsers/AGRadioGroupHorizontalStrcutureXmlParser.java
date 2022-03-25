package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGRadioGroupHorizontalDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGRadioGroupHorizontalStrcutureXmlParser extends
        AbstractAGRadioGroupStructureXmlParser {


    private static final String NODE_NAME = AGXmlNodes.CONTROL_RADIO_GROUP_HORIZONTAL;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        AGRadioGroupHorizontalDataDesc desc = new AGRadioGroupHorizontalDataDesc(id);
        AGRadioButtonStructureXmlParser.setLastCreatedRadioGroup(desc);
        return desc;
    }
}
