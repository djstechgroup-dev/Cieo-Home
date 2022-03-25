package com.kinetise.data.descriptors.calcdescriptors;

import android.view.View;

import java.io.Serializable;

public class AGElementCalcDesc implements Serializable{

    private double mWidth;
    private double mHeight;
    private double mPositionX;
    private double mPositionY;

    public double getHeight() {
        return mHeight;
    }

    public double getPositionX() {
        return mPositionX;
    }

    public double getPositionY() {
        return mPositionY;
    }

    public double getWidth() {
        return mWidth;
    }

    public void setHeight(double height) {
        mHeight = height;
    }

    public void setPositionX(double x) {
        mPositionX = x;
    }

    public void setPositionY(double y) {
        mPositionY = y;
    }

    public void setWidth(double width) {
        mWidth = width;
    }

    public int getViewWidth() {
        return (int) Math.round(mWidth);
    }

    public int getViewHeight() {
        return (int) Math.round(mHeight);
    }

    public AGElementCalcDesc createCalcDesc() {
        return new AGElementCalcDesc();
    }

    public double validateDimension(double pDimension){
        return pDimension < 0 ? 0 : pDimension;
    }

    public int getWidthAsMeasureSpec(){
        return View.MeasureSpec.makeMeasureSpec(getViewWidth(), View.MeasureSpec.EXACTLY);
    }

    public int getHeightAsMeasureSpec(){
        return View.MeasureSpec.makeMeasureSpec(getViewHeight(), View.MeasureSpec.EXACTLY);
    }

    @Override
    public String toString() {
        return String.format("AGElementCalcDesc: [hash: %d, positions=[%s, %s], width=%s, height=%s",
                hashCode(), String.valueOf(mPositionX), String.valueOf(mPositionY), String.valueOf(mWidth), String.valueOf(mHeight));
    }
}
