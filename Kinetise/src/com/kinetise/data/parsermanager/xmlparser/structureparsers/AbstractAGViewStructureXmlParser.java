package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.descriptors.types.AGSizeModeType;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGXmlCommonAttributes;

import java.security.InvalidParameterException;

import static com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper.*;
import static com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser.createVariable;

public abstract class AbstractAGViewStructureXmlParser extends
        AbstractStructureXmlParser<AbstractAGViewDataDesc> {

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc desc, String id, String value) {

        if (id.equals(AGXmlCommonAttributes.ID)) {
            String controlId = getStringOrNullIfNone(value);
            desc.setId(controlId);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.WIDTH)) {
            AGSizeDesc size = parseStringToSizeDesc(value);
            desc.setWidth(size);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.HEIGHT)) {
            AGSizeDesc size = parseStringToSizeDesc(value);
            desc.setHeight(size);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.BACKGROUND)) {
            String background = getStringOrNullIfNone(value);
            VariableDataDesc varDesc = createVariable(background, desc);
            desc.setBackground(varDesc);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.BACKGROUND_COLOR)) {
            int color = getColorFromHex(value);
            desc.setBackgroundColor(color);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.ALIGN)) {
            desc.setAlign(getAlignType(value));
            return true;
        } else if (id.equals(AGXmlCommonAttributes.VALIGN)) {
            desc.setVAlign(getVAlignType(value));
            return true;
        } else if (id.equals(AGXmlCommonAttributes.MARGIN_LEFT)) {
            AGSizeDesc size = parseStringToSizeDescWithoutMinMax(value);
            desc.getMargin().setLeft(size);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.MARGIN_RIGHT)) {
            AGSizeDesc size = parseStringToSizeDescWithoutMinMax(value);
            desc.getMargin().setRight(size);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.MARGIN_TOP)) {
            AGSizeDesc size = parseStringToSizeDescWithoutMinMax(value);
            desc.getMargin().setTop(size);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.MARGIN_BOTTOM)) {
            AGSizeDesc size = parseStringToSizeDescWithoutMinMax(value);
            desc.getMargin().setBottom(size);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.BORDER_LEFT)) {
            desc.getBorder().setLeft(getSizeDescFromKPXString(value));
            return true;
        }else if (id.equals(AGXmlCommonAttributes.BORDER_RIGHT)) {
            desc.getBorder().setRight(getSizeDescFromKPXString(value));
            return true;
        }else if (id.equals(AGXmlCommonAttributes.BORDER_TOP)) {
            desc.getBorder().setTop(getSizeDescFromKPXString(value));
            return true;
        }else if (id.equals(AGXmlCommonAttributes.BORDER_BOTTOM)) {
            desc.getBorder().setBottom(getSizeDescFromKPXString(value));
            return true;
        } else if (id.equals(AGXmlCommonAttributes.BORDER_COLOR)) {
            int color = getColorFromHex(value);
            desc.setBorderColor(color);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.PADDING_LEFT)) {
            AGSizeDesc size = parseStringToSizeDescWithoutMinMax(value);
            desc.setPaddingLeft(size);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.PADDING_RIGHT)) {
            AGSizeDesc size = parseStringToSizeDescWithoutMinMax(value);
            desc.setPaddingRight(size);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.PADDING_TOP)) {
            AGSizeDesc size = parseStringToSizeDescWithoutMinMax(value);
            desc.setPaddingTop(size);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.PADDING_BOTTOM)) {
            AGSizeDesc size = parseStringToSizeDescWithoutMinMax(value);
            desc.setPaddingBottom(size);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.ON_CLICK)) {
            value = getStringOrNullIfNone(value);
            VariableDataDesc variableDataDesc = createVariable(value, desc);
            desc.setOnClickActionDesc(variableDataDesc);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.ON_UPDATE)) {
            value = getStringOrNullIfNone(value);
            VariableDataDesc variableDataDesc = createVariable(value, desc);
            desc.setOnUpdateActionDesc(variableDataDesc);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.ON_CHANGE)) {
            value = getStringOrNullIfNone(value);
            VariableDataDesc variableDataDesc = createVariable(value, desc);
            desc.setOnChangeActionDesc(variableDataDesc);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.RADIUS_BOTTOM_LEFT)) {
            AGSizeDesc size = parseStringToSizeDescWithoutMinMax(value);
            desc.setRadiusBottomLeft(size);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.RADIUS_BOTTOM_RIGHT)) {
            AGSizeDesc size = parseStringToSizeDescWithoutMinMax(value);
            desc.setRadiusBottomRight(size);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.RADIUS_TOP_LEFT)) {
            AGSizeDesc size = parseStringToSizeDescWithoutMinMax(value);
            desc.setRadiusTopLeft(size);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.RADIUS_TOP_RIGHT)) {
            AGSizeDesc size = parseStringToSizeDescWithoutMinMax(value);
            desc.setRadiusTopRight(size);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.BACKGROUND_SIZEMODE)){
            AGSizeModeType sizeMode = getSizeModeFromString(value);
            desc.setBackgroundSizeMode(sizeMode);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.HIDDEN)){
            VariableDataDesc action = parseStringAsAction(value, desc);
            desc.setHiddenAction(action);
            return true;
        } else if (id.equals(AGXmlCommonAttributes.EXCLUDE_FROM_CALCULATE)){
            VariableDataDesc action = parseStringAsAction(value, desc);
            desc.setRemovedAction(action);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void proceedParseStructure(String nodeName, AbstractAGViewDataDesc desc) {
        throw new InvalidParameterException(String.format(
                "Unexpected node '%s' in '%s' structure", nodeName,
                getStructureRootNodeName()));
    }

    @Override
    protected boolean parseNodeValue(AbstractAGViewDataDesc desc) {
        return true;
    }
}
