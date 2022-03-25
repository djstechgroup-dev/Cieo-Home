package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGTextImageDataDesc;
import com.kinetise.data.descriptors.types.AGTextVAlignType;
import com.kinetise.data.descriptors.types.TextPosition;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGTextXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.XmlAttributeValues;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

public class AGImageStructureXmlParser extends AbstractAGImageStructureXmlParser {

	private static final String NODE_NAME = AGXmlNodes.CONTROL_IMAGE;

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}

	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		return new AGTextImageDataDesc(id);
	}

	@Override
	protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
		AGTextImageDataDesc desc = (AGTextImageDataDesc) descriptor;

        if (id.equals(AGTextXmlAttributes.TEXT_V_ALIGN)) {
            if (value.equals(XmlAttributeValues.BOTTOM)) {
                setTextPositionAndAlign(desc, TextPosition.ONTOP, AGTextVAlignType.BOTTOM);
            } else if (value.equals(XmlAttributeValues.TOP)) {
                setTextPositionAndAlign(desc, TextPosition.ONTOP, AGTextVAlignType.TOP);
            } else if (value.equals(XmlAttributeValues.CENTER)) {
                setTextPositionAndAlign(desc, TextPosition.ONTOP, AGTextVAlignType.CENTER);
            } else if (value.equals(XmlAttributeValues.ABOVE)) {
                setTextPositionAndAlign(desc, TextPosition.ABOVE, AGTextVAlignType.TOP);
            } else if (value.equals(XmlAttributeValues.BELOW)){
                setTextPositionAndAlign(desc, TextPosition.BELOW, AGTextVAlignType.BOTTOM);
            } else if (value.equals(XmlAttributeValues.BELOW)){
                setTextPositionAndAlign(desc, TextPosition.BELOW, AGTextVAlignType.BOTTOM);
            } else {
                throw new IllegalArgumentException(String.format(
                        "Unknown Align Type : '%s'", value));
            }
            return true;
        } else if (super.parseNodeAttribute(descriptor, id, value))
			return true;

		return false;
	}

    private void setTextPositionAndAlign(AGTextImageDataDesc desc, TextPosition position, AGTextVAlignType valign){
        desc.setTextPosition(position);
        desc.getTextDescriptor().setTextVAlign(valign);
    }

}
