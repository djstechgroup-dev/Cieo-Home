package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGWebBrowserDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGWebBrowserXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

public class AGWebBrowserStructureXmlParser extends
		AbstractAGViewStructureXmlParser {

	private static final String NODE_NAME = AGXmlNodes.CONTROL_WEB_BROWSER;

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}

	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		return new AGWebBrowserDataDesc(id);
	}

	@Override
	protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {

		AGWebBrowserDataDesc desc = (AGWebBrowserDataDesc) descriptor;

		if (super.parseNodeAttribute(descriptor, id, value)) {
			return true;
		} else if (id
				.equals(AGWebBrowserXmlAttributes.URI_SOURCE)) {
            String variable = AGXmlParserHelper
                    .getStringOrNullIfNone(value);
            VariableDataDesc varDesc = AGXmlActionParser
                    .createVariable(variable, descriptor);
            desc.setSource(varDesc);
			return true;
		} else if (id.equals(AGWebBrowserXmlAttributes.GO_TO_EXTERNAL_BROWSER)) {
			String variable = AGXmlParserHelper
					.getStringOrNullIfNone(value);
			boolean goToExternalBrowser = (variable.equals("yes")) ? true : false;
			desc.setGoToExternalBrowser(goToExternalBrowser);
			return true;
		}

		return false;
	}
}
