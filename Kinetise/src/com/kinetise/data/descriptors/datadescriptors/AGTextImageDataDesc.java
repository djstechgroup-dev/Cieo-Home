package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.datadescriptors.components.IImageDescriptor;
import com.kinetise.data.descriptors.datadescriptors.components.ImageDescriptor;
import com.kinetise.data.descriptors.types.AGTextVAlignType;
import com.kinetise.data.descriptors.types.TextPosition;

public class AGTextImageDataDesc extends AGTextDataDesc implements IImageDescriptor {
    private ImageDescriptor mImageDescriptor;
    private TextPosition mTextPosition;
    private boolean mShowLoading = true;

    public AGTextImageDataDesc(String id) {
        super(id);
        getTextDescriptor().setTextVAlign(AGTextVAlignType.CENTER);
        mImageDescriptor = new ImageDescriptor();
    }

    @Override
    public void setFeedItemIndex(int feedItemIndex) {
        super.setFeedItemIndex(feedItemIndex);
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return mImageDescriptor;
    }

    @Override
    public void setImageDescriptor(ImageDescriptor imageDescriptor) {
        mImageDescriptor = imageDescriptor;
    }

    @Override
    public AGTextImageDataDesc createInstance() {
        return new AGTextImageDataDesc(getId());
    }

    @Override
    public AGTextImageDataDesc copy() {
        AGTextImageDataDesc copied = (AGTextImageDataDesc) super.copy();
        copied.setImageDescriptor(mImageDescriptor.copy(copied));
        copied.setShowLoading(mShowLoading);
        copied.mTextPosition = mTextPosition;
        return copied;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mImageDescriptor.resolveVariable();
    }

    public void setShowLoading(boolean showLoading) {
        mShowLoading = showLoading;
    }

    public boolean getShowLoading() {
        return mShowLoading;
    }

    public TextPosition getTextPosition() {
        return mTextPosition;
    }

    public void setTextPosition(TextPosition textPosition) {
        mTextPosition = textPosition;
    }

}

