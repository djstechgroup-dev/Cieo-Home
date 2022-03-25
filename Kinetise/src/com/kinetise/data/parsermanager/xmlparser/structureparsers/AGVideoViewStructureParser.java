package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGVideoViewDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.VideoXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

public class AGVideoViewStructureParser extends AbstractAGViewStructureXmlParser {
    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGVideoViewDataDesc(id);
    }

    @Override
    protected String getStructureRootNodeName() {
        return AGXmlNodes.CONTROL_VIDEO;
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {

        AGVideoViewDataDesc desc = (AGVideoViewDataDesc) descriptor;

        if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else if (id
                .equals(VideoXmlAttributes.VIDEO_SRC)) {
            String variable = AGXmlParserHelper
                    .getStringOrNullIfNone(value);
            VariableDataDesc varDesc = AGXmlActionParser
                    .createVariable(variable, desc);
            desc.setVideoSrc(varDesc);
            return true;
        } else if (id.equals(VideoXmlAttributes.AUTOPLAY)){
            String variable = AGXmlParserHelper
                    .getStringOrNullIfNone(value);
            boolean autoplay = (variable.equals("yes")) ? true : false;
            desc.setAutoplay(autoplay);
            return true;
        }

        return false;
    }
}
