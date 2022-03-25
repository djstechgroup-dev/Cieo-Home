package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;

import java.util.ArrayList;

public class Namespaces {

    private ArrayList<NamespaceElement> elements = new ArrayList<NamespaceElement>();

    public void add(NamespaceElement element) {
        elements.add(element);
    }

    public NamespaceElement[] getNamespaceElements() {
        NamespaceElement[] content = new NamespaceElement[elements.size()];
        return elements.toArray(content);
    }

    public int size() {
        return elements != null ? elements.size() : 0;
    }

    public Namespaces copy() {
        Namespaces copied = new Namespaces();

        for (NamespaceElement elem : elements) {
            copied.add(elem.copy());
        }

        return copied;
    }

    public String getUriByPrefix(String prefix) {
        for (NamespaceElement ne : elements) {
            if (ne.getPrefix().equals(prefix)) {
                return ne.getNamespace();
            }
        }
        return null;
    }

    public String getPrefixByUri(String pUri) {

        for (NamespaceElement nsp : elements) {
            if (nsp.getNamespace().equals(pUri)) {
                return (nsp).getPrefix();
            }
        }
        return null;
    }
}
