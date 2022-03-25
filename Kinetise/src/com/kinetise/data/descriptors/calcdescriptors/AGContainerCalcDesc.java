package com.kinetise.data.descriptors.calcdescriptors;

public class AGContainerCalcDesc extends AGViewCalcDesc implements IAGCollectionCalcDesc {

    private double mContentHeight;
    private double mContentWidth;
    private double mItemSeparation;
    private double mItemBorder;
    private double mItemBorderMarginStart;
    private double mItemBorderMarginEnd;

    /**
     * @return Sum of all children height(one side another)
     * PL:suma wysokosci dzieci (ustawionych jeden pod drugim)
     */
    public double getContentHeight() {
        return mContentHeight;
    }

    /**
     * @return Sum of all children width(one side another)
     * PL:suma szerokosci dzieci (ustawionych jeden obok drugiego)
     */
    public double getContentWidth() {
        return mContentWidth;
    }
    /**
     * @return Height/Width of the innerBorder
     * */
    public double getItemSeparation() {
        return mItemSeparation;
    }
    /**
     * Sets itemSeparation width/height
     * @param itemSeparation InnerBorder size
     * */
    public void setItemSeparation(double itemSeparation) {
        mItemSeparation = itemSeparation;
    }
    /**
     * Sets content height for container
     * @param height Content Height of container
     * */
    public void setContentHeight(double height) {
        mContentHeight = validateDimension(height);
    }
    /**
     * Sets content width for container
     * @param width Content width for container
     * */
    public void setContentWidth(double width) {
        mContentWidth = validateDimension(width);
    }

    public AGContainerCalcDesc createCalcDesc() {
        return new AGContainerCalcDesc();
    }

    @Override
    public String toString() {
        return String.format("AGContainerCalcDesc: [hash: %d, contentHeight=%s, contentWidth=%s, innerBorder=%s]; %s",
                hashCode(), String.valueOf(mContentHeight), String.valueOf(mContentWidth), String.valueOf(mItemSeparation), super.toString());
    }

    public double getItemBorder() {
        return mItemBorder;
    }

    public void setItemBorder(double itemBorder) {
        mItemBorder = itemBorder;
    }

    public double getItemBorderMarginStart() {
        return mItemBorderMarginStart;
    }

    public void setItemBorderMarginStart(double itemBorderMarginStart) {
        mItemBorderMarginStart = itemBorderMarginStart;
    }

    public double getItemBorderMarginEnd() {
        return mItemBorderMarginEnd;
    }

    public void setItemBorderMarginEnd(double itemBorderMarginEnd) {
        mItemBorderMarginEnd = itemBorderMarginEnd;
    }
}
