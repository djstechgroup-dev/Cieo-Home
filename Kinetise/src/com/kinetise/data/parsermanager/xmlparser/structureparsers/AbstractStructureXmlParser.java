package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.support.J2meXmlParser.XmlReader;

import java.security.InvalidParameterException;

public abstract class AbstractStructureXmlParser<T> {

	/**
	 * Parses stuructore
	 * @return Structure descriptor
	 */
    public T parseStructure() {

        T desc = proceedParseNode();

        return parse(desc);
    }


    protected T proceedParseNode() {

        T desc = createDescriptor(XmlReader.getNodAttributeValue("id"));

        while (XmlReader.goToNextAttribute() > 0) {

            String id = XmlReader.getAttributeName();
            if(id == null)
                break;
            String value = XmlReader.getAttributeValue();

            if (!parseNodeAttribute(desc, id, value)) {
                throw new InvalidParameterException(String.format(
                        "Cannot find definition to parse '%s' attribute on node '%s'",
                        id, getStructureRootNodeName()));
            }
        }

        if (!parseNodeValue(desc)) {
            throw new InvalidParameterException(
                    String.format("Unexpected node value"));
        }

        endParseNode(desc);

        return desc;
    }

    protected void endParseNode(T desc) {
    }

    /**
     * Iterates through node to parse its arguments
     * @param desc descriptior of currently parsed structure
     * @return descriptor passed as parameter
     */
    protected T parse(T desc) {

        String currentStructureRootName = getStructureRootNodeName();

        String nodeName;
        if (!XmlReader.isCurrentNodeShortTag()) {
            while (XmlReader.goToNextNod() && !XmlReader.isErrorXml()) {
                nodeName = XmlReader.getNodName();

                if (nodeName.equals("/" + currentStructureRootName)) {
                    break;
                } else {
                    proceedParseStructure(nodeName.trim(), desc);
                }
            }
        }

        return desc;
    }

    /**
     * Parses value of node
     * @param desc of currently parsed structure
     * @return true if node was successfuly parsed, false otherwise
     */
    protected abstract boolean parseNodeValue(T desc);

    /**
     * Creates descriptor of this structure with given structure id
     * @param id of structure
     * @return Empty descriptor of currently parsed structure
     */
    protected abstract T createDescriptor(String id);

    /**
     * Parses next attribute of node
     * @param descriptor of currently parsed structure
     * @param id
     *@param value @return true if attribute was successfuly parsed, false if no definition for attribute were found
     */
    protected abstract boolean parseNodeAttribute(T descriptor, String id, String value);

    /**
     * Returns this structure NodeName
     * @return
     */
    protected abstract String getStructureRootNodeName();

    /**
     * Proceeds with parsing nodes inside current structure
     * @param nodeName  current node name
     * @param desc current structure descriptor
     */
    protected abstract void proceedParseStructure(String nodeName, T desc);

}
