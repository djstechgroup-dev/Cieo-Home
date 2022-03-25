package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGRadioButtonDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGRadioGroupDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGRadioButtonXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGRadioButtonStructureXmlParser extends
        AbstractAGCompoundButtonStructureXmlParser {

    private static AbstractAGRadioGroupDataDesc mLastCreatedRadioGroup;
    private static final String NODE_NAME = AGXmlNodes.CONTROL_RADIO_BUTTON;

    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        AGRadioButtonDataDesc desc = new AGRadioButtonDataDesc(id);
        desc.setParentRadioGroup(mLastCreatedRadioGroup);
        return desc;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {

        AGRadioButtonDataDesc desc = (AGRadioButtonDataDesc) descriptor;

        if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        }  else if (id.equals(AGRadioButtonXmlAttributes.VALUE)) {
            desc.setValue(value.toString());
            return true;
        }

        return false;
    }

    public static void setLastCreatedRadioGroup(AbstractAGRadioGroupDataDesc lastCreatedRadioGroup) {
        mLastCreatedRadioGroup = lastCreatedRadioGroup;
    }
}
