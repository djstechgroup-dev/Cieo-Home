package com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser;

import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.NamespaceElement;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGNamespaceXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractStructureXmlParser;

import java.security.InvalidParameterException;

public class NamespaceStructureXmlParser extends
        AbstractStructureXmlParser<NamespaceElement> {

    private final static String NODE_NAME = AGXmlNodes.NAMESPACE;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName, NamespaceElement desc) {
        throw new InvalidParameterException(String.format(
                "Unexpected node '%s' in '%s' structure", nodeName, NODE_NAME));
    }

    @Override
    protected NamespaceElement createDescriptor(String id) {
        return new NamespaceElement();
    }

    @Override
    protected boolean parseNodeAttribute(NamespaceElement descriptor, String id, String value) {

        if (id.equals(AGNamespaceXmlAttributes.PREFIX)) {
            descriptor.setPrefix(value);
            return true;
        }

        return false;
    }

    @Override
    protected boolean parseNodeValue(NamespaceElement desc) {
        String string = AGXmlParserHelper.loadXmlNodeValue();
        desc.setNamespace(string);

        return true;
    }
}
