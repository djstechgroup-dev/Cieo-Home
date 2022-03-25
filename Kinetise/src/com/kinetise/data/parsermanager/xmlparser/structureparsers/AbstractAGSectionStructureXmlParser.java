package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGSectionDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;

public abstract class AbstractAGSectionStructureXmlParser extends
        AbstractStructureXmlParser<AbstractAGSectionDataDesc> {

    @Override
    protected boolean parseNodeAttribute(AbstractAGSectionDataDesc descriptor, String id, String value) {
        return false;
    }

    @Override
    protected void proceedParseStructure(String nodeName,
                                         AbstractAGSectionDataDesc desc) {

        AbstractAGViewDataDesc viewDataDesc = (AbstractAGViewDataDesc) StructureXmlParsersFactory.getStructureParser(nodeName).parseStructure();
        desc.addControl(viewDataDesc);
    }


}
