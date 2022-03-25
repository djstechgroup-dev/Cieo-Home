package com.kinetise.data.descriptors.datadescriptors.components;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.DecoratorCalcDescriptor;
import com.kinetise.data.descriptors.types.AGAlignType;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.descriptors.types.AGSizeModeType;
import com.kinetise.data.descriptors.types.AGVAlignType;

public class DecoratorDescriptor {
    protected AGSizeDesc mWidth;
    protected AGSizeDesc mHeight;
    protected AGAlignType mAlign;
    protected AGVAlignType mVAlign;
    protected ImageDescriptor mImageDescriptor;
    protected ImageDescriptor mActiveImageDescriptor;
    protected DecoratorCalcDescriptor mCalcDescriptor;

    public DecoratorDescriptor() {
        mImageDescriptor = new ImageDescriptor();
        mActiveImageDescriptor = new ImageDescriptor();
        mCalcDescriptor = new DecoratorCalcDescriptor();
    }

    public void setSizeMode(AGSizeModeType sizeMode) {
        mImageDescriptor.setSizeMode(sizeMode);
        mActiveImageDescriptor.setSizeMode(sizeMode);
    }

    public DecoratorCalcDescriptor getCalcDescriptor() {
        return mCalcDescriptor;
    }

    public void setImageSrc(VariableDataDesc imageSrc) {
        mImageDescriptor.setImageSrc(imageSrc);
    }

    public void setActiveSrc(VariableDataDesc activeSrc) {
        mActiveImageDescriptor.setImageSrc(activeSrc);
    }

    public void setImageDescriptor(ImageDescriptor imageDescriptor) {
        mImageDescriptor = imageDescriptor;
    }

    public void setActiveImageDescriptor(ImageDescriptor activeImageDescriptor) {
        mActiveImageDescriptor = activeImageDescriptor;
    }

    public AGSizeDesc getWidth() {
        return mWidth;
    }

    public void setWidth(AGSizeDesc width) {
        mWidth = width;
    }

    public AGSizeDesc getHeight() {
        return mHeight;
    }

    public void setHeight(AGSizeDesc height) {
        mHeight = height;
    }

    public AGAlignType getAlign() {
        return mAlign;
    }

    public void setAlign(AGAlignType align) {
        mAlign = align;
    }

    public AGVAlignType getVAlign() {
        return mVAlign;
    }

    public void setVAlign(AGVAlignType VAlign) {
        mVAlign = VAlign;
    }

    public ImageDescriptor getActiveImageDescriptor() {
        return mActiveImageDescriptor;
    }

    public ImageDescriptor getImageDescriptor() {
        return mImageDescriptor;
    }

    public DecoratorDescriptor copy(AbstractAGElementDataDesc parent) {
        DecoratorDescriptor result = new DecoratorDescriptor();
        result.mImageDescriptor = mImageDescriptor.copy(parent);
        result.mActiveImageDescriptor = mActiveImageDescriptor.copy(parent);
        result.setWidth(mWidth);
        result.setHeight(mHeight);
        result.setAlign(mAlign);
        result.setVAlign(mVAlign);
        return result;
    }

}
