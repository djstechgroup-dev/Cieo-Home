package com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser;

import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Field;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.UsingFields;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractStructureXmlParser;

import java.security.InvalidParameterException;

public class UsingFieldsStructureXmlParser extends
        AbstractStructureXmlParser<UsingFields> {

    private final static String NODE_NAME = AGXmlNodes.USING_FIELDS;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName, UsingFields desc) {

        if (nodeName.equals(AGXmlNodes.FIELD)) {

            FieldStructureXmlParser parser = (FieldStructureXmlParser) StructureXmlParsersFactory
                    .getStructureParser(nodeName);
            Field field = parser.parseStructure();

            desc.addField(field);
        } else {
            throw new InvalidParameterException(String.format(
                    "Unexpected node '%s' in '%s' structure", nodeName,
                    NODE_NAME));
        }
    }

    protected UsingFields createDescriptor(String id) {
        return new UsingFields();
    }

    @Override
    protected boolean parseNodeAttribute(UsingFields descriptor, String id, String value) {

//        if (id.equals(AGUsingFieldsXmlAttributes.FORMAT)) {
//            AGFeedInputFormatType format = AGXmlParserHelper
//                    .getFeedInputFormatFromString(value);
//            descriptor.setFeedInputFormatType(format);
//            return true;
//        }

        return false;
    }

    @Override
    protected boolean parseNodeValue(UsingFields desc) {
        return true;
    }
}
