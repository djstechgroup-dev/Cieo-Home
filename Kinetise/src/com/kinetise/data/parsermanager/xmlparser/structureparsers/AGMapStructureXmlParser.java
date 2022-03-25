package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGMapDataDesc;
import com.kinetise.data.descriptors.types.InitCameraModeType;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGMapXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

public class AGMapStructureXmlParser extends AbstractAGDataFeedViewStructureXmlParser {

    private final static String NODE_NAME = AGXmlNodes.CONTROL_MAP;

    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGMapDataDesc(id);
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc desc, String id, String value) {
        AGMapDataDesc descriptor = (AGMapDataDesc) desc;
        if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else if (id.equals(AGMapXmlAttributes.LATITUDE)) {
            descriptor.setLatitudeNodeName(value);
            return true;
        } else if (id.equals(AGMapXmlAttributes.LONGTITUDE)) {
            descriptor.setLongtitudeNodeName(value);
            return true;
        } else if (id.equals(AGMapXmlAttributes.INIT_CAMERA_MODE)) {
            descriptor.setInitCameraMode(InitCameraModeType.valueOf(value.toUpperCase()));
            return true;
        } else if (id.equals(AGMapXmlAttributes.INIT_MIN_RADIUS)) {
            descriptor.setInitMinRadius(Integer.parseInt(value));
            return true;
        } else if (id.equals(AGMapXmlAttributes.PIN_IMAGE)) {
            String variable = AGXmlParserHelper.getStringOrNullIfNone(value);
            descriptor.setPinImageAdress(AGXmlActionParser.createVariable(variable, desc));
            return true;
        } else if (id.equals(AGMapXmlAttributes.MY_LOCATION_ENABLED)) {
            boolean myLocationEnabled = AGXmlParserHelper.convertYesNoToBoolean(value);
            descriptor.setMyLocationEnabled(myLocationEnabled);
            return true;
        } else if (id.equals(AGMapXmlAttributes.SHOW_MAP_POPUP)) {
            descriptor.setShowMapPopup(AGXmlParserHelper.convertYesNoToBoolean(value));
            return true;
        }
        return false;
    }

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

}
