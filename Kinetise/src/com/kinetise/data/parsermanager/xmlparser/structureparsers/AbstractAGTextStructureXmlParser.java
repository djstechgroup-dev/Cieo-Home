package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGTextDataDesc;

public abstract class AbstractAGTextStructureXmlParser extends AbstractAGViewStructureXmlParser {
    private static TextParser textParser = new TextParser();

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
        AGTextDataDesc desc = (AGTextDataDesc) descriptor;

        if (super.parseNodeAttribute(descriptor, id, value))
            return true;
        else if (textParser.parseNodeAttribute(desc.getTextDescriptor(), id, value))
            return true;

        return false;
    }

    @Override
    protected boolean parseNodeValue(AbstractAGViewDataDesc descriptor) {
        AGTextDataDesc desc = (AGTextDataDesc) descriptor;
        return textParser.parseNodeValue(desc.getTextDescriptor());
    }
}
