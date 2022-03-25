package com.kinetise.data.parsermanager.xmlparser;

import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.*;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.feedparser.*;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for Structure parsers
 */
public class StructureXmlParsersFactory {

    private static Map<String, AbstractStructureXmlParser<?>> mStructureParsers = 
    		new HashMap<String, AbstractStructureXmlParser<?>>();

    /**
     * Gets mInstance of structure parser for node name
     * @param name of node
     * @return structure parser for given node
     */
    public static AbstractStructureXmlParser<?> getStructureParser(String name) {

        if (!mStructureParsers.containsKey(name)) {
            mStructureParsers.put(name, createParser(name));
        }

        return mStructureParsers.get(name);
    }

    /**
     * Creates parser for given nodeName
     * @param nodeName name of node to parse
     * @return parser to parse given node
     */
    private static AbstractStructureXmlParser<?> createParser(String nodeName) {
        if(nodeName.equals(AGXmlNodes.OVERLAYS)) {
            return new OverlaysStructureXmlParser();
        } else if(nodeName.equals(AGXmlNodes.OVERLAY)){
            return new OverlayStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.SCREEN)) {
            return new AGScreenStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTAINER_HORIZONTAL)) {
            return new AGContainerHorizontalStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTAINER_VERTICAL)) {
            return new AGContainerVerticalStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTAINER_THUMBNAILS)) {
            return new AGContainerThumbnailsStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTAINER_TABLE)) {
            return new AGContainerTableStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_RADIO_GROUP_HORIZONTAL)) {
            return new AGRadioGroupHorizontalStrcutureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_RADIO_GROUP_VERTICAL)) {
            return new AGRadioGroupVerticalStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_RADIO_GROUP_TABLE)) {
            return new AGRadioGroupTableStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_RADIO_GROUP_THUMBNAILS)) {
            return new AGRadioGroupThumbnailsStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_DATA_FEED_HORIZONTAL)) {
            return new AGDataFeedHorizontalStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_DATA_FEED_VERTICAL)) {
            return new AGDataFeedVerticalStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_DATA_FEED_THUMBNAILS)) {
            return new AGDataFeedThumbnailsStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_IMAGE)) {
            return new AGImageStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_PINCH_IMAGE)) {
            return new AGPinchImageStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_TEXT)) {
            return new AGTextStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_BUTTON)) {
            return new AGButtonStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_CHECKBOX)) {
            return new AGCheckBoxStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_CODE_SCANNER)) {
            return new AGCodeScannerStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_GET_PHONE_CONTACT)) {
            return new AGGetPhoneContactStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_DATE)) {
            return new AGDateStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_HYPERLINK)) {
            return new AGHyperlinkStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_RADIO_BUTTON)) {
            return new AGRadioButtonStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_WEB_BROWSER)) {
            return new AGWebBrowserStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.ITEM_PATH)) {
            return new ItemPathStructureXmlParser(AGXmlNodes.ITEM_PATH);
        } else if (nodeName.equals(AGXmlNodes.FIELD_PATH)) {
            return new ItemPathStructureXmlParser(AGXmlNodes.FIELD_PATH);
        } else if (nodeName.equals(AGXmlNodes.CREATE_RESPONSE_PATH)) {
            return new ItemPathStructureXmlParser(AGXmlNodes.CREATE_RESPONSE_PATH);
        } else if (nodeName.equals(AGXmlNodes.USING_FIELDS)) {
            return new UsingFieldsStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.FIELD)) {
            return new FieldStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.ITEM_TEMPLATE)) {
            return new AGItemTemplateStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.NAMESPACES)) {
            return new NamespacesStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.NAMESPACE)) {
            return new NamespaceStructureXmlParser();
        } else  if (nodeName.equals(AGXmlNodes.BODY)) {
            return new AGBodyStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.HEADER)) {
            return new AGHeaderStrucutreXmlParser();
        } else if (nodeName.equals(AGXmlNodes.NAVIPANEL)) {
            return new AGNaviPanelStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.APPLICATION_DESCRIPTION)) {
            return new AGApplicationDescriptionStructXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_TEXTINPUT)) {
            return new AGTextInputStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.LOAD_MORE)) {
            return new LoadMoreStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.NO_DATA)) {
            return new NoDataStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.LOADING)) {
            return new LoadingStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.ERROR)) {
            return new ErrorStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.PAGINATION)) {
            return new PaginationStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.NEXT_PAGE_URL)) {
            return new NextPageUrlStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.NEXT_PAGE_TOKEN)) {
            return new NextPageTokenStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_TEXTAREA)) {
            return new AGTextAreaStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_PASSWORD)) {
            return new AGPasswordStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_SEARCH_INPUT)) {
            return new AGSearchInputStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_GALLERY)) {
            return new AGGalleryStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_MAP)) {
            return new AGMapStructureXmlParser();
        } else if (nodeName.equals(AGXmlNodes.CONTROL_PHOTO)) {
        	return new AGPhotoStructureXmlParser();
        } else if(nodeName.equals(AGXmlNodes.CONTROL_VIDEO)) {
            return new AGVideoViewStructureParser();
        } else if(nodeName.startsWith(AGXmlNodes.CONTROL_DROPDOWN)) {
            return new AGDropdownStructureXmlParser();
        }  else if(nodeName.startsWith(AGXmlNodes.CONTROL_DATE_PICKER)) {
            return new AGDatePickerStructureXmlParser();
        } else if(nodeName.startsWith(AGXmlNodes.CONTROL_TOGGLEBUTTON)){
            return new AGTogglebuttonStrunctureXmlParser();
        } else if(nodeName.startsWith(AGXmlNodes.CONTROL_ACTIVITY_INDICATOR)){
          return new AGActivityIndicatorStructureXmlParser();
        } else if(nodeName.startsWith(AGXmlNodes.CONTROL_CHART)){
            return new AGChartStructureXmlParser();
        } else if(nodeName.startsWith(AGXmlNodes.CONTROL_SIGNATURE)){
            return new AGSignatureStructureXmlParser();
        } else if (nodeName.startsWith(AGXmlNodes.LOCALSTORAGE)){
            return new AGLocalstorageDescriptionStructXmlParser();
        } else if (nodeName.startsWith(AGXmlNodes.CONTROL_CUSTOM)){
            return new AGCustomControlStructureXmlParser(nodeName);
        } else {
            throw new InvalidParameterException(String.format(
                    "There is no parser to parse node: [%s]", nodeName));
        }
    }

}
