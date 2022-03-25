package com.kinetise.data.systemdisplay.views;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGButtonDataDesc;
import com.kinetise.data.sourcemanager.ImageSource;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.helpers.AGViewHelper;

public class AGButtonView<T extends AGButtonDataDesc> extends AGTextImageView<T> {

    protected ImageSource activeImageSource;

    public AGButtonView(SystemDisplay display, T desc) {
        super(display, desc);
        activeImageSource = new ImageSource(mDescriptor.getActiveImageDescriptor(), null);
    }

    @Override
    public void loadAssets() {
        String baseUrl = mDescriptor.getFeedBaseAdress();
        super.loadAssets();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        activeImageSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        super.setDescriptor(descriptor);
        AGButtonDataDesc desc = (AGButtonDataDesc) descriptor;
        activeImageSource.setImageDescriptor(desc.getActiveImageDescriptor());
    }

    protected void setActiveState() {
        if (isButtonPressed)
            return;
        super.setActiveState();

        if (mTextView != null) {
            mTextView.setTextColor(mDescriptor.getActiveColor());
        }
        mDescriptor.setCurrentBorderColor(mDescriptor.getActiveBorderColor());
        mImageView.setImageBitmap(activeImageSource.getBitmap());
    }

    protected void setInactiveState() {
        if (!isButtonPressed)
            return;
        super.setInactiveState();

        if (mTextView != null) {
            mTextView.setTextColor(mDescriptor.getTextDescriptor().getTextColor());
        }
        mDescriptor.setCurrentBorderColor(mDescriptor.getBorderColor());
        mImageView.setImageBitmap(imageSource.getBitmap());
    }

    public void disable() {
        AGViewHelper.setHalftransparentIncludingChildren(this);
    }


}
