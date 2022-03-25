package com.kinetise.data.descriptors.calcdescriptors;

public class AGCompoundButtonCalcDesc extends AGViewCalcDesc {

    private double mCheckedHeight;
    private double mCheckedWidth;
    private double mInnerSpace;

    public double getCheckedHeight() {
        return mCheckedHeight;
    }

    public double getCheckedWidth() {
        return mCheckedWidth;
    }

    public double getInnerSpace() {
        return mInnerSpace;
    }

    public void setCheckedHeight(double checkedHeight) {
        mCheckedHeight = checkedHeight;
    }

    public void setCheckedWidth(double checkedWidth) {
        mCheckedWidth = checkedWidth;
    }

    public void setInnerSpace(double innerSpace) {
        mInnerSpace = innerSpace;
    }

    public AGCompoundButtonCalcDesc createCalcDesc() {
        return new AGCompoundButtonCalcDesc();
    }
}