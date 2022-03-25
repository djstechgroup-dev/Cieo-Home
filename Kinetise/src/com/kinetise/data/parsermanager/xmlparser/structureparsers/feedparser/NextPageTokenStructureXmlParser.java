package com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser;

import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.NextPageToken;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGPaginationXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractStructureXmlParser;

import java.security.InvalidParameterException;

public class NextPageTokenStructureXmlParser extends AbstractStructureXmlParser<NextPageToken> {

    private final static String NODE_NAME = AGXmlNodes.NEXT_PAGE_TOKEN;

    @Override
    protected boolean parseNodeValue(NextPageToken desc) {

        String token = AGXmlParserHelper.loadXmlNodeValue();
        desc.setToken(token);

        return true;
    }

    @Override
    protected NextPageToken createDescriptor(String url) {
        return new NextPageToken();
    }

    @Override
    protected boolean parseNodeAttribute(NextPageToken descriptor, String id, String value) {
        if (id.equals(AGPaginationXmlAttributes.PARAM)) {
            descriptor.setParam(value);
            return true;
        }

        return false;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName, NextPageToken desc) {
            throw new InvalidParameterException(String.format(
                    "Unexpected node '%s' in '%s' structure", nodeName,
                    NODE_NAME));
    }
}
