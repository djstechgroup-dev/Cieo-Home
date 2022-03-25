package com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers;

import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGInvalidXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;

public class InvalidFormControlStructureParser {
    public static boolean parseNodeAttribute(IFormControlDesc descriptor, String id, String value) {
         if (id.equals(AGInvalidXmlAttributes.INVALID_COLOR)) {
            int color = AGXmlParserHelper.getColorFromHex(value);
            descriptor.getFormDescriptor().setInvalidColor(color);
            return true;
        } else if (id.equals(AGInvalidXmlAttributes.INVALID_BORDER_COLOR)) {
            int color = AGXmlParserHelper.getColorFromHex(value);
            descriptor.getFormDescriptor().setInvalidBorderColor(color);
            return true;
        }
        return false;
    }
}