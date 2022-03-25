package com.kinetise.data.descriptors.calcdescriptors;


import com.kinetise.data.descriptors.types.Quad;

public class AGViewCalcDesc extends AGElementCalcDesc {
    private double mPaddingTop;
    private double mPaddingRight;
    private double mPaddingLeft;
    private double mPaddingBottom;
    private double mMarginTop;
    private double mMarginRight;
    private double mMarginLeft;
    private double mMarginBottom;
    private double mRadiusBottomLeft;
    private double mRadiusBottomRight;
    private double mRadiusTopRight;
    private double mRadiusTopLeft;
    private Quad border= new Quad();

    public Quad getBorder() {
        return border;
    }

    public double getMarginBottom() {
        return mMarginBottom;
    }

    public void setMarginBottom(double margin) {
        mMarginBottom = margin;
    }

    public double getMarginLeft() {
        return mMarginLeft;
    }

    public void setMarginLeft(double margin) {
        mMarginLeft = margin;
    }

    public double getMarginRight() {
        return mMarginRight;
    }

    public void setMarginRight(double margin) {
        mMarginRight = margin;
    }

    public double getMarginTop() {
        return mMarginTop;
    }

    public void setMarginTop(double margin) {
        mMarginTop = margin;
    }

    public double getPaddingBottom() {
        return mPaddingBottom;
    }

    public void setPaddingBottom(double padding) {
        mPaddingBottom = padding;
    }

    public double getPaddingLeft() {
        return mPaddingLeft;
    }

    public void setPaddingLeft(double padding) {
        mPaddingLeft = padding;
    }

    public double getPaddingRight() {
        return mPaddingRight;
    }

    public void setPaddingRight(double padding) {
        mPaddingRight = padding;
    }

    public double getPaddingTop() {
        return mPaddingTop;
    }

    public void setPaddingTop(double padding) {
        mPaddingTop = padding;
    }

    public int getRadiusBottomLeft() {
        return (int) Math.round(mRadiusBottomLeft);
    }

    public void setRadiusBottomLeft(double radius) {
        mRadiusBottomLeft = radius;
    }

    public int getRadiusBottomRight() {
        return (int) Math.round(mRadiusBottomRight);
    }

    public void setRadiusBottomRight(double radius) {
        mRadiusBottomRight = radius;
    }

    public int getRadiusTopLeft() {
        return (int) Math.round(mRadiusTopLeft);
    }

    public void setRadiusTopLeft(double radius) {
        mRadiusTopLeft = radius;
    }

    public int getRadiusTopRight() {
        return (int) Math.round(mRadiusTopRight);
    }

    public void setRadiusTopRight(double radius) {
        mRadiusTopRight = radius;
    }

    public double getContentWidth(){
        double result = getWidth() - (mPaddingLeft + mPaddingRight);
        return validateDimension(result);
    }

    public double getContentHeight(){
        double result = getHeight() - (mPaddingTop + mPaddingBottom);
        return validateDimension(result);
    }

    /**
     * @return wielkosc contentu = height - paddingTop - paddingBottom
     */
    public double getContentSpaceHeight() {
        double result = getHeight() - mPaddingTop - mPaddingBottom;
        return validateDimension(result);
    }

    public double getContentSpaceWidth() {
        double result = getWidth() - mPaddingLeft - mPaddingRight;
        return validateDimension(result);
    }

    public double getBlockHeight() {
        double result = getHeight() + mMarginTop + mMarginBottom + border.getVerticalBorderHeight();
        return validateDimension(result);
    }

    public double getBlockWidth() {
        double result = getWidth() + mMarginLeft + mMarginRight + border.getHorizontalBorderWidth();
        return validateDimension(result);
    }

    public int getViewWidth() {
        return (int) Math.round(getWidth() + border.getHorizontalBorderWidth());
    }

    public int getViewHeight() {
        return (int) Math.round(getHeight() + border.getVerticalBorderHeight());
    }

    public double getContentVerticalOffset(){
        return mPaddingTop + border.getTop();
    }

    public double getContetHorizontelOffset(){
        return mPaddingLeft + border.getLeft();
    }

    /**
     * @return true if this calc desc has any non-zero radius
     */
    public boolean hasRadiuses() {
        return (mRadiusBottomLeft != 0 || mRadiusBottomRight != 0 || mRadiusTopLeft != 0 || mRadiusTopRight != 0);
    }

    public AGViewCalcDesc createCalcDesc() {
        return new AGViewCalcDesc();
    }

    @Override
    public String toString() {
        return String.format("AGViewCalcDesc: [hash: %d, border=[%s, %s, %s, %s], margins=[%s, %s, %s, %s], " +
                "paddings=[%s, %s, %s, %s], radiuses=[%s, %s, %s, %s] ]; %s",
                hashCode(), String.valueOf(border.getLeft()),String.valueOf(border.getTop()),String.valueOf(border.getRight()),String.valueOf(border.getBottom()), String.valueOf(mMarginLeft), String.valueOf(mMarginTop), String.valueOf(mMarginRight), String.valueOf(mMarginBottom),
                String.valueOf(mPaddingLeft), String.valueOf(mPaddingTop), String.valueOf(mPaddingRight), String.valueOf(mPaddingBottom),
                String.valueOf(mRadiusTopLeft), String.valueOf(mRadiusTopRight), String.valueOf(mRadiusBottomLeft), String.valueOf(mRadiusBottomRight), super.toString());
    }
}
