package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.ApplicationDescriptionDataDesc;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

import static com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper.getColorFromHex;

public class AGApplicationDescriptionStructXmlParser extends
        AbstractStructureXmlParser<ApplicationDescriptionDataDesc> {

    private static final String NODE_NAME = "applicationDescription";

    @Override
    protected boolean parseNodeValue(ApplicationDescriptionDataDesc desc) {
        return true;
    }

    @Override
    protected ApplicationDescriptionDataDesc createDescriptor(String id) {
        return new ApplicationDescriptionDataDesc();
    }

    @Override
    protected boolean parseNodeAttribute(
            ApplicationDescriptionDataDesc descriptor, String id, String value) {
        return true;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName,
                                         ApplicationDescriptionDataDesc desc) {
        if (nodeName.equals(AGXmlNodes.START_SCREEN)) {
            desc.setStartScreenId(AGXmlParserHelper.parseStringAsAction(getValue(), null));
        } else if (nodeName.equals(AGXmlNodes.NAME)) {
            desc.setName(getValue());
        } else if (nodeName.equals(AGXmlNodes.LOGIN_SCREEN)) {
            desc.setLoginScreenId(AGXmlParserHelper.parseStringAsAction(getValue(), null));
        } else if (nodeName.equals(AGXmlNodes.MAIN_SCREEN)) {
            desc.setMainScreenId(AGXmlParserHelper.parseStringAsAction(getValue(), null));
        } else if (nodeName.equals(AGXmlNodes.PROTECTED_LOGIN_SCREEN)) {
            desc.setProtectedLoginScreenId(AGXmlParserHelper.parseStringAsAction(getValue(), null));
        } else if (nodeName.equals(AGXmlNodes.VERSION)) {
            desc.setVersion(getValue());
        } else if (nodeName.equals(AGXmlNodes.DEAFULT_USER_AGENT)) {
            desc.setDefaultUserAgent(getValue());
        } else if (nodeName.equals(AGXmlNodes.MIN_FONT_SIZE_MULTIPLIER)) {
            desc.setMinFontSizeMultiplier(AGXmlParserHelper.convertToFloat(getValue()));
        } else if (nodeName.equals(AGXmlNodes.MAX_FONT_SIZE_MULTIPLIER)) {
            desc.setMaxFontSizeMultiplier(AGXmlParserHelper.convertToFloat(getValue()));
        } else if (nodeName.equals(AGXmlNodes.VALIDATION_ERROR_TOAST_COLOR)) {
            int color = getColorFromHex(getValue());
            desc.setValidationErrorToastColor(color);
        } else if (nodeName.equals(AGXmlNodes.VERSION_API)) {
            desc.setApiVersion(getValue());
        } else if (nodeName.equals(AGXmlNodes.VERSION_API)) {
            desc.setApiVersion(getValue());
        } else if (nodeName.equals(AGXmlNodes.CREATED_VERSION)) {
            desc.setCreatedVersion(getValue());
        } else {
            // do nothing
        }
    }

    private String getValue() {
        return AGXmlParserHelper.loadXmlNodeValue();
    }

}
