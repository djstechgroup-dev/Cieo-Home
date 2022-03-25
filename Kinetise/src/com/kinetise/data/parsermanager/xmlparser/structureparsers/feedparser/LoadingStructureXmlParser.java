package com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.LoadingDataDesc;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractAGViewStructureXmlParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractStructureXmlParser;

public class LoadingStructureXmlParser extends AbstractStructureXmlParser<LoadingDataDesc> {

    private final static String NODE_NAME = AGXmlNodes.LOADING;

    @Override
    protected boolean parseNodeValue(LoadingDataDesc desc) {
        return true;
    }

    @Override
    protected LoadingDataDesc createDescriptor(String id) {
        return new LoadingDataDesc();
    }

    @Override
    protected boolean parseNodeAttribute(LoadingDataDesc descriptor, String id, String value) {
        //dla deskryptora typ layoutu do zawsze vertical wg. Adama
        return false;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName, LoadingDataDesc desc) {

        AbstractAGViewStructureXmlParser parser = (AbstractAGViewStructureXmlParser) StructureXmlParsersFactory
                .getStructureParser(nodeName);

        AbstractAGViewDataDesc element = parser.parseStructure();

        desc.addControl(element);
    }
}
