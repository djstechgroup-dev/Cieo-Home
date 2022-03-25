package com.kinetise.data.calcmanager;

import com.kinetise.data.calcmanager.struct.AlignData;
import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGContainerCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.types.AGAlignType;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.descriptors.types.AGUnitType;
import com.kinetise.data.descriptors.types.AGVAlignType;

import java.util.List;

public abstract class AbstractAGContainerCalculate extends AbstractCalculate {

    public void layout(AbstractAGElementDataDesc dataDesc) {
        List<AbstractAGElementDataDesc> children = ((AbstractAGContainerDataDesc) dataDesc).getPresentControls();
        for (int i = 0; i < children.size(); i++) {
            getCalcManager().layout(children.get(i));
        }
    }

    @Override
    public void measureWidthForMin(AbstractAGElementDataDesc dataDesc, double maxFreeSpace, double maxSpaceForMax) {
        AGContainerCalcDesc containerCalcDesc = (AGContainerCalcDesc) dataDesc.getCalcDesc();
        double contentWidth = Math.min(containerCalcDesc.getContentWidth(), maxFreeSpace);
        containerCalcDesc.setWidth(contentWidth + containerCalcDesc.getPaddingLeft() + containerCalcDesc.getPaddingRight());
    }

    public void measureBlockWidth(AbstractAGElementDataDesc desc, double maxFreeSpace, double maxSpaceForMax) {
        AbstractAGContainerDataDesc containerDataDesc = (AbstractAGContainerDataDesc) desc;
        AGContainerCalcDesc calcDesc = containerDataDesc.getCalcDesc();

        measureItemSeparation(containerDataDesc);
        super.measureBlockWidth(desc, maxFreeSpace, maxSpaceForMax);

        if (containerDataDesc.getWidth().getDescUnit().equals(AGUnitType.MIN)) {
            return;
        }

        maxFreeSpace = calcDesc.getWidth() - calcDesc.getPaddingLeft() - calcDesc.getPaddingRight();
        maxSpaceForMax = maxFreeSpace;

        double childrenWidth = measureWidthForChildren(containerDataDesc, maxFreeSpace, maxSpaceForMax);
        double viewportWidth = (calcDesc.getWidth() - calcDesc.getPaddingLeft() - calcDesc.getPaddingRight());

        double contentWidth;

        if (containerDataDesc.isScrollHorizontal()) {
            contentWidth = Math.max(viewportWidth, childrenWidth);
        } else {
            contentWidth = viewportWidth;
        }
        calcDesc.setContentWidth(contentWidth);
        measureInnerBorder(containerDataDesc);
    }

    protected void measureBorderMagins(AbstractAGContainerDataDesc dataDesc,double contentSpace) {
        AGContainerCalcDesc calcDesc = dataDesc.getCalcDesc();
        calcDesc.setItemBorderMarginStart(measureBorderMargin(dataDesc.getItemBorderMarginStart(), contentSpace));
        calcDesc.setItemBorderMarginEnd(measureBorderMargin(dataDesc.getItemBorderMarginEnd(), contentSpace));
    }

    protected void measureItemSeparation(AbstractAGContainerDataDesc containerDataDesc){
        AGContainerCalcDesc calcDesc = containerDataDesc.getCalcDesc();
        calcDesc.setItemSeparation(containerDataDesc.getItemSeparation().inPixels());
    }

    public void measureBlockHeight(AbstractAGElementDataDesc desc, double maxFreeSpace, double maxSpaceForMax) {

        super.measureBlockHeight(desc, maxFreeSpace, maxSpaceForMax);

        AbstractAGContainerDataDesc dataDesc = (AbstractAGContainerDataDesc) desc;
        AGContainerCalcDesc calcDesc = dataDesc.getCalcDesc();

        if (dataDesc.getHeight().getDescUnit().equals(AGUnitType.MIN))
            return;

        maxFreeSpace = calcDesc.getHeight() - calcDesc.getPaddingTop() - calcDesc.getPaddingBottom();
        maxSpaceForMax = maxFreeSpace;

        double childrenHeight = measureHeightForChildren(dataDesc, maxFreeSpace, maxSpaceForMax);
        double viewportHeight = calcDesc.getHeight() - calcDesc.getPaddingTop() - calcDesc.getPaddingBottom();

        double contentHeight;
        if (dataDesc.isScrollVertical()) {
            contentHeight = Math.max(viewportHeight, childrenHeight);
        } else {
            contentHeight = viewportHeight;
        }
        calcDesc.setContentHeight(contentHeight);

    }

    public void measureHeightForMin(AbstractAGElementDataDesc dataDesc, double maxFreeSpace, double maxSpaceForMax) {
        AGContainerCalcDesc calcDesc = (AGContainerCalcDesc) dataDesc.getCalcDesc();
        double measuredHeight = calcDesc.getHeight();

        calcDesc.setContentHeight(measuredHeight);
        measuredHeight = Math.min(measuredHeight, maxFreeSpace);

        calcDesc.setHeight(measuredHeight + calcDesc.getPaddingTop() + calcDesc.getPaddingBottom());
    }

    private void measureInnerBorder(AbstractAGContainerDataDesc dataDesc) {
        AGContainerCalcDesc calcDesc = dataDesc.getCalcDesc();
        calcDesc.setItemBorder(dataDesc.getItemBorder().inPixels());
    }

    protected double measureBorderMargin(AGSizeDesc itemBorderMargin, double contentSpace) {
        if (itemBorderMargin.getDescUnit().equals(AGUnitType.KPX))
            return itemBorderMargin.inPixels();
        else
            return measurePercent(itemBorderMargin.getDescValue(),contentSpace);
    }

    public AlignData getAlignAndVAlignDataForLayout(AbstractAGElementDataDesc dataDesc) {
        AlignData alignData = new AlignData();
        AbstractAGContainerDataDesc containerDataDesc = (AbstractAGContainerDataDesc) dataDesc;

        double horizontalSpaceForInnerBorder = getHorizontalSpaceForInnerBorder(containerDataDesc);
        double verticalSpaceForInnerBorder = getVerticalSpaceForInnerBorder(containerDataDesc);
        AGViewCalcDesc childCalcDesc;
        AbstractAGViewDataDesc child;
        List<AbstractAGElementDataDesc> children = containerDataDesc.getPresentControls();

        for (int i = 0; i < children.size(); i++) {
            child = (AbstractAGViewDataDesc)children.get(i);
            childCalcDesc = child.getCalcDesc();

            if (isLastChild(i, children.size())) {
                horizontalSpaceForInnerBorder = 0;
                verticalSpaceForInnerBorder = 0;
            }

            if (child.getAlign() != null) {
                if (child.getAlign().equals(AGAlignType.LEFT)) {
                    alignData.totalWidthForLeftAlign += childCalcDesc.getBlockWidth() + horizontalSpaceForInnerBorder;
                } else if (child.getAlign().equals(AGAlignType.RIGHT)) {
                    alignData.totalWidthForRightAlign += childCalcDesc.getBlockWidth() + horizontalSpaceForInnerBorder;
                } else if (child.getAlign().equals(AGAlignType.CENTER)) {
                    alignData.totalWidthForCenterAlign += childCalcDesc.getBlockWidth() + horizontalSpaceForInnerBorder;
                } else if (child.getAlign().equals(AGAlignType.DISTRIBUTED)) {
                    alignData.totalWidthForDistributedAlign += childCalcDesc.getBlockWidth();
                    alignData.elementsNoWidthAlignDistributed += 1;
                }
            }

            if (child.getVAlign() != null) {
                if (child.getVAlign().equals(AGVAlignType.TOP)) {
                    alignData.totalHeightForTopVAlign += childCalcDesc
                            .getBlockHeight() + verticalSpaceForInnerBorder;
                } else if (child.getVAlign().equals(AGVAlignType.CENTER)) {
                    alignData.totalHeightForCenterVAlign += childCalcDesc
                            .getBlockHeight() + verticalSpaceForInnerBorder;
                } else if (child.getVAlign().equals(AGVAlignType.BOTTOM)) {
                    alignData.totalHeightForBottomVAlign += childCalcDesc
                            .getBlockHeight() + verticalSpaceForInnerBorder;
                } else if (child.getVAlign().equals(AGVAlignType.DISTRIBUTED)) {
                    alignData.totalHeightForDistributedVAlign += childCalcDesc
                            .getBlockHeight();
                    alignData.elementsNoHeightVAlignDistributed += 1;
                }
            }
        }

        return alignData;
    }

    protected boolean isLastChild(int i, int childrenCount) {
        return i == childrenCount - 1;
    }

    /**
     * @return total width of all children blocks
     * (where block = width + marginLeft + marginRight + 2*border)
     */
    protected abstract double measureWidthForChildren(AbstractAGContainerDataDesc dataDesc, double maxFreeSpace, double maxSpaceForMax);

    /**
     * @return total height of all children blocks
     * (where block = height + marginTop + marginBottom + 2*border)
     */
    protected abstract double measureHeightForChildren(AbstractAGContainerDataDesc dataDesc, double maxFreeSpace, double maxSpaceForMax);

    /**
     * @return space occupied by single innerborder in vertical container
     * @param dataDesc
     */
    protected abstract double getHorizontalSpaceForInnerBorder(AbstractAGContainerDataDesc dataDesc);

    /**
     * @return space occupied by single innerborder in vertical container
     * @param dataDesc
     */
    protected abstract double getVerticalSpaceForInnerBorder(AbstractAGContainerDataDesc dataDesc);

}
