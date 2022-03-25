package com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.LoadMoreDataDesc;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractAGViewStructureXmlParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractStructureXmlParser;

public class LoadMoreStructureXmlParser extends AbstractStructureXmlParser<LoadMoreDataDesc> {

    private final static String NODE_NAME = AGXmlNodes.LOAD_MORE;

    @Override
    protected boolean parseNodeValue(LoadMoreDataDesc desc) {
        return true;
    }

    @Override
    protected LoadMoreDataDesc createDescriptor(String id) {
        return new LoadMoreDataDesc();
    }

    @Override
    protected boolean parseNodeAttribute(LoadMoreDataDesc descriptor, String id, String value) {
        //dla deskryptora typ layoutu do zawsze vertical wg. Adama
        return false;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName, LoadMoreDataDesc desc) {

        AbstractAGViewStructureXmlParser parser = (AbstractAGViewStructureXmlParser) StructureXmlParsersFactory
                .getStructureParser(nodeName);

        AbstractAGViewDataDesc element = parser.parseStructure();

        desc.addControl(element);
    }

}
