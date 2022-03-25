package com.kinetise.helpers.parser;

import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Field;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.NamespaceElement;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Namespaces;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.UsingFields;
import com.kinetise.helpers.parser.xmlNodePath.XMLNodePath;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;

public class XpathFeedParserHandler extends DefaultHandler {

    private final HashMap<String, String> mFieldsMap;
    private final XMLNodePath mNextPagePath;
    private boolean isParsingItem = false;
    private boolean isParsingNextPage = false;

    //region Fields assigned in constructor
    private final String mRssNodeNotFoundMessage;
    private final DataFeed mDataFeed;
    private final String mItemsRoot;
    private final Namespaces mFeedNamespaceContext;
    //endregion

    //region State fields
    private XMLNodePath mCurrentXmlNodePath;
    private StringBuilder mCurrentElementCharacters;
    private int mCurrentDepthRelativeToItem = 0;
    private int mCurrentDepthRelativeToNextPage = 0;
    //endregion

    //region Constants
    private final static String ALTER_API_CONTEXT = "k:context";
    private final static String TARGET_TYPE = "k:targettype";
    private final static String DEFAULT_NODE_NOT_FOUND_TEXT = "key: not found";
    //endregion

    public XpathFeedParserHandler(DataFeed dataFeed, String feedItemXPath,
                                  Namespaces namespaces, String keyNotFoundMessage, UsingFields usingFields, String nextPageXPath) {

        if (dataFeed == null)
            throw new Error("dataFeed can't be null!");

        if (keyNotFoundMessage != null) {
            mRssNodeNotFoundMessage = keyNotFoundMessage;
        } else {
            mRssNodeNotFoundMessage = DEFAULT_NODE_NOT_FOUND_TEXT;
        }

        mItemsRoot = feedItemXPath;

        if (nextPageXPath != null)
            mNextPagePath = new XMLNodePath(nextPageXPath);
        else
            mNextPagePath = null;

        mCurrentXmlNodePath = new XMLNodePath(mItemsRoot);
        mDataFeed = dataFeed;
        mFieldsMap = new HashMap<>();
        for (Field field : usingFields.getFields()) {
            mFieldsMap.put(field.getXpath(), field.getId());
        }
        mFeedNamespaceContext = namespaces.copy();
        mCurrentElementCharacters = new StringBuilder();
    }

    //region Inherited public methods
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        mCurrentXmlNodePath.push(localName, mFeedNamespaceContext.getPrefixByUri(uri));

        if (!isParsingItem && mCurrentXmlNodePath.isInItemRoot()) {
            mDataFeed.addItem(new DataFeedItem(mRssNodeNotFoundMessage));
            isParsingItem = true;
        }

        if (isParsingItem) {
            ++mCurrentDepthRelativeToItem;
            parseAttributes(attributes, mDataFeed.getLastItem());
        }

        if (mNextPagePath != null) {
            mNextPagePath.push(localName, mFeedNamespaceContext.getPrefixByUri(uri));

            if (!isParsingNextPage && mNextPagePath.isInItemRoot()) {
                isParsingNextPage = true;
            }

            if (isParsingNextPage) {
                ++mCurrentDepthRelativeToNextPage;
            }
        }
    }

    public void characters(char[] ch, int start, int len) throws SAXException {
        mCurrentElementCharacters.append(new String(ch, start, start + len));
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (isParsingItem) {
            --mCurrentDepthRelativeToItem;
            if (mCurrentDepthRelativeToItem > 0) {
                String relativeElementXPath = mCurrentXmlNodePath.getRelativePath();
                String usingField = mFieldsMap.get(relativeElementXPath);
                if (usingField != null) {
                    mDataFeed.getLastItem().putIfKeyDoesntExist(mFieldsMap.get(relativeElementXPath), mCurrentElementCharacters.toString());
                }
            } else {
                isParsingItem = false;
            }
        }

        if (isParsingNextPage) {
            if (mCurrentDepthRelativeToNextPage-- > 0) {
                String nextPageUrl = mCurrentElementCharacters.toString();
                mDataFeed.setNextPageAddress(nextPageUrl);
            } else {
                isParsingNextPage = false;
            }
        }
        mCurrentElementCharacters.delete(0, mCurrentElementCharacters.length());
        mCurrentXmlNodePath.pop();
        if (mNextPagePath != null)
            mNextPagePath.pop();

    }

    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        mFeedNamespaceContext.add(new NamespaceElement(prefix, uri));
    }
    //endregion

    //region Private methods

    /**
     * Parse xml element attributes and put them in the dataFeedItem hashmap
     *
     * @param attributes   xml element attributes
     * @param dataFeedItem data feed item corresponding to xml element
     */
    private void parseAttributes(Attributes attributes, DataFeedItem dataFeedItem) {

        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                String attributeName = attributes.getQName(i);
                if (attributeName.equals(ALTER_API_CONTEXT)) {
                    dataFeedItem.setAlterApiContext(attributes.getValue(i));
                } else if (attributeName.equals(TARGET_TYPE)) {
                    dataFeedItem.setTargetType(attributes.getValue(i));
                } else {
                    String usingField = mFieldsMap.get(getRelativeAttributePath(attributeName));
                    if (usingField != null) {
                        dataFeedItem.putIfKeyDoesntExist(usingField, attributes.getValue(i));
                    }
                }
            }
        }
    }

    /**
     * Get attribute xpath relative to item feed element:
     * e.g.
     * Absolute xpath: a/b/c/@d
     * Feed item xpath: a/b
     * Relative xpath: c/@d
     *
     * @param attributeName name of an attribute
     * @return relative xpath
     */
    private String getRelativeAttributePath(String attributeName) {
        return mCurrentXmlNodePath.getRelativePath() + "/@" + attributeName;
    }

}
