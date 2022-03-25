package com.kinetise.data.application.formdatautils;

import com.google.gson.annotations.SerializedName;

public class FormValidationRule {

    public static final String TYPE_REQUIRED = "REQUIRED";
    public static final String TYPE_REGEX = "REGEX";
    public static final String TYPE_SAME_AS = "SAME_AS";
    public static final String TYPE_JAVASCRIPT = "JAVASCRIPT";

    @SerializedName("type")
    private String type;

    @SerializedName("controlid")
    private String controlId;

    @SerializedName("code")
    private String code;

    @SerializedName("regex")
    private String regex;

    @SerializedName("message")
    private String message;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getControlId() {
        return controlId;
    }

    public void setControlId(String controlId) {
        this.controlId = controlId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public FormValidationRule copy() {
        FormValidationRule copied = new FormValidationRule();
        copied.setCode(code);
        copied.setControlId(controlId);
        copied.setMessage(message);
        copied.setType(type);
        copied.setRegex(regex);
        return copied;
    }
}
