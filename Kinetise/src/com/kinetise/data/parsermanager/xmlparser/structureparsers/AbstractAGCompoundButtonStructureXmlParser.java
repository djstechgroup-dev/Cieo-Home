package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGCompoundButtonDataDesc;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGCompoundButtonXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

public abstract class AbstractAGCompoundButtonStructureXmlParser extends
		AGTextStructureXmlParser {

	@Override
	protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
		AbstractAGCompoundButtonDataDesc desc = (AbstractAGCompoundButtonDataDesc) descriptor;

		if (super.parseNodeAttribute(descriptor, id, value)) {
			return true;
		}
		else if (id.equals(AGCompoundButtonXmlAttributes.CHECK_HEIGHT)) {
			AGSizeDesc size = AGXmlParserHelper.getSizeDescFromKPXString(value);
			desc.setCheckHeight(size);
			return true;
		} else if (id.equals(AGCompoundButtonXmlAttributes.CHECK_VALIGN)) {
			desc.setCheckVAlign(AGXmlParserHelper.getVAlignType(value));
			return true;
		} else if (id.equals(AGCompoundButtonXmlAttributes.CHECK_SRC)) {
			VariableDataDesc varDesc = AGXmlActionParser.createVariable(value, desc);
			desc.getActiveImageDescriptor().setImageSrc(varDesc);
			return true;
		} else if (id
				.equals(AGCompoundButtonXmlAttributes.CHECK_WIDTH)) {
			AGSizeDesc size = AGXmlParserHelper.getSizeDescFromKPXString(value);
			desc.setCheckWidth(size);
			return true;
		} else if (id.equals(AGCompoundButtonXmlAttributes.INNER_SPACE)) {
			AGSizeDesc size = AGXmlParserHelper.getSizeDescFromKPXString(value);
			desc.setInnerSpace(size);
			return true;
		} else if (id.equals(AGCompoundButtonXmlAttributes.SRC)) {
			VariableDataDesc varDesc = AGXmlActionParser.createVariable(value, desc);
			desc.getImageDescriptor().setImageSrc(varDesc);
			return true;
		} else if (id.equals(AGCompoundButtonXmlAttributes.SIZEMODE)) {
			desc.getImageDescriptor().setSizeMode(AGXmlParserHelper.getSizeModeFromString(value));
			return true;
		}

		return false;
	}
}
