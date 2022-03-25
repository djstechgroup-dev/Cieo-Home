package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.TextDescriptor;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.descriptors.types.AGTextAlignType;
import com.kinetise.data.descriptors.types.AGTextVAlignType;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGTextXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.XmlAttributeValues;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

public class TextParser {

    protected boolean parseNodeAttribute(TextDescriptor descriptor, String id, String value) {
         if (id.equals(AGTextXmlAttributes.FONT_SIZE)) {
             AGSizeDesc size = AGXmlParserHelper.getSizeDescFromKPXString(value);
             descriptor.setFontSizeDesc(size);
             return true;
        } else if (id.equals(AGTextXmlAttributes.FONT_STYLE)) {
             descriptor.setItalic(getFontStyleFromString(value));
             return true;
        } else if (id.equals(AGTextXmlAttributes.FONT_WEIGHT)) {
             descriptor.setBold(getFontWeightFromString(value));
             return true;
        } else if (id.equals(AGTextXmlAttributes.TEXT_ALIGN)) {
             descriptor.setTextAlign(getTextAlignFromString(value));
             return true;
        } else if (id.equals(AGTextXmlAttributes.TEXT_COLOR)) {
            int color = AGXmlParserHelper.getColorFromHex(value);
             descriptor.setTextColor(color);
             return true;
        } else if (id.equals(AGTextXmlAttributes.TEXT_DECORATION)) {
             descriptor.setTextDecoration(getTextDecorationFromString(value));
             return true;
        } else if (id.equals(AGTextXmlAttributes.TEXT_V_ALIGN)) {
             descriptor.setTextVAlign(getTextVAlignFromString(value));
             return true;
        } else if (id.equals(AGTextXmlAttributes.MAX_CHARACTERS)) {
             int max = AGXmlParserHelper.convertToIntIncludeNONE(value);
             descriptor.setMaxCharacters(max);
             return true;
         } else if (id.equals(AGTextXmlAttributes.MAX_LINES)) {
             int max = AGXmlParserHelper.convertToIntIncludeNONE(value);
             descriptor.setMaxLines(max);
            return true;
         } else if (id.equals(AGTextXmlAttributes.FONT_PROPORTIONAL)) {
             boolean fontProportional = AGXmlParserHelper.convertYesNoToBoolean(value);
             descriptor.setFontProportional(fontProportional);
             return true;
         } else if (id.equals(AGTextXmlAttributes.TEXT_PADDING_TOP)) {
             descriptor.getPadding().setTop(AGXmlParserHelper.getSizeDescFromKPXString(value));
             return true;
         } else if (id.equals(AGTextXmlAttributes.TEXT_PADDING_RIGHT)) {
             descriptor.getPadding().setRight(AGXmlParserHelper.getSizeDescFromKPXString(value));
             return true;
         } else if (id.equals(AGTextXmlAttributes.TEXT_PADDING_LEFT)) {
             descriptor.getPadding().setLeft(AGXmlParserHelper.getSizeDescFromKPXString(value));
             return true;
         } else if (id.equals(AGTextXmlAttributes.TEXT_PADDING_BOTTOM)) {
             descriptor.getPadding().setBottom(AGXmlParserHelper.getSizeDescFromKPXString(value));
             return true;
         }

        return false;
    }

    protected boolean parseNodeValue(TextDescriptor descriptor) {
        String text = AGXmlParserHelper.loadXmlNodeValue();
        VariableDataDesc variable = AGXmlActionParser.createVariable(text, descriptor.getParent());
        descriptor.setText(variable);
        return true;
    }

    private boolean getFontWeightFromString(String value) {
        if (value.equals(XmlAttributeValues.BOLD))
            return true;
        else if (value.equals(XmlAttributeValues._NONE))
            return false;
        else {
            throw new IllegalArgumentException(String.format(
                    "Text bold value cannot be '%s'", value));
        }
    }

    private boolean getFontStyleFromString(String value) {
        if (value.equals(XmlAttributeValues.ITALIC))
            return true;
        else if (value.equals(XmlAttributeValues._NONE))
            return false;
        else {
            throw new IllegalArgumentException(String.format(
                    "Text italic value cannot be '%s'", value));
        }
    }

    private boolean getTextDecorationFromString(String value) {
        if (value.equals(XmlAttributeValues.UNDERLINE))
            return true;
        else if (value.equals(XmlAttributeValues._NONE))
            return false;
        else {
            throw new IllegalArgumentException(String.format(
                    "Text underline value cannot be '%s'", value));
        }
    }

    private AGTextAlignType getTextAlignFromString(String value) {
        if (value.equals(XmlAttributeValues.LEFT))
            return AGTextAlignType.LEFT;
        else if (value.equals(XmlAttributeValues.RIGHT))
            return AGTextAlignType.RIGHT;
        else if (value.equals(XmlAttributeValues.CENTER))
            return AGTextAlignType.CENTER;
        else {
            throw new IllegalArgumentException(String.format(
                    "Text align value cannot be '%s'", value));
        }
    }

    private AGTextVAlignType getTextVAlignFromString(String value) {
        if(value.equals(XmlAttributeValues.TOP)){
            return AGTextVAlignType.TOP;
        }
        else if(value.equals(XmlAttributeValues.CENTER)){
            return AGTextVAlignType.CENTER;
        }
        else if(value.equals(XmlAttributeValues.BOTTOM)){
            return AGTextVAlignType.BOTTOM;
        }
        else{
            throw new IllegalArgumentException(String.format(
                    "Text valign value cannot be '%s'", value));
        }
    }
}
