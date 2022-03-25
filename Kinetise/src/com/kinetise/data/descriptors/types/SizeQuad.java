package com.kinetise.data.descriptors.types;

public class SizeQuad {
    public SizeQuad() {
        mTop = AGSizeDesc.ZEROKPX;
        mBottom = AGSizeDesc.ZEROKPX;
        mLeft = AGSizeDesc.ZEROKPX;
        mRight = AGSizeDesc.ZEROKPX;
    }

    private AGSizeDesc mTop;
    private AGSizeDesc mBottom;
    private AGSizeDesc mLeft;
    private AGSizeDesc mRight;

    public AGSizeDesc getTop() {
        return mTop;
    }

    public void setTop(AGSizeDesc top) {
        mTop = top;
    }

    public AGSizeDesc getLeft() {
        return mLeft;
    }

    public void setLeft(AGSizeDesc left) {
        mLeft = left;
    }

    public AGSizeDesc getBottom() {
        return mBottom;
    }

    public void setBottom(AGSizeDesc bottom) {
        mBottom = bottom;
    }

    public AGSizeDesc getRight() {
        return mRight;
    }

    public void setRight(AGSizeDesc right) {
        mRight = right;
    }

    public void setAll(AGSizeDesc size) {
        mTop = size;
        mBottom = size;
        mRight = size;
        mLeft = size;
    }


    public void copyFrom(SizeQuad border) {
        mTop = border.mTop.copy();
        mBottom = border.mBottom.copy();
        mLeft = border.mLeft.copy();
        mRight = border.mRight.copy();
    }
}
