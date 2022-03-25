package com.kinetise.data.descriptors.types;

public class Quad {
    private double mLeft;
    private double mRight;
    private double mTop;
    private double mBottom;

    public double getHorizontalBorderWidth(){
        return mLeft + mRight;
    }

    public double getVerticalBorderHeight(){
        return mTop + mBottom;
    }

    public void setLeft(double left) {
        mLeft = left;
    }

    public double getLeft(){
        return mLeft;
    }

    public int getLeftAsInt() {
        return (int)Math.round(mLeft);
    }

    public void setRight(double right) {
        mRight = right;
    }

    public double getRight(){
        return mRight;
    }

    public int getRightAsInt() {
        return (int)Math.round(mRight);
    }

    public void setTop(double top) {
        mTop = top;
    }

    public double getTop() {
       return mTop;
    }

    public int getTopAsInt() {
        return (int)Math.round(mTop);
    }

    public void setBottom(double bottom) {
        mBottom = bottom;
    }

    public double getBottom() {
        return mBottom;
    }

    public int getBottomAsInt() {
        return (int)Math.round(mBottom);
    }
}
