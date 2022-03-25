package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGSignatureDataDesc;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGSignatureXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.FormControlStructureParser;

import static com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper.getColorFromHex;
import static com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper.parseStringToSizeDesc;

public class AGSignatureStructureXmlParser extends
		AbstractAGViewStructureXmlParser {
	
	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		return new AGSignatureDataDesc(id);
	}

	@Override
	protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
		AGSignatureDataDesc desc = (AGSignatureDataDesc) descriptor;
		if (id.equals(AGSignatureXmlAttributes.STROKE_WIDTH)) {
			AGSizeDesc size = parseStringToSizeDesc(value);
			desc.setStrokeWidth(size);
			return true;
		} else if (id.equals(AGSignatureXmlAttributes.STROKE_COLOR)) {
			int color = getColorFromHex(value);
			desc.setStrokeColor(color);
			return true;
		}  else if (id.equals(AGSignatureXmlAttributes.CLEAR_SIZE)) {
			AGSizeDesc size = parseStringToSizeDesc(value);
			desc.setClearSize(size);
			return true;
		}  else if (id.equals(AGSignatureXmlAttributes.CLEAR_SRC)) {
			String variable = AGXmlParserHelper.getStringOrNullIfNone(value);
			VariableDataDesc varDesc = AGXmlActionParser.createVariable(variable, desc);
			desc.getImageDescriptor().setImageSrc(varDesc);
			return true;
		}  else if (id.equals(AGSignatureXmlAttributes.CLEAR_ACTIVE_SRC)) {
			String variable = AGXmlParserHelper.getStringOrNullIfNone(value);
			VariableDataDesc varDesc = AGXmlActionParser.createVariable(variable, desc);
			desc.getActiveImageDescriptor().setImageSrc(varDesc);
			return true;
		} else if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else if (FormControlStructureParser.parseNodeAttribute(desc,id,value)) {
            return true;
        }
        return false;

	}
	
	@Override
	protected String getStructureRootNodeName() {
		return AGXmlNodes.CONTROL_PHOTO;
	}

}
