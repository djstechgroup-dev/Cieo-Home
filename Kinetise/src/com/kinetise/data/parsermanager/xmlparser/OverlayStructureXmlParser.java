package com.kinetise.data.parsermanager.xmlparser;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.OverlayAnimationType;
import com.kinetise.data.descriptors.datadescriptors.OverlayDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGXmlCommonAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.OverlayXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractAGContainerStructureXmlPraser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AbstractStructureXmlParser;

import java.security.InvalidParameterException;

public class OverlayStructureXmlParser extends AbstractStructureXmlParser<OverlayDataDesc> {
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String TOP = "top";
    private static final String BOTTOM = "bottom";

    @Override
    protected boolean parseNodeValue(OverlayDataDesc desc) {
        return true;
    }

    @Override
    protected OverlayDataDesc createDescriptor(String id) {
        return new OverlayDataDesc(id);
    }

    @Override
    protected boolean parseNodeAttribute(OverlayDataDesc descriptor, String id, String value) {
        if (id.equals(AGXmlCommonAttributes.ID)) {
            return true;
        } else if (id.equals(OverlayXmlAttributes.ANIMATION)) {
            OverlayAnimationType animationType = getAnimationTypeByName(value);
            descriptor.setAnimationType(animationType);
            return true;
        } else if (id.equals(OverlayXmlAttributes.GRAYOUTBACKGROUND)) {
            boolean grayoutBackground = AGXmlParserHelper.convertYesNoToBoolean(value);
            descriptor.setGrayoutBackground(grayoutBackground);
            return true;
        } else if (id.equals(OverlayXmlAttributes.MOVEOVERLAY)) {
            boolean moveOverlay = AGXmlParserHelper.convertYesNoToBoolean(value);
            descriptor.setMoveOverlay(moveOverlay);
            return true;
        } else if (id.equals(OverlayXmlAttributes.MOVESCREEN)) {
            boolean moveScreen = AGXmlParserHelper.convertYesNoToBoolean(value);
            descriptor.setMoveScreen(moveScreen);
            return true;
        }else if (id.equals(OverlayXmlAttributes.ONENTER)){
            descriptor.setOnOverlayEnterAction(AGXmlParserHelper.parseStringAsAction(value, null));
            return true;
        }else if (id.equals(OverlayXmlAttributes.ONEXIT)){
            descriptor.setOnOverlayExitAction(AGXmlParserHelper.parseStringAsAction(value, null));
            return true;
        }
        return false;
    }

    private OverlayAnimationType getAnimationTypeByName(String value) {
        if (value.equals(LEFT)) {
            return OverlayAnimationType.LEFT;
        } else if (value.equals(RIGHT)) {
            return OverlayAnimationType.RIGHT;
        } else if (value.equals(TOP)) {
            return OverlayAnimationType.TOP;
        } else if (value.equals(BOTTOM)) {
            return OverlayAnimationType.BOTTOM;
        } else {
            throw new IllegalArgumentException(String.format("Unexpected value of animation type node: %s", value));
        }
    }

    @Override
    protected String getStructureRootNodeName() {
        return AGXmlNodes.OVERLAY;
    }

    @Override
    protected void proceedParseStructure(String nodeName, OverlayDataDesc desc) {
        AbstractAGViewDataDesc viewDataDesc;
        if (nodeName.startsWith(AGXmlNodes.CONTAINER)) {

            AbstractAGContainerStructureXmlPraser parser = (AbstractAGContainerStructureXmlPraser) StructureXmlParsersFactory
                    .getStructureParser(nodeName);
            viewDataDesc = parser.parseStructure();
        } else {
            throw new InvalidParameterException(String.format(
                    "Unexpected node '%s' in '%s' structure", nodeName,
                    getStructureRootNodeName()));
        }

        if(desc.getMainViewDesc() == null) {
            desc.setMainViewDesc(viewDataDesc);
        } else {
            throw new InvalidParameterException(String.format("Overlay node with id '%s' contains more than one container.",desc.getId()));
        }
    }
}
