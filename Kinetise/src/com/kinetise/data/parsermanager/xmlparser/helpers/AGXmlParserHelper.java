package com.kinetise.data.parsermanager.xmlparser.helpers;

import android.support.annotation.NonNull;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.types.AGAlignType;
import com.kinetise.data.descriptors.types.AGDatePickerModeType;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.data.descriptors.types.AGOrientationType;
import com.kinetise.data.descriptors.types.AGScreenTransition;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.descriptors.types.AGSizeModeType;
import com.kinetise.data.descriptors.types.AGUnitType;
import com.kinetise.data.descriptors.types.AGVAlignType;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGDatePickerXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGHttpXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGScreenTransitionAttributes;
import com.kinetise.data.parsermanager.xmlparser.attributes.XmlAttributeValues;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.helpers.time.DateSourceType;
import com.kinetise.support.J2meXmlParser.XmlReader;

/**
 * Helper used for parsing xml
 */
public class AGXmlParserHelper {

    public static final String HEXADECIMAL_PREFIX = "0x";
    public static final int ARGB_LENGTH = 8;
    public static final int COLOR_TRANSPARENT = 0;
    public static final String NOTIFICATION_BAR_MODE_DARK = "dark";
    public static final String NOTIFICATION_BAR_MODE_LIGHT = "light";

    public static AGSizeDesc parseStringToSizeDesc(String value) {
        if (isKPX(value)) {
            return getSizeDescFromKPX(value);
        } else if (isPercent(value)) {
            return getSizeDescFromPercent(value);
        } else if (isMax(value)) {
            return AGSizeDesc.MAX;
        } else if (isMin(value)) {
            return AGSizeDesc.MIN;
        } else {
            throw new IllegalArgumentException(String.format("Cannot get SizeDesc value from '%s'", value));
        }
    }

    @NonNull
    private static AGSizeDesc getSizeDescFromKPX(String value) {
        int val = convertToInt(value, XmlAttributeValues.KPX_STRING);
        return new AGSizeDesc(val, AGUnitType.KPX);
    }

    @NonNull
    private static AGSizeDesc getSizeDescFromPercent(String value) {
        int val = convertToInt(value, XmlAttributeValues.PERCENT_STRING);
        return new AGSizeDesc(val, AGUnitType.PERCENT);
    }

    public static AGSizeDesc parseStringToSizeDescWithoutMinMax(String value) {
        if (isMax(value) | isMin(value)) {
            throw new IllegalArgumentException(String.format("Cannot get SizeDesc value from '%s'", value));
        }
        return parseStringToSizeDesc(value);
    }

    public static AGSizeDesc getSizeDescFromKPXString(String value) {
        if (!isKPX(value)) {
            throw new IllegalArgumentException(String.format("Cannot get SizeDesc value from '%s'", value));
        }

        return getSizeDescFromKPX(value);
    }

    public static boolean convertYesNoToBoolean(String value) {
        if (value.equals(XmlAttributeValues.YES)) {
            return true;
        } else if (value.equals(XmlAttributeValues.NO)) {
            return false;
        } else {
            throw new IllegalArgumentException(String.format("Cannot parse %s to boolean", value));
        }
    }

    /**
     * Converts given value to int
     *
     * @param value      string value
     * @param unitString string that represents unit for example 'kpx' '%'
     * @return integer reperesentation of value
     */
    public static int convertToInt(String value, String unitString) {
        return Integer.parseInt(value.replace(unitString, ""));
    }

    /**
     * Converts given value to long
     *
     * @param value string value
     * @return long reperesentation of value
     */
    public static long convertToLong(String value) {
        return Long.parseLong(value);
    }

    /**
     * Converts given value to int, if value is _NONE_ than returns -1
     *
     * @param value string value
     * @return integer reperesentation of value
     */
    public static int convertToIntIncludeNONE(String value) {
        return !value.equals(XmlAttributeValues._NONE) ? Integer.parseInt(value) : -1;
    }

    public static float convertToFloat(String value) {
        try {
            return Float.valueOf(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 1.0f;
        }
    }

    /**
     * Checks if value is in percent
     *
     * @param value to check
     * @return true if value is in percent, false otherwise
     */
    public static boolean isPercent(String value) {
        return value.contains(XmlAttributeValues.PERCENT_STRING);
    }

    /**
     * Checks if value is in kpx
     *
     * @param value to check
     * @return true if value is in kpx, false otherwise
     */
    public static boolean isKPX(String value) {
        return value.contains(XmlAttributeValues.KPX_STRING);
    }

    /**
     * Checks if value is max
     *
     * @param value to check
     * @return true if value is in max, false otherwise
     */
    public static boolean isMax(String value) {
        return value.contains(XmlAttributeValues.MAX_STRING);
    }

    /**
     * Checks if value is in min
     *
     * @param value to check
     * @return true if value is min, false otherwise
     */
    public static boolean isMin(String value) {
        return value.contains(XmlAttributeValues.MIN_STRING);
    }

    /**
     * Converts hex color to integer
     *
     * @param value to check
     * @return integer representing given color
     */
    public static int getColorFromHex(String value) {
        String tmp = value;

        if (value.contains(HEXADECIMAL_PREFIX))
            tmp = value.substring(HEXADECIMAL_PREFIX.length());

        if (tmp.length() < ARGB_LENGTH) {
            tmp = "FF" + tmp;
        }
        try {
            return hexToInt(tmp.trim());
        } catch (Exception e) {

            return COLOR_TRANSPARENT;
        }
    }

    public static int hexToInt(String hexadecimalString) {
        return parseColor("#" + hexadecimalString);
    }

    private static int parseColor(String colorString) {
        if (colorString.charAt(0) == '#') {
            // Use a long to avoid rollovers on #ffXXXXXX
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                // Set the alpha value
                color |= 0x00000000ff000000;
            } else if (colorString.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int) color;
        } else
            throw new IllegalArgumentException("Unknown color");
    }


    /**
     * Converts given value to orientation type throws {@link IllegalArgumentException} if value does not match any orientation
     *
     * @param value of orientation
     * @return AGOrientationType representing value
     */
    public static AGOrientationType getOrientationType(String value) {
        if (value.equals(XmlAttributeValues.BOTH)) {
            return AGOrientationType.BOTH;
        } else if (value.equals(XmlAttributeValues.LANDSCAPE)) {
            return AGOrientationType.LANDSCAPE;
        } else if (value.equals(XmlAttributeValues.PORTRAIT)) {
            return AGOrientationType.PORTRAIT;
        } else {
            throw new IllegalArgumentException(String.format("Unknown Orientation Type : '%s'", value));
        }
    }

    /**
     * Converts given value to {@link AGAlignType} type throws {@link IllegalArgumentException} if value does not match any {@link AGAlignType}
     *
     * @param value of Align
     * @return {@link AGAlignType} representing value
     */
    public static AGAlignType getAlignType(String value) {

        if (value.equals(XmlAttributeValues.LEFT)) {
            return AGAlignType.LEFT;
        } else if (value.equals(XmlAttributeValues.RIGHT)) {
            return AGAlignType.RIGHT;
        } else if (value.equals(XmlAttributeValues.CENTER)) {
            return AGAlignType.CENTER;
        } else if (value.equals(XmlAttributeValues.DISTRIBUTED)) {
            return AGAlignType.DISTRIBUTED;
        } else if (value.equals(XmlAttributeValues._NONE)) {
            return null;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unknown Align Type : '%s'", value));
        }
    }

    /**
     * Converts given value to {@link AGVAlignType} type throws {@link IllegalArgumentException} if value does not match any {@link AGVAlignType}
     *
     * @param value of VAlign
     * @return {@link AGVAlignType} representing value
     */
    public static AGVAlignType getVAlignType(String value) {

        if (value.equals(XmlAttributeValues.BOTTOM)) {
            return AGVAlignType.BOTTOM;
        } else if (value.equals(XmlAttributeValues.TOP)) {
            return AGVAlignType.TOP;
        } else if (value.equals(XmlAttributeValues.CENTER)) {
            return AGVAlignType.CENTER;
        } else if (value.equals(XmlAttributeValues.DISTRIBUTED)) {
            return AGVAlignType.DISTRIBUTED;
        } else if (value.equals(XmlAttributeValues._NONE)) {
            return null;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unknown Align Type : '%s'", value));
        }
    }

    public static String getStringOrNullIfNone(String value) {
        if (value.equals(XmlAttributeValues._NONE))
            return null;

        return value;
    }

    public static int getIntOrNegativeIfNone(String value) {
        if (value.equals(XmlAttributeValues._NONE))
            return -1;

        return Integer.parseInt(value);
    }

    /**
     * Returns {@link AGSizeModeType} representing value, throws {@link IllegalArgumentException} if value does not match any SizeMode
     *
     * @param value
     * @return
     */
    public static AGSizeModeType getSizeModeFromString(String value) {
        if (value.equals(XmlAttributeValues.LONGEDGE)) {
            return AGSizeModeType.LONGEDGE;
        } else if (value.equals(XmlAttributeValues.SHORTEDGE)) {
            return AGSizeModeType.SHORTEDGE;
        } else if (value.equals(XmlAttributeValues.STRETCH)) {
            return AGSizeModeType.STRETCH;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unknown size mode value: [%s]", value));
        }
    }

    /**
     * Returns byte representing value, throws {@link IllegalArgumentException} if value does not match any Src
     *
     * @param value
     * @return
     */
    public static DateSourceType getDateSrcFromString(String value) {
        if (value.equals("local")) {
            return DateSourceType.LOCAL;
        } else if (value.equals("node")) {
            return DateSourceType.NODE;
        } else if (value.equals("internet")) {
            return DateSourceType.INTERNET;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unknown datesrc value: [%s]", value));
        }

    }

    public static AGDatePickerModeType getDatePickerModeType(String value) {
        if (value.equals(AGDatePickerXmlAttributes.DATE))
            return AGDatePickerModeType.DATE;
        else if (value.equals(AGDatePickerXmlAttributes.TIME))
            return AGDatePickerModeType.TIME;
        else
            return AGDatePickerModeType.DATETIME;
    }

    public static AGHttpMethodType getHttpMethodType(String value) {
        if (value.equals(AGHttpXmlAttributes.HTTP_METHOD_POST))
            return AGHttpMethodType.POST;
        else if (value.equals(AGHttpXmlAttributes.HTTP_METHOD_PUT))
            return AGHttpMethodType.PUT;
        else if (value.equals(AGHttpXmlAttributes.HTTP_METHOD_DELETE))
            return AGHttpMethodType.DELETE;
        else if (value.equals(AGHttpXmlAttributes.HTTP_METHOD_PATCH))
            return AGHttpMethodType.PATCH;
        else
            return AGHttpMethodType.GET;
    }

    public static AGScreenTransition getScreenTransition(String value) {
        if (value.equals(AGScreenTransitionAttributes.NONE))
            return AGScreenTransition.NONE;
        else if (value.equals(AGScreenTransitionAttributes.FADE))
            return AGScreenTransition.FADE;
        else if (value.equals(AGScreenTransitionAttributes.SLIDE_LEFT))
            return AGScreenTransition.SLIDE_LEFT;
        else if (value.equals(AGScreenTransitionAttributes.SLIDE_RIGHT))
            return AGScreenTransition.SLIDE_RIGHT;
        else if (value.equals(AGScreenTransitionAttributes.COVER_FROM_LEFT))
            return AGScreenTransition.COVER_FROM_LEFT;
        else if (value.equals(AGScreenTransitionAttributes.COVER_FROM_RIGHT))
            return AGScreenTransition.COVER_FROM_RIGHT;
        else if (value.equals(AGScreenTransitionAttributes.COVER_FROM_TOP))
            return AGScreenTransition.COVER_FROM_TOP;
        else if (value.equals(AGScreenTransitionAttributes.COVER_FROM_BOTTOM))
            return AGScreenTransition.COVER_FROM_BOTTOM;
        else if (value.equals(AGScreenTransitionAttributes.UNCOVER_TO_LEFT))
            return AGScreenTransition.UNCOVER_TO_LEFT;
        else if (value.equals(AGScreenTransitionAttributes.UNCOVER_TO_RIGHT))
            return AGScreenTransition.UNCOVER_TO_RIGHT;
        else if (value.equals(AGScreenTransitionAttributes.UNCOVER_TO_TOP))
            return AGScreenTransition.UNCOVER_TO_TOP;
        else if (value.equals(AGScreenTransitionAttributes.UNCOVER_TO_BOTTOM))
            return AGScreenTransition.UNCOVER_TO_BOTTOM;
        else
            return AGScreenTransition.NONE;
    }


    /**
     * Loads XMLNode value
     *
     * @return node value, if value is in CDATA than cuts Opening and closing tag
     */
    public static String loadXmlNodeValue() {
        String string_text = XmlReader.getNodData();

        if (string_text != null) {
            string_text = string_text.trim();

            if (string_text.startsWith("<![CDATA[")) {
                string_text = string_text.replace("<![CDATA[", "");
                string_text = string_text.replace("]]>", "");
            }
        }

        return string_text;
    }

    public static VariableDataDesc parseStringAsAction(String value, AbstractAGElementDataDesc desc) {
        String actionString = AGXmlParserHelper.getStringOrNullIfNone(value);
        return AGXmlActionParser.createVariable(actionString, desc);
    }

    public static boolean getColorModeType(String value) {
        if (value != null && value.equals(NOTIFICATION_BAR_MODE_LIGHT))
            return true;
        else if (value != null && value.equals(NOTIFICATION_BAR_MODE_DARK))
            return false;

        return false;
    }
}
