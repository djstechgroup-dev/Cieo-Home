package com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser;

import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.NamespaceElement;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Namespaces;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractStructureXmlParser;

import java.security.InvalidParameterException;

public class NamespacesStructureXmlParser extends
        AbstractStructureXmlParser<Namespaces> {

    private final static String NODE_NAME = AGXmlNodes.NAMESPACES;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName, Namespaces desc) {

        if (nodeName.equals(AGXmlNodes.NAMESPACE)) {

            NamespaceStructureXmlParser parser = (NamespaceStructureXmlParser) StructureXmlParsersFactory
                    .getStructureParser(nodeName);
            NamespaceElement namespace = parser.parseStructure();

            desc.add(namespace);
        } else {
            throw new InvalidParameterException(String.format(
                    "Unexpected node '%s' in '%s' structure", nodeName,
                    NODE_NAME));
        }
    }

    @Override
    protected Namespaces createDescriptor(String id) {
        return new Namespaces();
    }

    @Override
    protected boolean parseNodeAttribute(Namespaces descriptor, String id, String value) {
//        if (id.equals(AGNamespacesXmlAttributes.FORMAT)) {
//            AGFeedInputFormatType format = AGXmlParserHelper
//                    .getFeedInputFormatFromString(value);
//            descriptor.setFormat(format);
//            return true;
//        }

        return false;
    }

    @Override
    protected boolean parseNodeValue(Namespaces desc) {
        return true;
    }
}
