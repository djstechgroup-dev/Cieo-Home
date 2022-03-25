package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGCodeScannerDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGCodeScannerXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.FormControlStructureParser;

public class AGCodeScannerStructureXmlParser extends
        AGButtonStructureXmlParser {
    private static final String NODE_NAME = AGXmlNodes.CONTROL_CODE_SCANNER;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGCodeScannerDataDesc(id);
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
        AGCodeScannerDataDesc desc = (AGCodeScannerDataDesc) descriptor;
        if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else if (FormControlStructureParser.parseNodeAttribute(desc,id,value)) {
            return true;
        } else if (id.equals(AGCodeScannerXmlAttributes.CODE_TYPE)) {
            desc.setCodeTypes(AGCodeScannerDataDesc.parseCodeTypes(value));
            return true;
        }
        return false;
    }

}
