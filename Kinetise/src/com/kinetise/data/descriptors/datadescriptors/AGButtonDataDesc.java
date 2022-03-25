package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.datadescriptors.components.ITwoStateImageDescriptor;
import com.kinetise.data.descriptors.datadescriptors.components.ImageDescriptor;

public class AGButtonDataDesc extends AGTextImageDataDesc implements ITwoStateImageDescriptor {

    private int mActiveColor;
    private int mActiveBorderColor;
    protected ImageDescriptor mActiveImageDescriptor;

    public AGButtonDataDesc(String id) {
        super(id);
        mActiveImageDescriptor = new ImageDescriptor();
    }

    @Override
    public AGButtonDataDesc createInstance() {
        return new AGButtonDataDesc(getId());
    }

    @Override
    public AGButtonDataDesc copy() {
        AGButtonDataDesc copied = (AGButtonDataDesc) super.copy();
        copied.setActiveImageDescriptor(mActiveImageDescriptor.copy(copied));

        copied.setActiveColor(getActiveColor());
        copied.setActiveBorderColor(getActiveBorderColor());
        return copied;
    }


    public int getActiveColor() {
        return mActiveColor;
    }

    public void setActiveColor(int activeColor) {
        mActiveColor = activeColor;
    }

    public int getActiveBorderColor() {
        return mActiveBorderColor;
    }

    public void setActiveBorderColor(int activeBorderColor) {
        mActiveBorderColor = activeBorderColor;
    }

    @Override
    public int getBorderColor() {
        return super.getBorderColor();
    }

    @Override
    public ImageDescriptor getActiveImageDescriptor() {
        return mActiveImageDescriptor;
    }

    @Override
    public void setActiveImageDescriptor(ImageDescriptor activeImageDescriptor) {
        mActiveImageDescriptor = activeImageDescriptor;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mActiveImageDescriptor.resolveVariable();
    }
}
