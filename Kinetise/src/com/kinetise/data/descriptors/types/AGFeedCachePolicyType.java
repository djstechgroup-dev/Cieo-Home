package com.kinetise.data.descriptors.types;

import com.kinetise.data.parsermanager.xmlparser.attributes.AGFeedCachePolicyXmlAttributeValues;

public enum AGFeedCachePolicyType {
    FRESH_DATA,
    MAX_AGE,
    REFRESH_EVERY,
    CACHE_DATA_REFRESH_EVERY,
    CACHE_DATA,
    CACHE_DATA_AND_REFRESH,
    LIVE,
    NO_STORE;

    public static AGFeedCachePolicyType getCachePolicyType(String type) {
        if (type.equals(AGFeedCachePolicyXmlAttributeValues.FRESH_DATA)) {
            return AGFeedCachePolicyType.FRESH_DATA;
        } else if (type.equals(AGFeedCachePolicyXmlAttributeValues.MAX_AGE)) {
            return AGFeedCachePolicyType.MAX_AGE;
        } else if (type.equals(AGFeedCachePolicyXmlAttributeValues.REFRESH_EVERY)) {
            return AGFeedCachePolicyType.REFRESH_EVERY;
        } else if (type.equals(AGFeedCachePolicyXmlAttributeValues.CACHE_DATA_REFRESH_EVERY)) {
            return AGFeedCachePolicyType.CACHE_DATA_REFRESH_EVERY;
        } else if (type.equals(AGFeedCachePolicyXmlAttributeValues.CACHE_DATA)) {
            return AGFeedCachePolicyType.CACHE_DATA;
        } else if (type.equals(AGFeedCachePolicyXmlAttributeValues.CACHE_DATA_AND_REFRESH)) {
            return AGFeedCachePolicyType.CACHE_DATA_AND_REFRESH;
        } else if (type.equals(AGFeedCachePolicyXmlAttributeValues.LIVE)) {
            return AGFeedCachePolicyType.LIVE;
        } else if (type.equals(AGFeedCachePolicyXmlAttributeValues.NO_STORE)) {
            return AGFeedCachePolicyType.NO_STORE;
        } else {
            throw new IllegalArgumentException("Cannot parse cache policy type: " + type);
        }
    }
}
