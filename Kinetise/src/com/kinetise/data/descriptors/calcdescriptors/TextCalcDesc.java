package com.kinetise.data.descriptors.calcdescriptors;

import com.kinetise.data.descriptors.types.IntQuad;
import com.kinetise.data.systemdisplay.views.text.LineData;

import java.util.ArrayList;

public class TextCalcDesc {
    private double mFontSize;
    private double mTextWidth;
    private double mTextHeight;
    private double mRowHeight;
    private IntQuad padding;
    
    private ArrayList<LineData> mLinesData;
    private double mTextInterline;


    public TextCalcDesc(){
        padding = new IntQuad();
    }

    public double getFontSize() {
        return mFontSize;
    }

    public void setFontSize(double fontSize) {
        mFontSize = fontSize;
    }

    public TextCalcDesc createCalcDesc() {
        return new TextCalcDesc();
    }

    public double getTextWidth() {
        return mTextWidth;
    }

    public void setTextWidth(double measuredWidth) {
        mTextWidth = measuredWidth;
    }

    public double getTextHeight() {
        return this.mTextHeight;
    }

    public void setTextHeight(double measuredHeight) {
        this.mTextHeight = measuredHeight;
    }

    public double getRowHeight() {
        return mRowHeight;
    }

    public void setRowHeight(double rowHeight) {
        mRowHeight = rowHeight;
    }

    public ArrayList<LineData> getLinesData() {
        return mLinesData;
    }

    public void setLinesData(ArrayList<LineData> linesData) {
        mLinesData = linesData;
    }

    public double getTextInterline() {
        return mTextInterline;
    }

    public void setTextInterline(double texInterLine) {
        mTextInterline = texInterLine;
    }

    public IntQuad getPadding() {
        return padding;
    }
}
