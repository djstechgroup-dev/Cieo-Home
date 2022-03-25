package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGDatePickerDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGDatePickerXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGDateXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGTextInputXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.FormControlStructureParser;

public class AGDatePickerStructureXmlParser extends AGButtonStructureXmlParser {
    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGDatePickerDataDesc(id);
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc desc, String id, String value) {
        AGDatePickerDataDesc descriptor = (AGDatePickerDataDesc) desc;
        DecoratorParser decoratorParser = new DecoratorParser();

        if (FormControlStructureParser.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else if (decoratorParser.parseNodeAttribute(descriptor.getDecoratorDescriptor(), id, value, descriptor)) {
            return true;
        } else if (super.parseNodeAttribute(desc, id, value)) {
            return true;
        } else if (id.equals(AGDateXmlAttributes.DATE_FORMAT)) {
            AGDateStructureXmlParser.setDateFormatForDataDescriptor(desc, value);
            return true;
        } else if (id.equals(AGTextInputXmlAttributes.WATERMARK)){
            descriptor.setWatermark(AGXmlActionParser.createVariable(value, desc));
            return true;
        } else if (id.equals(AGTextInputXmlAttributes.WATERMARK_COLOR)){
            descriptor.setWatermarkColor(AGXmlParserHelper.getColorFromHex(value));
            return true;
        } else if (id.equals(AGDatePickerXmlAttributes.MODE)){
            descriptor.setDatePickerMode(AGXmlParserHelper.getDatePickerModeType(value));
            return true;
        } else if (id.equals(AGDatePickerXmlAttributes.MIN_DATE)){
            descriptor.setMinDateDesc(AGXmlActionParser.createVariable(value, descriptor));
            return true;
        } else if (id.equals(AGDatePickerXmlAttributes.MAX_DATE)){
            descriptor.setMaxDateDesc(AGXmlActionParser.createVariable(value, descriptor));
            return true;
        }
        return false;
    }

    @Override
    protected void endParseNode(AbstractAGViewDataDesc desc) {
        AGDatePickerDataDesc descriptor = (AGDatePickerDataDesc) desc;
        descriptor.setTextColor(descriptor.getTextDescriptor().getTextColor());
        super.endParseNode(desc);

    }

    @Override
    protected String getStructureRootNodeName() {
        return AGXmlNodes.CONTROL_DATE_PICKER;
    }

    @Override
    protected boolean parseNodeValue(AbstractAGViewDataDesc descriptor) {
        return true;
    }
}
