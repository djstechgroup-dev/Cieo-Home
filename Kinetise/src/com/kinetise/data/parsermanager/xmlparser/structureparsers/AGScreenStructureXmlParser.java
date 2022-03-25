package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AGBodyDataDesc;
import com.kinetise.data.descriptors.AGHeaderDataDesc;
import com.kinetise.data.descriptors.AGNaviPanelDataDesc;
import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.AbstractAGSectionDataDesc;
import com.kinetise.data.descriptors.types.AGSizeModeType;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGScreenXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGXmlCommonAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

import java.security.InvalidParameterException;

public class AGScreenStructureXmlParser extends
        AbstractStructureXmlParser<AGScreenDataDesc> {

    private static final String NODE_NAME = AGXmlNodes.SCREEN;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected void proceedParseStructure(String nodeName, AGScreenDataDesc desc) {

        AbstractAGSectionStructureXmlParser parser = (AbstractAGSectionStructureXmlParser) StructureXmlParsersFactory
                .getStructureParser(nodeName);

        if (nodeName.equals(AGXmlNodes.HEADER)) {
            AbstractAGSectionDataDesc headerDesc = parser.parseStructure();
            desc.setScreenHeader((AGHeaderDataDesc) headerDesc);
        } else if (nodeName.equals(AGXmlNodes.BODY)) {
            AbstractAGSectionDataDesc bodyDesc = parser.parseStructure();
            desc.setScreenBody((AGBodyDataDesc) bodyDesc);
        } else if (nodeName.equals(AGXmlNodes.NAVIPANEL)) {
            AbstractAGSectionDataDesc naviPanelDesc = parser.parseStructure();
            desc.setScreenNaviPanel((AGNaviPanelDataDesc) naviPanelDesc);
        } else {
            throw new InvalidParameterException(String.format(
                    "Unexpected node '%s' in '%s' structure", nodeName,
                    NODE_NAME));
        }
    }

    @Override
    public AGScreenDataDesc parseStructure() {
        return super.parseStructure();
    }

    @Override
    protected AGScreenDataDesc createDescriptor(String id) {
        return new AGScreenDataDesc(id);
    }

    @Override
    protected boolean parseNodeAttribute(AGScreenDataDesc desc, String id, String value) {

        if (id.equals(AGXmlCommonAttributes.ID)) {
            return true;
        } else if (id.equals(AGScreenXmlAttributes.BACKGROUND)) {
            desc.setBackground(AGXmlParserHelper.parseStringAsAction(value, desc));
            return true;
        } else if (id
                .equals(AGScreenXmlAttributes.BACKGROUND_COLOR)) {
            int color = AGXmlParserHelper
                    .getColorFromHex(value);
            desc.setBackgroundColor(color);
            return true;
        } else if (id.equals(AGScreenXmlAttributes.ANALITYCSTAG)) {
            desc.setAnalitycsTag(value);
            return true;
        } else if (id
                .equals(AGScreenXmlAttributes.NEXT_SCREEN)) {
            String nextScreenId = AGXmlParserHelper
                    .getStringOrNullIfNone(value);
            desc.setNextScreenId(nextScreenId);
            return true;
        } else if (id.equals(AGScreenXmlAttributes.ORIENTATION)) {
            desc.setOrientation(AGXmlParserHelper.getOrientationType(value));
            return true;
        } else if (id.equals(AGScreenXmlAttributes.PULLTOREFRESH)) {
            desc.setPullToRefresh(AGXmlParserHelper.convertYesNoToBoolean(value));
            return true;
        } else if (id.equals(AGScreenXmlAttributes.PROTECTED)) {
            desc.setProtected(AGXmlParserHelper.convertYesNoToBoolean(value));
            return true;
        } else if (id.equals(AGScreenXmlAttributes.BACKGROUNDVIDEO)) {
            desc.setBackgroundVideoName(AGXmlParserHelper.getStringOrNullIfNone(value));
            return true;
        } else if (id.equals(AGScreenXmlAttributes.ONENTER)) {
            desc.setOnScreenEnterAction(AGXmlParserHelper.parseStringAsAction(value, desc));
            return true;
        } else if (id.equals(AGScreenXmlAttributes.ONEXIT)) {
            desc.setOnScreenExitAction(AGXmlParserHelper.parseStringAsAction(value, desc));
            return true;
        } else if (id.equals(AGXmlCommonAttributes.BACKGROUND_SIZEMODE)) {
            AGSizeModeType sizeMode = AGXmlParserHelper.getSizeModeFromString(value);
            desc.setBackgroundSizeMode(sizeMode);
            return true;

        } else if (id.equals(AGXmlCommonAttributes.STATUS_BAR_COLOR)) {
            desc.setStatusBarColor(AGXmlParserHelper.getColorFromHex(value));
            return true;

        } else if (id.equals(AGXmlCommonAttributes.STATUS_BAR_MODE)) {
            boolean statusBarColorInvert =  AGXmlParserHelper.getColorModeType(value);
            desc.setStatusBarColorInvert(statusBarColorInvert);
            return true;
        } else {
            return false;
        }
    }



    @Override
    protected boolean parseNodeValue(AGScreenDataDesc desc) {
        return true;
    }
}
