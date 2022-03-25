package com.kinetise.data.descriptors.types;

import com.kinetise.data.parsermanager.xmlparser.attributes.AGFeedFormatXmlAttributeValues;

public enum AGFeedFormatType {
    XML,
    JSON;

    public static AGFeedFormatType getFormatType(String type) {
        return type.equals(AGFeedFormatXmlAttributeValues.XML) ? AGFeedFormatType.XML : AGFeedFormatType.JSON;
    }
}
