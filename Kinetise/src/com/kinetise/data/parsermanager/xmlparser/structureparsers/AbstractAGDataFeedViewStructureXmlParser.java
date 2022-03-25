package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGDataFeedViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.AGItemTemplateDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.ErrorDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.ItemPath;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.LoadMoreDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.LoadingDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Namespaces;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.NoDataDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.UsingFields;
import com.kinetise.data.descriptors.types.AGFeedCachePolicyType;
import com.kinetise.data.descriptors.types.AGFeedFormatType;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGDataFeedXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGFormAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGHttpXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser.ErrorStructureXmlParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser.ItemPathStructureXmlParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser.LoadMoreStructureXmlParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser.LoadingStructureXmlParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser.NamespacesStructureXmlParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser.NoDataStructureXmlParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser.UsingFieldsStructureXmlParser;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

import java.security.InvalidParameterException;

public abstract class AbstractAGDataFeedViewStructureXmlParser extends AbstractAGViewStructureXmlParser {

    @Override
    protected void proceedParseStructure(String nodeName, AbstractAGViewDataDesc descriptor) {

        AbstractAGDataFeedViewDataDesc desc = (AbstractAGDataFeedViewDataDesc) descriptor;

        if (nodeName.startsWith(AGXmlNodes.ITEM_PATH)) {

            ItemPathStructureXmlParser parser = (ItemPathStructureXmlParser) StructureXmlParsersFactory
                    .getStructureParser(nodeName);
            ItemPath itemPath = parser.parseStructure();

            desc.setItemPath(itemPath);
        } else if (nodeName.startsWith(AGXmlNodes.USING_FIELDS)) {

            UsingFieldsStructureXmlParser parser = (UsingFieldsStructureXmlParser) StructureXmlParsersFactory
                    .getStructureParser(nodeName);
            UsingFields usingField = parser.parseStructure();

            desc.setUsingFields(usingField);
        } else if (nodeName.startsWith(AGXmlNodes.LOAD_MORE)) {
            LoadMoreStructureXmlParser parser = (LoadMoreStructureXmlParser)
                    StructureXmlParsersFactory.getStructureParser(nodeName);
            LoadMoreDataDesc loadMore = parser.parseStructure();

            desc.setLoadMoreTemplate(loadMore);
        } else if (nodeName.startsWith(AGXmlNodes.NO_DATA)) {
            NoDataStructureXmlParser parser = (NoDataStructureXmlParser)
                    StructureXmlParsersFactory.getStructureParser(nodeName);
            NoDataDataDesc noDataDataDesc = parser.parseStructure();

            desc.setNoDataTemplate(noDataDataDesc);
        } else if (nodeName.startsWith(AGXmlNodes.LOADING)) {
            LoadingStructureXmlParser parser = (LoadingStructureXmlParser)
                    StructureXmlParsersFactory.getStructureParser(nodeName);
            LoadingDataDesc loading = parser.parseStructure();

            desc.setLoadingTemplate(loading);
        } else if (nodeName.startsWith(AGXmlNodes.ERROR)) {
            ErrorStructureXmlParser parser = (ErrorStructureXmlParser)
                    StructureXmlParsersFactory.getStructureParser(nodeName);
            ErrorDataDesc error = parser.parseStructure();

            desc.setErrorTemplate(error);
        } else if (nodeName.startsWith(AGXmlNodes.ITEM_TEMPLATE)) {

            AGItemTemplateStructureXmlParser parser = (AGItemTemplateStructureXmlParser) StructureXmlParsersFactory
                    .getStructureParser(nodeName);
            AGItemTemplateDataDesc itemTemplate = (AGItemTemplateDataDesc) parser.parseStructure();

            desc.addTempleteDataDesc(itemTemplate);
        } else if (nodeName.startsWith(AGXmlNodes.NAMESPACES)) {

            NamespacesStructureXmlParser parser = (NamespacesStructureXmlParser) StructureXmlParsersFactory
                    .getStructureParser(nodeName);
            Namespaces namespaces = parser.parseStructure();

            desc.setNamespaces(namespaces);
        } else {
            throw new InvalidParameterException(String.format(
                    "Unexpected node '%s' in '%s' strucutre", nodeName,
                    getStructureRootNodeName()));
        }
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc desc, String id, String value) {

        AbstractAGDataFeedViewDataDesc descriptor = (AbstractAGDataFeedViewDataDesc) desc;

        if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else if (id.equals(AGDataFeedXmlAttributes.URI_SOURCE)) {
            String variable = AGXmlParserHelper.getStringOrNullIfNone(value);
            VariableDataDesc varDesc = AGXmlActionParser.createVariable(variable, desc);
            descriptor.setSource(varDesc);
            return true;
        } else if (id.equals(AGDataFeedXmlAttributes.SHOW_ITEMS)) {
            descriptor.setNumberItemsPerPage(AGXmlParserHelper.convertToIntIncludeNONE(value));
            return true;
        }
        else if (id
                .equals(AGHttpXmlAttributes.HTTP_PARAMS)) {
            descriptor.setHttpParams(HttpParamsDataDesc.getHttpParams(value, descriptor));
            return true;
        }
        else if (id
                .equals(AGHttpXmlAttributes.HEADER_PARAMS)) {
            descriptor.setHeaderParams(HttpParamsDataDesc.getHttpParams(value, descriptor));
            return true;
        }
        else if (id
                .equals(AGHttpXmlAttributes.BODY_PARAMS)) {
            descriptor.setBodyParams(HttpParamsDataDesc.getHttpParams(value, descriptor));
            return true;
        }
        else if (id.equals(AGDataFeedXmlAttributes.FORMAT)) {
            descriptor.setFormat(AGFeedFormatType.getFormatType(value));
            return true;
        } else if (id.equals(AGDataFeedXmlAttributes.CACHE_POLICY)) {
            descriptor.setCachePolicyType(AGFeedCachePolicyType.getCachePolicyType(value));
            return true;
        } else if (id.equals(AGDataFeedXmlAttributes.CACHE_POLICY_ATTRIBUTE)) {
            if (value.equals(""))
                descriptor.setCachePolicyAttribute(0);
            else
                descriptor.setCachePolicyAttribute(AGXmlParserHelper.convertToLong(value));
            return true;
        } else if (id.equals(AGHttpXmlAttributes.HTTP_METHOD)) {
            descriptor.setHttpMethod(AGXmlParserHelper.getHttpMethodType(value));
            return true;
        } else if (id.equals(AGHttpXmlAttributes.REQUEST_BODY_TRANSFORM)) {
            descriptor.setRequestBodyTrasform(value);
            return true;
        } else if (id.equals(AGFormAttributes.FORM_ID)) {
            String variable = AGXmlParserHelper.getStringOrNullIfNone(value);
            descriptor.setFormId( AGXmlActionParser.createVariable(variable, desc));
            return true;
        } else if (id.equals(AGDataFeedXmlAttributes.GUID_NODE_NAME)) {
            descriptor.setGUIDNodeName(value);
            return true;
        } else if (id.equals(AGHttpXmlAttributes.LOCAL_DB_PARAMS)) {
            return true;
        }

        return false;
    }
}
