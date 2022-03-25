package com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.NoDataDataDesc;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractAGViewStructureXmlParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractStructureXmlParser;

public class NoDataStructureXmlParser extends AbstractStructureXmlParser<NoDataDataDesc> {

    private final static String NODE_NAME = AGXmlNodes.NO_DATA;

    @Override
    protected boolean parseNodeValue(NoDataDataDesc desc) {
        return true;
    }

    @Override
    protected NoDataDataDesc createDescriptor(String id) {
        return new NoDataDataDesc();
    }

    @Override
    protected boolean parseNodeAttribute(NoDataDataDesc descriptor, String id, String value) {
        //dla deskryptora typ layoutu do zawsze vertical wg. Adama
        return false;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName, NoDataDataDesc desc) {

        AbstractAGViewStructureXmlParser parser = (AbstractAGViewStructureXmlParser) StructureXmlParsersFactory
                .getStructureParser(nodeName);

        AbstractAGViewDataDesc element = parser.parseStructure();

        desc.addControl(element);
    }
}
