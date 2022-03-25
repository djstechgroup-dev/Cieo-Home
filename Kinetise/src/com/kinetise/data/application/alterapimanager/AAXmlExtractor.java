package com.kinetise.data.application.alterapimanager;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

public class AAXmlExtractor extends AAExtractor{
    XmlDocumentParser mXmlDocumentParser;

    @Override
    protected void initParser(String response) throws InvalidAlterAPIResponse {
        super.initParser(response);

        InputStream responseStream = IOUtils.toInputStream(response);
        try {
            mXmlDocumentParser = new XmlDocumentParser(responseStream);
        } catch (Exception e){
            throw new InvalidAlterAPIResponse();
        } finally {
            IOUtils.closeQuietly(responseStream);
        }
        if (!mXmlDocumentParser.containsNode(RESPONSE)) {
            throw new InvalidAlterAPIResponse();
        }
    }

    protected void parseMessages() {
        String messageNodeXPath = concatXPath(RESPONSE, MESSAGE_NODE);
        Node message = mXmlDocumentParser.getNode(messageNodeXPath).item(0);
        if (message != null) {
            AAMessage alterApiMessage = new AAMessage();
            final String messageValueXPath = concatXPath(RESPONSE, MESSAGE_NODE, VALUE_NODE);
            NodeList messageValues = mXmlDocumentParser.getNode(messageValueXPath);
            int length = messageValues.getLength();
            String messageValue;
            for (int i = 0; i < length; i++) {
                Node valueNode = messageValues.item(i);
                messageValue = getTextValue(valueNode);
                alterApiMessage.addMessageValue(messageValue);
            }
            if (length > 0) {
                mResponse.message = alterApiMessage;
            }
        }
    }

    protected void parseExpiredUrls() {
        String expiredUrlsNodeXPath = concatXPath(RESPONSE, EXPIRED_URLS_NODE);
        Node expiredUrls = mXmlDocumentParser.getNode(expiredUrlsNodeXPath).item(0);
        if (expiredUrls != null) {
            final String urlXPath = concatXPath(RESPONSE, EXPIRED_URLS_NODE, URL_NODE);
            NodeList urls = mXmlDocumentParser.getNode(urlXPath);
            int length = urls.getLength();
            for (int i = 0; i < length; i++) {
                Node valueNode = urls.item(i);
                mResponse.expiredUrls.add(getTextValue(valueNode));
            }
        }
    }

    protected void parseSessionId() {
        String sessionIdXPath = concatXPath(RESPONSE, SESSION_ID_NODE);
        Node sessionId = mXmlDocumentParser.getNode(sessionIdXPath).item(0);
        if (sessionId != null) {
            String sessionIdValue;
            sessionIdValue = getTextValue(sessionId);

            if (sessionIdValue != null && sessionIdValue.length() > 0) {
                mResponse.sessionId = sessionIdValue;
            }

        }
    }

    protected void parseAppVariables() {
        String appVariablesNodeXPath = concatXPath(RESPONSE, APP_VARIABLES_NODE);
        Node appVariables = mXmlDocumentParser.getNode(appVariablesNodeXPath).item(0);
        if (appVariables != null) {
            final String variableXPath = concatXPath(RESPONSE, APP_VARIABLES_NODE, VARIABLE_NODE);
            NodeList variables = mXmlDocumentParser.getNode(variableXPath);
            int length = variables.getLength();
            for (int i = 0; i < length; i++) {
                parseApplicationVariable(variables.item(i));
            }
        }
    }

    protected void parseApplicationVariable(Node variableNode) {
        NodeList variableNodes = variableNode.getChildNodes();
        String key = null;
        String value = null;
        for (int j=0; j < variableNodes.getLength(); j++) {
            Node node = variableNodes.item(j);
            if (node.getNodeName().equals(KEY_NODE) ) {
                key = getNodeTextContent(node);
            } else if (node.getNodeName().equals(VALUE_NODE)) {
                value = getNodeTextContent(node);
                if (value == null)
                    value = "";
            }
        }
        if (key != null && value != null) {
            mResponse.applicationVariables.put(key, value);
        }
    }

    private String getNodeTextContent(Node node) {
        NodeList keyChilds = node.getChildNodes();
        if (keyChilds.getLength() > 0) {
            return keyChilds.item(0).getTextContent();
        }
        return null;
    }

    private String getTextValue(Node valueNode) {
        String messageValue;
        if (valueNode.getNodeType() == Node.TEXT_NODE) {
            messageValue = valueNode.getNodeValue();
        } else {
            messageValue = valueNode.getFirstChild().getNodeValue();
        }
        return messageValue;
    }

    private String concatXPath(String... params) {
        int length = params.length;
        if (length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length - 1; i++) {
            sb.append(params[i]).append("/");
        }
        sb.append(params[length - 1]);
        return sb.toString();
    }

}
