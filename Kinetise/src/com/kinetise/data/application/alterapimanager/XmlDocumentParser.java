package com.kinetise.data.application.alterapimanager;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class XmlDocumentParser {
    private Document mDocument;
    private XPathFactory mXpathFactory = XPathFactory.newInstance();
    private XPath mXpath = mXpathFactory.newXPath();

    public XmlDocumentParser(InputStream xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setValidating(false);
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder;
        docBuilder = docFactory.newDocumentBuilder();
        mDocument = docBuilder.parse(new InputSource(xml));
    }

    public boolean containsNode(String xPathQuery) {
        NodeList messageNode;
        try {
            mXpath = mXpathFactory.newXPath();
            messageNode = (NodeList) mXpath.evaluate(xPathQuery, mDocument,
                    XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return false;
        }

        if (messageNode == null) {
            return false;
        } else {
            return messageNode.getLength() > 0;
        }

    }

    public NodeList getNode(String xPathQuery) {
        NodeList messageNode = null;
        try {
            mXpath = mXpathFactory.newXPath();
            messageNode = (NodeList) mXpath.evaluate(xPathQuery, mDocument,
                    XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return messageNode;
    }


}
