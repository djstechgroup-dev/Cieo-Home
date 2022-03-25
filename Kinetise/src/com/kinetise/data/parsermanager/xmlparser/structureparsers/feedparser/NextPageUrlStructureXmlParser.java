package com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser;

import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.NextPageUrl;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractStructureXmlParser;

import java.security.InvalidParameterException;

public class NextPageUrlStructureXmlParser extends AbstractStructureXmlParser<NextPageUrl> {

    private final static String NODE_NAME = AGXmlNodes.NEXT_PAGE_URL;

    @Override
    protected boolean parseNodeValue(NextPageUrl desc) {

        String url = AGXmlParserHelper.loadXmlNodeValue();
        desc.setNextPageUrl(url);

        return true;
    }

    @Override
    protected NextPageUrl createDescriptor(String id) {
        return new NextPageUrl();
    }

    @Override
    protected boolean parseNodeAttribute(NextPageUrl descriptor, String id, String value) {
        //dla deskryptora typ layoutu do zawsze vertical wg. Adama
        return false;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName, NextPageUrl desc) {
        throw new InvalidParameterException(String.format(
                "Unexpected node '%s' in '%s' structure", nodeName,
                NODE_NAME));
    }
}
