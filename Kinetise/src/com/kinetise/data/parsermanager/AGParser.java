package com.kinetise.data.parsermanager;

import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.ApplicationDescriptionDataDesc;
import com.kinetise.data.descriptors.LocalStorageDescriptionDataDesc;
import com.kinetise.data.descriptors.datadescriptors.OverlayDataDesc;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.AGScreenStructureXmlParser;
import com.kinetise.support.J2meXmlParser.XmlReader;

import java.util.HashMap;
import java.util.Map;

public abstract class AGParser {

    private void initializeXmlReader() {
        XmlReader.setXml(getXml());
        XmlReader.reset();
    }

    public void loadDescriptors(ParserManager.LoadDescriptorsCallback callback) {
        initializeXmlReader();
        Map<String, AGScreenDataDesc> screenMap = new HashMap<String, AGScreenDataDesc>();
        Map<String, OverlayDataDesc> applicationOverlays = new HashMap<String, OverlayDataDesc>();
        ApplicationDescriptionDataDesc appDataDesc = null;
        LocalStorageDescriptionDataDesc localStorageDescriptionDataDesc = null;
        String nodeName;
        while (XmlReader.goToNextNod() && !XmlReader.isErrorXml()) {
            nodeName = XmlReader.getNodName();

            if (nodeName.equals(AGXmlNodes.SCREEN)) {
                AGScreenStructureXmlParser parser = (AGScreenStructureXmlParser) StructureXmlParsersFactory
                        .getStructureParser(nodeName);

                AGScreenDataDesc agScreenDataDesc = parser.parseStructure();

                screenMap.put(agScreenDataDesc.getScreenId(), agScreenDataDesc);
            } else if (nodeName.equals(AGXmlNodes.APPLICATION_DESCRIPTION)) {
                appDataDesc = (ApplicationDescriptionDataDesc) StructureXmlParsersFactory
                        .getStructureParser(nodeName).parseStructure();
            } else if (nodeName.equals((AGXmlNodes.OVERLAYS))) {
                applicationOverlays = (Map<String, OverlayDataDesc>) StructureXmlParsersFactory.getStructureParser(nodeName).parseStructure();
            } else if (nodeName.equals(AGXmlNodes.LOCALSTORAGE)) {
                localStorageDescriptionDataDesc = (LocalStorageDescriptionDataDesc) StructureXmlParsersFactory
                        .getStructureParser(nodeName).parseStructure();
            }
        }

        if (screenMap == null || applicationOverlays == null || appDataDesc == null) {
            throw new RuntimeException("Error creating descriptors");
        }

        callback.onScreensLoaded(screenMap);
        callback.onOverlaysLoaded(applicationOverlays);
        callback.onDescriptorsLoaded(appDataDesc);
        callback.onLocalStorageDescriptorLoaded(localStorageDescriptionDataDesc);
        callback.onParseCompleted();
    }

    public String getValidationUrl() {
        return null;
    }

    public abstract String getXml();

    public boolean isAdvertScreen() {
        return false;
    }
}
