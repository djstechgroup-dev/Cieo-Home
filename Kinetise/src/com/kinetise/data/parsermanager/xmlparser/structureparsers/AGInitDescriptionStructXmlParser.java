package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.InitDescriptionDataDesc;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;

public class AGInitDescriptionStructXmlParser extends
        AbstractStructureXmlParser<InitDescriptionDataDesc> {
    private static final String NODE_NAME = "init";
    private static final String SOURCE = "src";


    @Override
    protected boolean parseNodeValue(InitDescriptionDataDesc desc) {
        return true;
    }

    @Override
    protected InitDescriptionDataDesc createDescriptor(String id) {
        return new InitDescriptionDataDesc();
    }

    @Override
    protected boolean parseNodeAttribute(
            InitDescriptionDataDesc descriptor, String id, String value) {
        if (id.equals(SOURCE)) {
            descriptor.setSource(value);
            return true;
        }

        return true;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName,
                                         InitDescriptionDataDesc desc) {
    }

    private String getValue() {
        return AGXmlParserHelper.loadXmlNodeValue();
    }

}
