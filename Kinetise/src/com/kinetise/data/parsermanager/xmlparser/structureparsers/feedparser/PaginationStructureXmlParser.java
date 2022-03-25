package com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser;

import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.NextPageToken;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.NextPageUrl;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Pagination;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractStructureXmlParser;

import java.security.InvalidParameterException;

public class PaginationStructureXmlParser extends AbstractStructureXmlParser<Pagination> {

    private final static String NODE_NAME = AGXmlNodes.PAGINATION;

    @Override
    protected boolean parseNodeValue(Pagination desc) {
        return true;
    }

    @Override
    protected Pagination createDescriptor(String id) {
        return new Pagination();
    }

    @Override
    protected boolean parseNodeAttribute(Pagination descriptor, String id, String value) {
        //dla deskryptora typ layoutu do zawsze vertical wg. Adama
        return false;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName, Pagination desc) {
        if (nodeName.equals(AGXmlNodes.NEXT_PAGE_URL)) {

            NextPageUrlStructureXmlParser parser = (NextPageUrlStructureXmlParser) StructureXmlParsersFactory
                    .getStructureParser(nodeName);
            NextPageUrl url = parser.parseStructure();

            desc.setNextPageUrl(url);
        } else if (nodeName.equals(AGXmlNodes.NEXT_PAGE_TOKEN)) {
            NextPageTokenStructureXmlParser parser = (NextPageTokenStructureXmlParser) StructureXmlParsersFactory
                    .getStructureParser(nodeName);
            NextPageToken token = parser.parseStructure();

            desc.setNextPageToken(token);
        } else {
            throw new InvalidParameterException(String.format(
                    "Unexpected node '%s' in '%s' structure", nodeName,
                    NODE_NAME));
        }
    }
}
