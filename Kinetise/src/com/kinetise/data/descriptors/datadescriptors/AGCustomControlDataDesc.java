package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;

import java.util.HashMap;
import java.util.Map;

public class AGCustomControlDataDesc extends AbstractAGViewDataDesc {

    private final String mControlName;
    private Map<String, String> mNodes;

    public AGCustomControlDataDesc(String controlName, String id) {
        super(id);
        mNodes = new HashMap<>();
        mControlName = controlName;
    }

    public void addAttribute(String key, String value) {
        mNodes.put(key, value);
    }

    @Override
    public AGCustomControlDataDesc createInstance() {
        return new AGCustomControlDataDesc(mControlName, getId());
    }

    @Override
    public AGCustomControlDataDesc copy() {
        AGCustomControlDataDesc copied = (AGCustomControlDataDesc) super.copy();
        return copied;
    }

    public String getControlName() {
        return mControlName;
    }

    public String getAttribute(String node) {
        return mNodes.get(node);
    }
}
