package com.kinetise.data.descriptors.calcdescriptors;

public class AGSectionCalcDesc extends AGElementCalcDesc implements IAGCollectionCalcDesc {

    private double mContentHeight;
    private double mContentWidth;

    public double getContentWidth() {
        return mContentWidth;
    }

    public void setContentWidth(double mContentWidth) {
        this.mContentWidth = validateDimension(mContentWidth);
    }

    public double getContentHeight() {
        return mContentHeight;
    }

    public void setContentHeight(double mContentHeight) {
        this.mContentHeight = validateDimension(mContentHeight);
    }

    public double getContentSpcaeHeight(){
        return validateDimension(getHeight());
    }

    public double getContentSpaceWidth(){
        return validateDimension(getWidth());
    }

    public AGSectionCalcDesc createCalcDesc() {
        return new AGSectionCalcDesc();
    }

}
