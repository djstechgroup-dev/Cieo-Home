package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGDropdownDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGDropdownXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGTextInputXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.FormControlStructureParser;

public class AGDropdownStructureXmlParser extends AGButtonStructureXmlParser {
    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGDropdownDataDesc(id);
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc desc, String id, String value) {
        AGDropdownDataDesc descriptor = (AGDropdownDataDesc) desc;
        DecoratorParser decoratorParser = new DecoratorParser();

        if (FormControlStructureParser.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else if (decoratorParser.parseNodeAttribute(descriptor.getDecoratorDescriptor(),id, value, desc)) {
                return true;
        } else if (id.equals(AGDropdownXmlAttributes.LIST_SRC)) {
            descriptor.setOptionsList(AGXmlActionParser.createVariable(value, descriptor));
            return true;
        } else if (id.equals(AGTextInputXmlAttributes.WATERMARK)){
            descriptor.setWatermark(AGXmlActionParser.createVariable(value, descriptor));
            return true;
        } else if (id.equals(AGTextInputXmlAttributes.WATERMARK_COLOR)){
            descriptor.setWatermarkColor(AGXmlParserHelper.getColorFromHex(value));
            return true;
        } else if (id.equals(AGDropdownXmlAttributes.ITEM_PATH)){
            descriptor.setItemPath(AGXmlActionParser.createVariable(value, descriptor));
            return true;
        } else if (id.equals(AGDropdownXmlAttributes.VALUE_PATH)){
            descriptor.setValuePath(AGXmlActionParser.createVariable(value, descriptor));
            return true;
        }else if (id.equals(AGDropdownXmlAttributes.TEXT_PATH)){
            descriptor.setTextPath(AGXmlActionParser.createVariable(value, descriptor));
            return true;
        }
        return false;
    }

    @Override
    protected void endParseNode(AbstractAGViewDataDesc desc) {
        super.endParseNode(desc);
        AGDropdownDataDesc descriptor = (AGDropdownDataDesc) desc;
        descriptor.setTextColor(descriptor.getTextDescriptor().getTextColor());
    }

    @Override
    protected String getStructureRootNodeName() {
        return AGXmlNodes.CONTROL_DROPDOWN;
    }

    @Override
    protected boolean parseNodeValue(AbstractAGViewDataDesc descriptor) {
        return true;
    }
}
