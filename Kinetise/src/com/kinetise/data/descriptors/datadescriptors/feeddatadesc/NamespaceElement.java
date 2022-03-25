package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;

public class NamespaceElement {

    private String mNamespace;
    private String mPrefix;

    public NamespaceElement() {
    }

    public NamespaceElement(String prefix, String namespace) {
        mNamespace = namespace;
        mPrefix = prefix;
    }

    public String getNamespace() {
        return mNamespace;
    }

    public String getPrefix() {
        return mPrefix;
    }

    public void setNamespace(String namespace) {
        mNamespace = namespace;
    }

    public void setPrefix(String prefix) {
        mPrefix = prefix;
    }

    public NamespaceElement copy() {
        return new NamespaceElement(mPrefix, mNamespace);
    }
}
