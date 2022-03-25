package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGRadioGroupDataDesc;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.FormControlStructureParser;

public abstract class AbstractAGRadioGroupStructureXmlParser extends
        AbstractAGContainerStructureXmlPraser {

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {

        AbstractAGRadioGroupDataDesc desc = (AbstractAGRadioGroupDataDesc) descriptor;

        if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } if(FormControlStructureParser.parseNodeAttribute(desc,id,value))
            return true;



        return false;
    }
}
