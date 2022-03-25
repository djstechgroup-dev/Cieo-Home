package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.SynchronizationDescriptionDataDesc;
import com.kinetise.data.descriptors.SynchronizationMethodDataDesc;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGSynchronizationXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

import java.security.InvalidParameterException;

public class AGsynchronizationDescriptionStructXmlParser extends AbstractStructureXmlParser<SynchronizationDescriptionDataDesc> {
    private static final String NODE_NAME = AGXmlNodes.SYNCHRONIZATION;
    private static final String ENABLED = "enabled";
    private static final String TYPE = "type";

    @Override
    protected boolean parseNodeValue(SynchronizationDescriptionDataDesc desc) {
        return true;
    }

    @Override
    protected SynchronizationDescriptionDataDesc createDescriptor(String id) {
        return new SynchronizationDescriptionDataDesc();
    }

    @Override
    protected boolean parseNodeAttribute(
            SynchronizationDescriptionDataDesc descriptor, String id, String value) {
        if (id.equals(AGSynchronizationXmlAttributes.ENABLED)) {
            boolean isScroll = Boolean.valueOf(value);
            descriptor.setEnabled(isScroll);
            return true;
        } else if (id.equals(AGSynchronizationXmlAttributes.TYPE)) {
            descriptor.setType(value);
            return true;
        }
        return true;
    }

    @Override
    protected String getStructureRootNodeName() {
        return AGXmlNodes.SYNCHRONIZATION;
    }

    @Override
    protected void proceedParseStructure(String nodeName, SynchronizationDescriptionDataDesc desc) {
        if (nodeName.equals(AGXmlNodes.GET)) {
            AGSynchronizationMethodStructureXmlParser synchronizationParser = new AGSynchronizationMethodStructureXmlParser();
            SynchronizationMethodDataDesc synchronizationMethodDataDesc = synchronizationParser.parseStructure();
            desc.setGetMethodDataDesc(synchronizationMethodDataDesc);
        } else if (nodeName.equals(AGXmlNodes.CREATE)) {
            AGSynchronizationMethodStructureXmlParser synchronizationParser = new AGSynchronizationMethodStructureXmlParser();
            SynchronizationMethodDataDesc synchronizationMethodDataDesc = synchronizationParser.parseStructure();
            desc.setCreateMethodDataDesc(synchronizationMethodDataDesc);
        } else if (nodeName.equals(AGXmlNodes.UPDATE)) {
            AGSynchronizationMethodStructureXmlParser synchronizationParser = new AGSynchronizationMethodStructureXmlParser();
            SynchronizationMethodDataDesc synchronizationMethodDataDesc = synchronizationParser.parseStructure();
            desc.setUpdateMethodDataDesc(synchronizationMethodDataDesc);
        } else if (nodeName.equals(AGXmlNodes.DELETE)) {
            AGSynchronizationMethodStructureXmlParser synchronizationParser = new AGSynchronizationMethodStructureXmlParser();
            SynchronizationMethodDataDesc synchronizationMethodDataDesc = synchronizationParser.parseStructure();
            desc.setDeleteMethodDataDesc(synchronizationMethodDataDesc);
        }
    }

    private String getValue() {
        return AGXmlParserHelper.loadXmlNodeValue();
    }
}
