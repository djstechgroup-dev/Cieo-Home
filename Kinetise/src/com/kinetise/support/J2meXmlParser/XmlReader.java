package com.kinetise.support.J2meXmlParser;

import com.kinetise.helpers.unescapeUtils.StringEscapeUtils;

import java.util.HashMap;

public class XmlReader {

    private static HashMap<Character, Boolean> mEscapedStringsMap = new HashMap<Character, Boolean>();

    static {
        mEscapedStringsMap.put('\n', true);
        mEscapedStringsMap.put('\r', true);
        mEscapedStringsMap.put('\t', true);
        mEscapedStringsMap.put(' ', true);
    }

    private static String xmlCode = null;
    private static int pointerIndex = 0;
    private static int pointerNodParamIndex = 0;
    private static boolean errorXml = false;

    /*
     * zmienen statyczne funkcji
     */
    private static boolean nodChange = false;
    private static int IndexOfParamName = -1;

    private static String string_nodXml = null;

    public static void setXml(String _xmlCode) {
        xmlCode = _xmlCode;
        reset();
    }

    public static void deleteXml() {
        xmlCode = null;
    }

    public static void reset() {
        pointerIndex = 0;
        pointerNodParamIndex = 0;
        nodChange = false;
        IndexOfParamName = -1;
        string_nodXml = null;
        errorXml = false;
    }

    public static String getXml() {
        return xmlCode;
    }

    public static boolean goToNextNod() {
        if (xmlCode != null && pointerIndex > -1) {
            char sign;
            String checkCDATA;
            nodChange = true;

            //search in xmlCode the "<" character
            pointerIndex = xmlCode.indexOf("<", pointerIndex);
            if (pointerIndex < 0)
                return false;
            pointerIndex++;

            if (pointerIndex < xmlCode.length()) {
                //search in xmlCode next character as a sign of "<"
                sign = xmlCode.charAt(pointerIndex);

                if (pointerIndex + "<![CDATA[".length() < xmlCode.length()) {
                    checkCDATA = xmlCode.substring(pointerIndex - 1, pointerIndex - 1 + "<![CDATA[".length());
                    if (checkCDATA.equals("<![CDATA["))
                        pointerIndex = xmlCode.indexOf("]]>", pointerIndex) + "]]>".length();
                }
            } else {
                errorXml = true;
                return false;
            }

            //if character placed next to "<" is ! or ? starts new searching.
            //Nod can't starts from character like ! or ?
            if (sign == '!' || sign == '?')
                goToNextNod();
            return true;
        }
        return false;
    }

    public static String getNodName() {
        if (xmlCode != null && pointerIndex > -1) {
            int IndexOfSignNextToNodName;
            int IndexOfSignSlash = xmlCode.indexOf("/>", pointerIndex + 1);
            int IndexOfSignEndNod = xmlCode.indexOf('>', pointerIndex);
            int IndexOfSignSpace = xmlCode.indexOf(' ', pointerIndex);

            IndexOfSignNextToNodName = IndexOfSignEndNod;

            if (IndexOfSignSlash != -1 && IndexOfSignSlash < IndexOfSignEndNod)
                IndexOfSignNextToNodName = IndexOfSignSlash;

            if (IndexOfSignSpace != -1 && IndexOfSignSpace < IndexOfSignNextToNodName)
                IndexOfSignNextToNodName = IndexOfSignSpace;

            return xmlCode.substring(pointerIndex, IndexOfSignNextToNodName);
        }
        return null;
    }

    //Zwraca wartosc dla podanego parametru
    public static String getNodAttributeValue(String _paramName) {
        String searchingString = " " + _paramName + "=\"";

        if (nodChange) {
            pointerNodParamIndex = 0;
            string_nodXml = getNod();
            nodChange = false;
        }

        IndexOfParamName = string_nodXml.indexOf(searchingString);

        if (IndexOfParamName != -1) {
            int pointerIndexForValue = IndexOfParamName + searchingString.length();
            int endPointerIndexForValue = string_nodXml.indexOf("\"", pointerIndexForValue);
            if (endPointerIndexForValue < 0)
                endPointerIndexForValue = 0;
            return string_nodXml.substring(pointerIndexForValue, endPointerIndexForValue);
        }

        return null;
    }

    //Zwraca kolerny atrybut tablicy
    public static int goToNextAttribute() {
        if (nodChange) {
            pointerNodParamIndex = 0;
            string_nodXml = getNod();
            nodChange = false;
        }

        String searchingString;
        if (pointerNodParamIndex == 0) {
            searchingString = " ";
        } else {
            searchingString = "\" ";
        }

        pointerNodParamIndex = string_nodXml.indexOf(searchingString, pointerNodParamIndex);

        if (pointerNodParamIndex != -1) {
            pointerNodParamIndex += searchingString.length();
        }

        return pointerNodParamIndex;
    }

    private static String escapeString(String str, boolean leaveSingleSpaces) {

        int length = str.length();
        StringBuilder builder = new StringBuilder(length);
        Character lastChar = null;
        boolean dontCutWhitespaces = false;
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if(c == '"'){
            	dontCutWhitespaces = !dontCutWhitespaces;
            }
            if (!mEscapedStringsMap.containsKey(c)) {
                builder.append(c);
            } else {
                if ((leaveSingleSpaces && lastChar != ' ' && c == ' ') || (dontCutWhitespaces)) {
                    builder.append(c);
                }
            }

            lastChar = c;
        }

        return builder.toString();
    }

    public static String getAttributeName() {
        try {
            int endAttributeName = string_nodXml.indexOf('=', pointerNodParamIndex);
            String attrSubstr = string_nodXml.substring(pointerNodParamIndex, endAttributeName);
            return escapeString(attrSubstr, false);
        } catch (Exception e){
           //XmlReader","node ends with /> which was parsed as additional argument in item
        }
            return null;
    }

    public static String getAttributeValue() {
        int startAttributeValue = string_nodXml.indexOf('"', pointerNodParamIndex) + 1;
        int endAttributeValue = string_nodXml.indexOf('"', startAttributeValue);
        /* Ok, heres why i change this line:
           for eg. name=" abcd "
           when searching for next attribute we were in place like name=(start from here)
           its buggy so i moved the pointer after the attribute value
         */
        pointerNodParamIndex = endAttributeValue;
        String value = string_nodXml.substring(startAttributeValue, endAttributeValue);
        return StringEscapeUtils.unescapeXML(value);
    }

    //Zwraca zawartosci noda pomiedzy nodem otwierajacym, a zamykajacym.
    //Kiedy nie ma zawartosci zwraca null.
    public static String getNodData() {
        String data;
        String name = getNodName();

        int indexOfEndOpeningNod = xmlCode.indexOf('>', pointerIndex);
        int indexOfSlashEnd = xmlCode.indexOf("/>", indexOfEndOpeningNod - 1);
        int indexOfEndingNod = xmlCode.indexOf("</" + name, indexOfEndOpeningNod);

        if (indexOfSlashEnd > 0 && indexOfEndOpeningNod > indexOfSlashEnd) {
            return null;
        }

        data = xmlCode.substring(indexOfEndOpeningNod + ">".length(), indexOfEndingNod);
        return data;
    }

    //Zwraca naglowek noda.
    private static String getNod() {
        String temp = xmlCode.substring(pointerIndex, xmlCode.indexOf(">", pointerIndex));
        temp = escapeString(temp, true);
        return temp;
    }

    public static boolean isErrorXml() {
        return errorXml;
    }

    public static boolean isCurrentNodeShortTag() {
        int indexOfEndOpeningNod = xmlCode.indexOf('>', pointerIndex);
        int indexOfSlashEnd = xmlCode.indexOf("/>", indexOfEndOpeningNod - 1);

        return indexOfSlashEnd > 0 && indexOfEndOpeningNod > indexOfSlashEnd;

    }
}
