package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGTextImageDataDesc;
import com.kinetise.data.descriptors.types.AGSizeModeType;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGHttpXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGImageXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

public abstract class AbstractAGImageStructureXmlParser extends
        AbstractAGTextStructureXmlParser {

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {
        AGTextImageDataDesc desc = (AGTextImageDataDesc) descriptor;

        if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else if (id.equals(AGImageXmlAttributes.SRC)) {
            String variable = AGXmlParserHelper.getStringOrNullIfNone(value);
            VariableDataDesc varDesc = AGXmlActionParser.createVariable(variable, desc);
            desc.getImageDescriptor().setImageSrc(varDesc);
            return true;
        } else if (id.equals(AGImageXmlAttributes.SIZE_MODE)) {
            AGSizeModeType sizeMode = AGXmlParserHelper.getSizeModeFromString(value);
            desc.getImageDescriptor().setSizeMode(sizeMode);
            return true;
        } else if (id.equals(AGImageXmlAttributes.SHOW_LOADING)){
            boolean showLoading = AGXmlParserHelper.convertYesNoToBoolean(value);
            desc.setShowLoading(showLoading);
            return true;
        } else if (id.equals(AGHttpXmlAttributes.HTTP_METHOD)) {
            desc.getImageDescriptor().setHttpMethod(AGXmlParserHelper.getHttpMethodType(value));
            return true;
        } else if (id.equals(AGHttpXmlAttributes.HTTP_PARAMS)) {
            desc.getImageDescriptor().setHttpParams(HttpParamsDataDesc.getHttpParams(value, descriptor));
            return true;
        } else if (id.equals(AGHttpXmlAttributes.HEADER_PARAMS)) {
            desc.getImageDescriptor().setHeaderParams(HttpParamsDataDesc.getHttpParams(value, descriptor));
            return true;
        } else if (id.equals(AGHttpXmlAttributes.BODY_PARAMS)) {
            desc.getImageDescriptor().setBodyParams(HttpParamsDataDesc.getHttpParams(value, descriptor));
            return true;
        } else if (id.equals(AGHttpXmlAttributes.REQUEST_BODY_TRANSFORM)) {
            desc.getImageDescriptor().setRequestBodyTrasform(value);
            return true;
        }

        return false;
    }
}
