package com.kinetise.data.descriptors.datadescriptors;

public class AGHyperlinkDataDesc extends AGTextDataDesc {
    private int mActiveColor;

    public AGHyperlinkDataDesc(String id) {
        super(id);
    }

    public int getActiveColor() {
        return mActiveColor;
    }

    public void setActiveColor(int color) {
        mActiveColor = color;
    }

    @Override
    public AGHyperlinkDataDesc createInstance() {
        return new AGHyperlinkDataDesc(getId());
    }

    @Override
    public AGHyperlinkDataDesc copy() {
        AGHyperlinkDataDesc copied = (AGHyperlinkDataDesc) super.copy();
        copied.mActiveColor = mActiveColor;

        return copied;
    }
}
