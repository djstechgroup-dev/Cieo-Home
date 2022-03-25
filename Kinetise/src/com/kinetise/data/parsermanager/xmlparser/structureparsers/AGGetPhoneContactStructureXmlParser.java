package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGGetPhoneContactDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGGetPhoneContactXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGTextInputXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.FormControlStructureParser;

public class AGGetPhoneContactStructureXmlParser extends
        AGButtonStructureXmlParser {
    private static final String NODE_NAME = AGXmlNodes.CONTROL_GET_PHONE_CONTACT;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGGetPhoneContactDataDesc(id);
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
        AGGetPhoneContactDataDesc desc = (AGGetPhoneContactDataDesc) descriptor;
        if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else if (FormControlStructureParser.parseNodeAttribute(desc,id,value)) {
            return true;
        } else if (id.equals(AGGetPhoneContactXmlAttributes.CONTACT_INFO)) {
            //TODO desc.set(AGCodeScannerDataDesc.parseCodeTypes(value));
            return true;
        } else if (id.equals(AGTextInputXmlAttributes.WATERMARK)){
            desc.setWatermark(AGXmlActionParser.createVariable(value, desc));
            return true;
        } else if (id.equals(AGTextInputXmlAttributes.WATERMARK_COLOR)){
            desc.setWatermarkColor(AGXmlParserHelper.getColorFromHex(value));
            return true;
        }
        return false;
    }

}
