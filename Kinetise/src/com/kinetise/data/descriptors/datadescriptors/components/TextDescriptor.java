package com.kinetise.data.descriptors.datadescriptors.components;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.TextCalcDesc;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.descriptors.types.AGTextAlignType;
import com.kinetise.data.descriptors.types.AGTextVAlignType;
import com.kinetise.data.descriptors.types.SizeQuad;

public class TextDescriptor {
    private TextCalcDesc mCalcDesc;
    private AbstractAGElementDataDesc mParent;
    protected int mMaxLines;
    protected int mMaxCharacters;
    private AGSizeDesc mFontSize;
    private SizeQuad mPadding;
    private boolean mItalic;
    private boolean mBold;
    private VariableDataDesc mText;
    private AGTextAlignType mTextAlign;
    private int mTextColor;
    private boolean mUnderline;
    private AGTextVAlignType mTextVAlignType;
    private boolean mFontProportional;

    public TextDescriptor(AbstractAGElementDataDesc parent) {
        mCalcDesc = new TextCalcDesc();
        mParent = parent;
        mPadding = new SizeQuad();
        mFontProportional = false;
        mMaxCharacters = -1;
        mMaxLines = -1;
    }

    public AbstractAGElementDataDesc getParent() {
        return mParent;
    }

    public AGSizeDesc getFontSizeDesc() {
        return mFontSize;
    }

    public double getFontSize() {
        double sizeInPixels = getFontSizeDesc().inPixels();
        float fontMultiplier = 1;
        if (isFontProportional())
            fontMultiplier = AGApplicationState.getInstance().getFontSizeMultiplier();
        return sizeInPixels * fontMultiplier;
    }

    public void setFontSizeDesc(AGSizeDesc fontSize) {
        mFontSize = fontSize;
    }

    public boolean isItalic() {
        return mItalic;
    }

    public void setItalic(boolean fontStyle) {
        mItalic = fontStyle;
    }

    public boolean isBold() {
        return mBold;
    }

    public void setBold(boolean fontWeight) {
        mBold = fontWeight;
    }

    public VariableDataDesc getText() {
        return mText;
    }

    public void setText(VariableDataDesc text) {
        mText = text;
    }

    public AGTextAlignType getTextAlign() {
        return mTextAlign;
    }

    public void setTextAlign(AGTextAlignType textAlign) {
        mTextAlign = textAlign;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int color) {
        mTextColor = color;
    }

    public boolean getTextDecoration() {
        return mUnderline;
    }

    public void setTextDecoration(boolean textDecoration) {
        mUnderline = textDecoration;
    }

    public AGTextVAlignType getTextVAlign() {
        return mTextVAlignType;
    }

    public void setTextVAlign(AGTextVAlignType textVAlignType) {
        mTextVAlignType = textVAlignType;
    }

    public int getMaxCharacters() {
        return mMaxCharacters;
    }

    public void setMaxCharacters(int maxCharacters) {
        mMaxCharacters = maxCharacters;
    }

    public int getMaxLines() {
        return mMaxLines;
    }

    public void setMaxLines(int maxLines) {
        mMaxLines = maxLines;
    }

    public void resolveVariable() {
        if (mText != null)
            mText.resolveVariable();
    }

    public boolean isFontProportional() {
        return mFontProportional;
    }

    public void setFontProportional(boolean fontProportional) {
        mFontProportional = fontProportional;
    }

    public TextDescriptor copy(AbstractAGElementDataDesc parent) {
        TextDescriptor copied = new TextDescriptor(parent);
        if (mFontSize != null) {
            copied.mFontSize = new AGSizeDesc(this.mFontSize.getDescValue(), (mFontSize.getDescUnit()));
        }
        copied.mItalic = this.mItalic;
        copied.mBold = this.mBold;

        if (mText != null) {
            copied.mText = this.mText.copy(parent);
        }
        if (mTextVAlignType != null) {
            copied.mTextVAlignType = mTextVAlignType;
        }
        if (mTextAlign != null) {
            copied.mTextAlign = mTextAlign;
        }
        copied.getPadding().copyFrom(getPadding());
        copied.mTextColor = this.mTextColor;
        copied.mUnderline = this.mUnderline;
        copied.mMaxCharacters = this.mMaxCharacters;
        copied.mMaxLines = this.mMaxLines;
        copied.mFontProportional = mFontProportional;
        return copied;
    }

    public TextCalcDesc getCalcDescriptor() {
        return mCalcDesc;
    }

    public SizeQuad getPadding() {
        return mPadding;
    }
}
