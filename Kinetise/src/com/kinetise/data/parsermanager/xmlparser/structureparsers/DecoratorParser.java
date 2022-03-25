package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.DecoratorDescriptor;
import com.kinetise.data.descriptors.types.AGSizeModeType;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGDecoratorXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

public class DecoratorParser {
    protected boolean parseNodeAttribute(DecoratorDescriptor descriptor, String id, String value, AbstractAGElementDataDesc parentDescriptor) {
        if (id.equals(AGDecoratorXmlAttributes.SRC)) {
            String variable = AGXmlParserHelper.getStringOrNullIfNone(value);
            VariableDataDesc varDesc = AGXmlActionParser.createVariable(variable, parentDescriptor);
            descriptor.setImageSrc(varDesc);
            return true;
        } else if (id.equals(AGDecoratorXmlAttributes.ACTIVE_SRC)) {
            String variable = AGXmlParserHelper.getStringOrNullIfNone(value);
            VariableDataDesc varDesc = AGXmlActionParser.createVariable(variable, parentDescriptor);
            descriptor.setActiveSrc(varDesc);
            return true;
        } else if (id.equals(AGDecoratorXmlAttributes.SIZE_MODE)) {
            AGSizeModeType sizeMode = AGXmlParserHelper.getSizeModeFromString(value);
            descriptor.setSizeMode(sizeMode);
            return true;
        } else if (id.equals(AGDecoratorXmlAttributes.ALIGN)) {
            descriptor.setAlign(AGXmlParserHelper.getAlignType(value));
            return true;
        } else if (id.equals(AGDecoratorXmlAttributes.V_ALIGN)) {
            descriptor.setVAlign(AGXmlParserHelper.getVAlignType(value));
            return true;
        } else if (id.equals(AGDecoratorXmlAttributes.HEIGHT)) {
            descriptor.setHeight(AGXmlParserHelper.getSizeDescFromKPXString(value));
            return true;
        } else if (id.equals(AGDecoratorXmlAttributes.WIDTH)) {
            descriptor.setWidth(AGXmlParserHelper.getSizeDescFromKPXString(value));
            return true;
        }
        return false;
    }
}
