package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;


import java.io.Serializable;

/**
 * Describes single rule for mathing templates to datafeed items.
 * <p>
 * Created by Rafal Ochtera on 2014-10-20.
 */
public class RequiredField implements Serializable {
    public String getRegexName() {
        return regexName;
    }

    public void setRegexName(String regexName) {
        this.regexName = regexName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getMatch() {
        return match;
    }

    public void setMatch(Object match) {
        this.match = match;
    }

    public boolean isAllowEmpty() {
        return allowEmpty;
    }

    public void setAllowEmpty(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }

    /**
     * name of the usingField this rule requires to be present in datafeed item
     */
    private String name;
    /**
     * Object read from JSON. For Strings field has to match exactly after regex has be run on it.
     * For other types (Boolean, Double) values should be equal to match.
     * Null for no match required
     */
    private Object match;
    /**
     * flag if the items field can be empty after regex has been run on it
     */
    private boolean allowEmpty;
    /**
     * name of the regex to run on fields content, regexes come form StripHTMLTagsForJSON file.
     */
    private String regexName;

}
