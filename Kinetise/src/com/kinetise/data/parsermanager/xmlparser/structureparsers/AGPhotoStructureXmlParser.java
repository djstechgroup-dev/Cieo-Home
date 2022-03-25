package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGPhotoDataDesc;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.FormControlStructureParser;

public class AGPhotoStructureXmlParser extends
		AGButtonStructureXmlParser {
	
	@Override
	protected AbstractAGViewDataDesc createDescriptor(String id) {
		return new AGPhotoDataDesc(id);
	}

	@Override
	protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
        AGPhotoDataDesc desc = (AGPhotoDataDesc) descriptor;
        if (super.parseNodeAttribute(descriptor, id, value)) {
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
