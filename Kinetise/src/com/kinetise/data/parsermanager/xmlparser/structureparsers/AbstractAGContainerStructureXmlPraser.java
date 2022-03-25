package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGContainerXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

import java.security.InvalidParameterException;

public abstract class AbstractAGContainerStructureXmlPraser extends
        AbstractAGViewStructureXmlParser {

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
        AbstractAGContainerDataDesc desc = (AbstractAGContainerDataDesc) descriptor;

        if (super.parseNodeAttribute(desc, id, value)) {
            return true;
        } else if (id.equals(AGContainerXmlAttributes.SCROLLVERTICAL)) {
            boolean isScroll = AGXmlParserHelper.convertYesNoToBoolean(value);
            desc.setScrollVertical(isScroll);
            return true;
        } else if (id.equals(AGContainerXmlAttributes.SCROLLHORIZONTAL)) {
            boolean isScroll = AGXmlParserHelper.convertYesNoToBoolean(value);
            desc.setScrollHorizontal(isScroll);
            return true;
        } else if (id.equals(AGContainerXmlAttributes.SEPARATOR_COLOR)) {
            desc.setSeparatorColor(AGXmlParserHelper.getColorFromHex(value));
            return true;
        } else if (id.equals(AGContainerXmlAttributes.SEPARATOR_WIDTH)) {
            AGSizeDesc size = AGXmlParserHelper.getSizeDescFromKPXString(value);
            desc.setItemBorder(size);
            return true;
        }else if (id.equals(AGContainerXmlAttributes.CHILDREN_SPACING)) {
            AGSizeDesc size = AGXmlParserHelper.getSizeDescFromKPXString(value);
            desc.setItemSeparation(size);
            return true;
        } else if (id.equals(AGContainerXmlAttributes.SEPARATOR_MARGIN_BEGIN)) {
            AGSizeDesc size = AGXmlParserHelper.getSizeDescFromKPXString(value);
            desc.setItemBorderMarginStart(size);
            return true;
        }else if (id.equals(AGContainerXmlAttributes.SEPARATOR_MARGIN_END)) {
            AGSizeDesc size = AGXmlParserHelper.getSizeDescFromKPXString(value);
            desc.setItemBorderMarginEnd(size);
            return true;
        } else if (id.equals(AGContainerXmlAttributes.INNER_BORDER)) {
            AGSizeDesc size = AGXmlParserHelper.getSizeDescFromKPXString(value);
            desc.setItemSeparation(size);
            desc.setItemBorder(size);
            desc.setItemBorderMarginStart(AGSizeDesc.ZEROKPX);
            desc.setItemBorderMarginEnd(AGSizeDesc.ZEROKPX);
            return true;
        }else if (id.equals(AGContainerXmlAttributes.INNER_ALIGN)) {
            desc.setInnerAlign(AGXmlParserHelper.getAlignType(value));
            return true;
        } else if (id.equals(AGContainerXmlAttributes.INNER_V_ALIGN)) {
            desc.setInnerVAlign(AGXmlParserHelper.getVAlignType(value));
            return true;
        } else if (id.equals(AGContainerXmlAttributes.INVERT)) {
            desc.setInverted(AGXmlParserHelper.convertYesNoToBoolean(value));
            return true;
        }

        return false;
    }

    @Override
    protected void proceedParseStructure(String nodeName, AbstractAGViewDataDesc descriptor) {
        AbstractAGContainerDataDesc desc = (AbstractAGContainerDataDesc) descriptor;
        AbstractAGViewDataDesc viewDataDesc;

        if (nodeName.startsWith(AGXmlNodes.CONTROL)) {
            AbstractAGViewStructureXmlParser parser = (AbstractAGViewStructureXmlParser) StructureXmlParsersFactory.getStructureParser(nodeName);
            viewDataDesc = parser.parseStructure();
        } else if (nodeName.startsWith(AGXmlNodes.CONTAINER)) {
            AbstractAGContainerStructureXmlPraser parser = (AbstractAGContainerStructureXmlPraser) StructureXmlParsersFactory.getStructureParser(nodeName);
            viewDataDesc = parser.parseStructure();
        } else {
            throw new InvalidParameterException(String.format("Unexpected node '%s' in '%s' structure", nodeName, getStructureRootNodeName()));
        }
        desc.addControl(viewDataDesc);
    }
}
