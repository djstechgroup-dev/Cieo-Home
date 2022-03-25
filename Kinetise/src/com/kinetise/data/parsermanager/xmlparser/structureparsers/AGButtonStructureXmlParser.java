package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGButtonDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGButtonXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

public class AGButtonStructureXmlParser extends AGImageStructureXmlParser {

	private static final String NODE_NAME = AGXmlNodes.CONTROL_BUTTON;

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}

	@Override
	protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {

		AGButtonDataDesc desc = (AGButtonDataDesc) descriptor;

		if (super.parseNodeAttribute(descriptor, id, value)) {
			return true;
		} else if (id.equals(AGButtonXmlAttributes.ACTIVE_SRC)) {
            String variable = AGXmlParserHelper.getStringOrNullIfNone(value);
            VariableDataDesc varDesc = AGXmlActionParser.createVariable(variable, desc);
            desc.getActiveImageDescriptor().setImageSrc(varDesc);
			return true;
		} else if(id.equals(AGButtonXmlAttributes.ACTIVE_COLOR)){
            int color = AGXmlParserHelper.getColorFromHex(value);
            desc.setActiveColor(color);
            return true;
		} else if(id.equals(AGButtonXmlAttributes.ACTIVE_BORDER_COLOR)){
            int color = AGXmlParserHelper.getColorFromHex(value);
            desc.setActiveBorderColor(color);
            return true;
        } else if(id.equals(AGButtonXmlAttributes.ACTIVE_HTTP_METHOD)){
			desc.getActiveImageDescriptor().setHttpMethod(AGXmlParserHelper.getHttpMethodType(value));
			return true;
		}  else if(id.equals(AGButtonXmlAttributes.ACTIVE_HEADER_PARAMS)){
			desc.getActiveImageDescriptor().setHeaderParams(HttpParamsDataDesc.getHttpParams(value, descriptor));
			return true;
		} else if(id.equals(AGButtonXmlAttributes.ACTIVE_BODY_PARAMS)){
			desc.getActiveImageDescriptor().setBodyParams(HttpParamsDataDesc.getHttpParams(value, descriptor));
			return true;
		} else if(id.equals(AGButtonXmlAttributes.ACTIVE_REQUEST_BODY_TRANSFORM)){
			desc.getActiveImageDescriptor().setRequestBodyTrasform(value);
			return true;
		} else if(id.equals(AGButtonXmlAttributes.ACTIVE_HTTP_PARAMS)){
			desc.getActiveImageDescriptor().setHttpParams(HttpParamsDataDesc.getHttpParams(value, descriptor));
			return true;
		}

		return false;
	}

	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		return new AGButtonDataDesc(id);
	}

}
