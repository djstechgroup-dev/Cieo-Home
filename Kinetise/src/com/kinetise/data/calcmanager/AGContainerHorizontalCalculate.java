package com.kinetise.data.calcmanager;

import com.kinetise.data.calcmanager.struct.AlignData;
import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGContainerCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.types.AGAlignType;
import com.kinetise.data.descriptors.types.AGUnitType;
import com.kinetise.data.descriptors.types.AGVAlignType;

import java.util.List;

public class AGContainerHorizontalCalculate extends
        AbstractAGContainerCalculate {

    private static AGContainerHorizontalCalculate mInstance;

    public static AGContainerHorizontalCalculate getInstance() {
        if (mInstance == null) {
            mInstance = new AGContainerHorizontalCalculate();
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    public void layout(AbstractAGElementDataDesc dataDesc) {
        AbstractAGContainerDataDesc containerDataDesc = (AbstractAGContainerDataDesc) dataDesc;
        AGContainerCalcDesc calcDesc = containerDataDesc.getCalcDesc();
        AlignData alignData = getAlignAndVAlignDataForLayout(dataDesc);

        double contentWidth = calcDesc.getContentWidth();
        double contentHeight = calcDesc.getContentHeight();

        double horizontalInnerBorder = getHorizontalSpaceForInnerBorder(containerDataDesc);
        double verticalInnerBorder = getVerticalSpaceForInnerBorder(containerDataDesc);

        double posForRightAlign = contentWidth - alignData.totalWidthForRightAlign;
        double posForCenterAlign = (contentWidth - alignData.totalWidthForCenterAlign) * 0.5;
        double posForDistributedAlign = 0;

        // two types of align distributed so we have 2 algorithms to
        // calculate location.
        double freeSpaceForDistributedAlign;
        if (alignData.elementsNoWidthAlignDistributed == 1) {
            freeSpaceForDistributedAlign = (contentWidth - alignData.totalWidthForDistributedAlign) * 0.5;
        } else {
            freeSpaceForDistributedAlign = (contentWidth - alignData.totalWidthForDistributedAlign)
                    / (alignData.elementsNoWidthAlignDistributed - 1);
        }

        // repositiong of center group in case it overlaps with left or
        // right group
        if (posForCenterAlign + alignData.totalWidthForCenterAlign > posForRightAlign) {
            posForCenterAlign = posForRightAlign
                    - alignData.totalWidthForCenterAlign;
        }
        if (posForCenterAlign < alignData.totalWidthForLeftAlign) {
            posForCenterAlign = alignData.totalWidthForLeftAlign;
        }

        double controlPositionY = 0;
        double controlPositionX = 0;
        double posForLeftAlign = 0;
        List<AbstractAGElementDataDesc> children = containerDataDesc.getPresentControls();
        AbstractAGViewDataDesc child;
        AGViewCalcDesc childCalcDesc;
        for (int i = 0; i < children.size(); i++) {
            if(containerDataDesc.isInverted())
                child = (AbstractAGViewDataDesc) children.get(children.size()-1-i);
            else
                child = (AbstractAGViewDataDesc)children.get(i);
            childCalcDesc = child.getCalcDesc();

            // Align
            if(isLastChild(i,children.size())){
                horizontalInnerBorder = 0;
                verticalInnerBorder = 0;
            }

            if (child.getAlign() != null) { //for unit tests purposes only!!!!!
                if (child.getAlign().equals(AGAlignType.LEFT)) {
                    controlPositionX = posForLeftAlign;
                    posForLeftAlign += childCalcDesc.getBlockWidth() + horizontalInnerBorder;
                } else if (child.getAlign().equals(AGAlignType.RIGHT)) {
                    controlPositionX = posForRightAlign;
                    posForRightAlign += childCalcDesc.getBlockWidth() + horizontalInnerBorder;
                } else if (child.getAlign().equals(AGAlignType.CENTER)) {
                    controlPositionX = posForCenterAlign;
                    posForCenterAlign += childCalcDesc.getBlockWidth() + horizontalInnerBorder;
                } else if (child.getAlign().equals(AGAlignType.DISTRIBUTED)) {
                    if (alignData.elementsNoWidthAlignDistributed == 1) {
                        controlPositionX = posForDistributedAlign + freeSpaceForDistributedAlign;
                    } else {
                        controlPositionX = posForDistributedAlign;
                        posForDistributedAlign += (childCalcDesc.getBlockWidth() + freeSpaceForDistributedAlign);
                    }
                }
            }
            childCalcDesc.setPositionX(controlPositionX);

            if (child.getVAlign() != null) { //for unit tests purposes only!!!!!
                if (child.getVAlign().equals(AGVAlignType.TOP)) {
                    controlPositionY = 0;
                } else if (child.getVAlign().equals(AGVAlignType.CENTER) || child.getVAlign().equals(AGVAlignType.DISTRIBUTED)) {
                    controlPositionY = (contentHeight - childCalcDesc.getBlockHeight() - verticalInnerBorder) * 0.5;
                } else if (child.getVAlign().equals(AGVAlignType.BOTTOM)) {
                    controlPositionY = contentHeight - childCalcDesc.getBlockHeight() - verticalInnerBorder;
                }
            }
            childCalcDesc.setPositionY(controlPositionY);
        }

        super.layout(dataDesc);
    }

    @Override
    public void measureWidthForMin(AbstractAGElementDataDesc desc,
                                   double maxWidth, double maxSpaceForMax) {
        double result = 0;
        AGViewCalcDesc childCalcDesc;
        AbstractAGContainerDataDesc containerDataDesc = (AbstractAGContainerDataDesc) desc;
        List<AbstractAGElementDataDesc> children = containerDataDesc.getPresentControls();

        double horizontalSpaceForInnerBorder = getHorizontalSpaceForInnerBorder(containerDataDesc);

        measureWidthForChildren(containerDataDesc, maxWidth, maxSpaceForMax);

        for (int i = 0; i < children.size(); i++) {
            childCalcDesc = ((AbstractAGViewDataDesc)children.get(i)).getCalcDesc();
            result += childCalcDesc.getBlockWidth();
        }

        if(children.size()>1)
            result += horizontalSpaceForInnerBorder * (children.size()-1);

        containerDataDesc.getCalcDesc().setContentWidth(result);
        super.measureWidthForMin(desc, maxWidth, maxSpaceForMax);
    }

    @Override
    protected double measureWidthForChildren(AbstractAGContainerDataDesc dataDesc,
                                             double maxFreeSpace, double maxSpaceForMax) {

       // Log.d("TEST", "measureWidthForChildren start this hash:" + this.hashCode());
        if (dataDesc.isScrollHorizontal()) {
            maxFreeSpace = Integer.MAX_VALUE - 1000;
        }
        double childrenWidth = 0;
        double innerBorderWidth = dataDesc.getCalcDesc().getItemSeparation();

        List<AbstractAGElementDataDesc> children = dataDesc.getPresentControls();
        AbstractAGViewDataDesc child;


        for (int i = 0; i < children.size(); i++) {
            child = (AbstractAGViewDataDesc)children.get(i);
            if (child.getWidth().getDescUnit().equals(AGUnitType.KPX) ||
                    child.getWidth().getDescUnit().equals(AGUnitType.PERCENT)) {

                CalcManager.getInstance().measureBlockWidth(child,
                        maxFreeSpace, maxSpaceForMax);
                double childWidth = child.getCalcDesc()
                        .getBlockWidth();
                childWidth += innerBorderWidth;
                maxFreeSpace -= childWidth;
                childrenWidth += childWidth;
                if (maxFreeSpace < maxSpaceForMax) {
                    maxSpaceForMax = maxFreeSpace;
                }

            }
        }

        for (int i = 0; i < children.size(); i++) {
            child = (AbstractAGViewDataDesc)children.get(i);
            if (child.getWidth().getDescUnit().equals(AGUnitType.MIN)) {
                CalcManager.getInstance().measureBlockWidth(child,
                        maxFreeSpace, maxSpaceForMax);
                double childWidth = child.getCalcDesc()
                        .getBlockWidth();
                childWidth += innerBorderWidth;
                maxFreeSpace -= childWidth;
                childrenWidth += childWidth;


                if (maxFreeSpace < maxSpaceForMax) {
                    maxSpaceForMax = maxFreeSpace;
                }
            }
        }

        for (int i = 0; i < children.size(); i++) {
            child = (AbstractAGViewDataDesc)children.get(i);
            if (child.getWidth().getDescUnit().equals(AGUnitType.MAX)) {
                CalcManager.getInstance().measureBlockWidth(child,
                        maxFreeSpace, maxSpaceForMax);
                double childWidth = child.getCalcDesc()
                        .getBlockWidth();
                childWidth += innerBorderWidth;
                maxFreeSpace -= childWidth;
                childrenWidth += childWidth;


                if (maxFreeSpace < maxSpaceForMax) {
                    maxSpaceForMax = maxFreeSpace;
                }

            }
        }

        childrenWidth -= innerBorderWidth;
      //  Log.d("TEST", "measureWidthForChildren  end this hash:" + this.hashCode());
        return childrenWidth;
    }

    @Override
    public void measureHeightForMin(AbstractAGElementDataDesc dataDesc, double maxHeight, double maxSpaceForMax) {
        double result = 0;
        int i;
        AGViewCalcDesc childCalcDesc;
        List<AbstractAGElementDataDesc> children = ((AbstractAGContainerDataDesc) dataDesc).getPresentControls();

        measureHeightForChildren((AbstractAGContainerDataDesc)dataDesc, maxHeight, maxSpaceForMax);

        for (i = 0; i < children.size(); i++) {
            childCalcDesc = ((AbstractAGViewDataDesc)children.get(i)).getCalcDesc();

            if (result < childCalcDesc.getBlockHeight()) {
                result = childCalcDesc.getBlockHeight();
            }
        }

        dataDesc.getCalcDesc().setHeight(result);

        // adds paddings and inner border
        super.measureHeightForMin(dataDesc, maxHeight, maxSpaceForMax);
    }

    @Override
    public void measureBlockHeight(AbstractAGElementDataDesc desc, double maxFreeSpace, double maxSpaceForMax) {
        super.measureBlockHeight(desc, maxFreeSpace, maxSpaceForMax);
        AbstractAGContainerDataDesc dataDesc = (AbstractAGContainerDataDesc) desc;
        measureBorderMagins(dataDesc,dataDesc.getCalcDesc().getContentSpaceHeight());
    }

    @Override
    protected double measureHeightForChildren(AbstractAGContainerDataDesc dataDesc,
                                              double maxFreeSpace, double maxSpaceForMax) {

        if (dataDesc.isScrollVertical()) {
            maxFreeSpace = Integer.MAX_VALUE - 1000;
        }

        int objNo;
        List<AbstractAGElementDataDesc> children = dataDesc.getPresentControls();
        double contentHeight = 0;
        double tmpHeight;

        for (objNo = 0; objNo < children.size(); objNo++) {
            CalcManager.getInstance().measureBlockHeight(children.get(objNo),
                    maxFreeSpace, maxSpaceForMax);
            tmpHeight = ((AbstractAGViewDataDesc)children.get(objNo)).getCalcDesc()
                    .getBlockHeight();
            if (tmpHeight > contentHeight) {
                contentHeight = tmpHeight;
            }
        }

        return contentHeight;
    }

    @Override
    protected double getVerticalSpaceForInnerBorder(AbstractAGContainerDataDesc dataDesc) {
        return 0;
    }

    @Override
    protected double getHorizontalSpaceForInnerBorder(AbstractAGContainerDataDesc dataDesc) {
        double spaceForInnerBorder = 0;
        if (dataDesc.getPresentControls().size() > 1) {
            spaceForInnerBorder = dataDesc.getCalcDesc().getItemSeparation();
        }
        return spaceForInnerBorder;
    }
}
