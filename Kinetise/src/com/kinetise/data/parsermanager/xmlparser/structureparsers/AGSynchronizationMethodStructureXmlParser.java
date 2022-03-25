package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.SynchronizationMethodDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGHttpXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGSynchronizationMethodXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

public class AGSynchronizationMethodStructureXmlParser extends AbstractStructureXmlParser<SynchronizationMethodDataDesc> {

    @Override
    protected boolean parseNodeValue(SynchronizationMethodDataDesc desc) {
        return true;
    }

    @Override
    protected SynchronizationMethodDataDesc createDescriptor(String id) {
        return new SynchronizationMethodDataDesc();
    }

    @Override
    protected boolean parseNodeAttribute(SynchronizationMethodDataDesc descriptor, String id, String value) {
        if (id.equals(AGSynchronizationMethodXmlAttributes.SRC)) {
            String variable = AGXmlParserHelper.getStringOrNullIfNone(value);
            VariableDataDesc varDesc = AGXmlActionParser.createVariable(variable, null);
            descriptor.setSrc(varDesc);
            return true;
        } else if (id.equals(AGHttpXmlAttributes.HTTP_METHOD)) {
            descriptor.setHttpMethod(AGXmlParserHelper.getHttpMethodType(value));
            return true;
        } else if (id.equals(AGHttpXmlAttributes.HTTP_PARAMS)) {
            descriptor.setHttpParams(HttpParamsDataDesc.getHttpParams(value, null));
            return true;
        } else if (id.equals(AGHttpXmlAttributes.HEADER_PARAMS)) {
            descriptor.setHeaderParams(HttpParamsDataDesc.getHttpParams(value, null));
            return true;
        } else if (id.equals(AGHttpXmlAttributes.BODY_PARAMS)) {
            descriptor.setBodyParams(HttpParamsDataDesc.getHttpParams(value, null));
            return true;
        } else if (id.equals(AGHttpXmlAttributes.REQUEST_BODY_TRANSFORM)) {
            descriptor.setRequestBodyTrasform(value);
            return true;
        }
        return true;
    }

    @Override
    protected String getStructureRootNodeName() {
        return AGXmlNodes.SYNCHRONIZATION; //TODO czy to ważne???
    }

    @Override
    protected void proceedParseStructure(String nodeName, SynchronizationMethodDataDesc desc) {
    }

    private String getValue() {
        return AGXmlParserHelper.loadXmlNodeValue();
    }
}
