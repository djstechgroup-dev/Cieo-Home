package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGTemplateDataDesc;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

import java.security.InvalidParameterException;

abstract public class AbstractAGTemplateStructureParser extends AbstractStructureXmlParser<AbstractAGTemplateDataDesc> {

	@Override
	protected boolean parseNodeValue(AbstractAGTemplateDataDesc desc) {
		return true;
	}

	@Override
	protected boolean parseNodeAttribute(AbstractAGTemplateDataDesc descriptor, String id, String value) {
		return true;
	}

	@Override
	protected void proceedParseStructure(String nodeName,
			AbstractAGTemplateDataDesc desc) {
		if(nodeName.contains(AGXmlNodes.CONTROL) || nodeName.contains(AGXmlNodes.CONTAINER)){
			desc.addControl((AbstractAGViewDataDesc) StructureXmlParsersFactory.getStructureParser(nodeName).parseStructure());
		}else
        {
			throw new InvalidParameterException(String.format(
					"Unexpected node '%s' in '%s' strucutre", nodeName,
					getStructureRootNodeName()));
		}
	}
}
