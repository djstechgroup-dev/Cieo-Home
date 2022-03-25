package com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.ErrorDataDesc;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractAGViewStructureXmlParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractStructureXmlParser;

public class ErrorStructureXmlParser extends AbstractStructureXmlParser<ErrorDataDesc> {

    private final static String NODE_NAME = AGXmlNodes.ERROR;

    @Override
    protected boolean parseNodeValue(ErrorDataDesc desc) {
        return true;
    }

    @Override
    protected ErrorDataDesc createDescriptor(String id) {
        return new ErrorDataDesc();
    }

    @Override
    protected boolean parseNodeAttribute(ErrorDataDesc descriptor, String id, String value) {
        //dla deskryptora typ layoutu do zawsze vertical wg. Adama
        return false;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName, ErrorDataDesc desc) {

        AbstractAGViewStructureXmlParser parser = (AbstractAGViewStructureXmlParser) StructureXmlParsersFactory
                .getStructureParser(nodeName);

        AbstractAGViewDataDesc element = parser.parseStructure();

        desc.addControl(element);
    }
}
