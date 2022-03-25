package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGCheckBoxDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.FormControlStructureParser;

public class AGCheckBoxStructureXmlParser extends
        AbstractAGCompoundButtonStructureXmlParser {
    private static final String NODE_NAME = AGXmlNodes.CONTROL_CHECKBOX;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGCheckBoxDataDesc(id);
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
        AGCheckBoxDataDesc desc = (AGCheckBoxDataDesc) descriptor;
        if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else if(FormControlStructureParser.parseNodeAttribute(desc,id,value)){
            return true;
        }
        return false;
    }

}
