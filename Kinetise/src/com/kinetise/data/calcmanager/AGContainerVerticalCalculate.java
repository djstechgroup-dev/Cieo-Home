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

public class AGContainerVerticalCalculate extends AbstractAGContainerCalculate {
    private static AGContainerVerticalCalculate mInstance = null;

    public static AGContainerVerticalCalculate getInstance() {
        if (mInstance == null) {
            mInstance = new AGContainerVerticalCalculate();
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    @Override
    public void layout(AbstractAGElementDataDesc dataDesc) {
        AlignData alignData = this.getAlignAndVAlignDataForLayout(dataDesc);

        double controlPositionX = 0;
        double controlPositionY = 0;
        AbstractAGContainerDataDesc containerDesc = (AbstractAGContainerDataDesc) dataDesc;
        AGContainerCalcDesc calcDesc = containerDesc.getCalcDesc();
        List<AbstractAGElementDataDesc> children = containerDesc.getPresentControls();
        AbstractAGViewDataDesc child;
        AGViewCalcDesc childCalcDesc;
        double contentWidth = calcDesc.getContentWidth();
        double contentHeight = calcDesc.getContentHeight();
        double horizontalSpaceForInnerBorder = getHorizontalSpaceForInnerBorder(containerDesc),
                verticalSpaceForInnerBorder = getVerticalSpaceForInnerBorder(containerDesc);

        //VERTICAL
        double posForTopVAlign = 0;
        double posForBottomVAlign = contentHeight - alignData.totalHeightForBottomVAlign;
        double posForCenterVAlign = (contentHeight - alignData.totalHeightForCenterVAlign) * 0.5;
        double posForDistributedVAlign = 0;

        double freeSpaceForDistributedVAlign;
        if (alignData.elementsNoHeightVAlignDistributed == 1) {
            freeSpaceForDistributedVAlign = (contentHeight - alignData.totalHeightForDistributedVAlign) * 0.5;
        } else {
            freeSpaceForDistributedVAlign = ((contentHeight - alignData.totalHeightForDistributedVAlign) / (alignData.elementsNoHeightVAlignDistributed - 1));
        }

        // repositiong of center group in case it overlaps height top or
        // bottom group
        if (posForCenterVAlign + alignData.totalHeightForCenterVAlign > posForBottomVAlign) {
            posForCenterVAlign = posForBottomVAlign - alignData.totalHeightForCenterVAlign;
        }
        if (posForCenterVAlign < posForTopVAlign + alignData.totalHeightForTopVAlign) {
            posForCenterVAlign = posForTopVAlign + alignData.totalHeightForTopVAlign;
        }

        for (int i = 0; i < children.size(); i++) {
            if (containerDesc.isInverted())
                child = (AbstractAGViewDataDesc)children.get(children.size()-1-i);
            else
                child = (AbstractAGViewDataDesc)children.get(i);

            childCalcDesc = child.getCalcDesc();

            if (isLastChild(i,children.size())) {
                horizontalSpaceForInnerBorder = 0;
                verticalSpaceForInnerBorder = 0;
            }

            if (child.getAlign() != null) { //for unit tests purposes only!!!!!
                if (child.getAlign().equals(AGAlignType.LEFT)) {
                    controlPositionX = 0;
                } else if (child.getAlign().equals(AGAlignType.RIGHT)) {
                    controlPositionX = contentWidth - childCalcDesc.getBlockWidth() - horizontalSpaceForInnerBorder;
                } else if (child.getAlign().equals(AGAlignType.CENTER)) {
                    controlPositionX = (contentWidth - childCalcDesc.getBlockWidth() - horizontalSpaceForInnerBorder) * 0.5;
                }
            }

            if (child.getVAlign() != null) {  //for unit tests purposes only!!!!!
                if (child.getVAlign().equals(AGVAlignType.TOP)) {
                    controlPositionY = (posForTopVAlign);
                    posForTopVAlign += childCalcDesc.getBlockHeight() + verticalSpaceForInnerBorder;
                } else if (child.getVAlign().equals(AGVAlignType.CENTER)) {
                    controlPositionY = (posForCenterVAlign);
                    posForCenterVAlign += childCalcDesc.getBlockHeight() + verticalSpaceForInnerBorder;
                } else if (child.getVAlign().equals(AGVAlignType.BOTTOM)) {
                    controlPositionY = (posForBottomVAlign);
                    posForBottomVAlign += childCalcDesc.getBlockHeight() + verticalSpaceForInnerBorder;
                } else if (child.getVAlign().equals(AGVAlignType.DISTRIBUTED)) {
                    if (alignData.elementsNoHeightVAlignDistributed == 1) {
                        controlPositionY = posForDistributedVAlign + freeSpaceForDistributedVAlign;
                    } else {
                        controlPositionY = posForDistributedVAlign;
                        posForDistributedVAlign += childCalcDesc.getBlockHeight() + freeSpaceForDistributedVAlign;
                    }
                }
            }
            childCalcDesc.setPositionX(controlPositionX);
            childCalcDesc.setPositionY(controlPositionY);
        }

        //Calls .layout method on children
        super.layout(dataDesc);
    }

    @Override
    public void measureBlockHeight(AbstractAGElementDataDesc desc, double maxFreeSpace, double maxSpaceForMax) {
        super.measureBlockHeight(desc, maxFreeSpace, maxSpaceForMax);
        AbstractAGContainerDataDesc dataDesc = (AbstractAGContainerDataDesc) desc;
        measureBorderMagins(dataDesc,dataDesc.getCalcDesc().getContentSpaceWidth());
    }

    @Override
    public void measureWidthForMin(AbstractAGElementDataDesc dataDesc, double maxWidth, double maxSpaceForMax) {
        double result = 0;
        AGViewCalcDesc childCalcDesc;
        AbstractAGContainerDataDesc containerDataDesc = (AbstractAGContainerDataDesc) dataDesc;
        List<AbstractAGElementDataDesc> children = containerDataDesc.getPresentControls();

        measureWidthForChildren(containerDataDesc, maxWidth, maxSpaceForMax);

        for (int i = 0; i < children.size(); i++) {
            childCalcDesc = ((AbstractAGViewDataDesc)children.get(i)).getCalcDesc();
            result = Math.max(childCalcDesc.getBlockWidth(),result);
        }

        containerDataDesc.getCalcDesc().setContentWidth(result);
        super.measureWidthForMin(dataDesc, maxWidth, maxSpaceForMax);
    }

    @Override
    public void measureBlockWidth(AbstractAGElementDataDesc desc, double maxFreeSpace, double maxSpaceForMax) {
        super.measureBlockWidth(desc, maxFreeSpace, maxSpaceForMax);
    }

    @Override
    public double measureWidthForChildren(AbstractAGContainerDataDesc dataDesc, double maxFreeSpace, double maxSpaceForMax) {

        if (dataDesc.isScrollHorizontal()) {
            maxFreeSpace = Integer.MAX_VALUE - 1000;
        }

        int objNo;
        double maxChild = 0;

        for (objNo = 0; objNo < dataDesc.getPresentControls().size(); objNo++) {
            CalcManager.getInstance().measureBlockWidth(dataDesc.getPresentControls().get(objNo), maxFreeSpace, maxSpaceForMax);
            maxChild = Math.max(((AbstractAGViewDataDesc)dataDesc.getPresentControls().get(objNo)).getCalcDesc().getBlockWidth(), maxChild);
        }

        return maxChild;
    }


    @Override
    public void measureHeightForMin(AbstractAGElementDataDesc dataDesc, double maxHeight, double maxSpaceForMax) {
        double result = 0;
        int i;
        AGViewCalcDesc childCalcDesc;
        List<AbstractAGElementDataDesc> children = ((AbstractAGContainerDataDesc) dataDesc).getPresentControls();

        double verticalSpaceForInnerBorder = getVerticalSpaceForInnerBorder((AbstractAGContainerDataDesc) dataDesc);

        this.measureHeightForChildren((AbstractAGContainerDataDesc)dataDesc, maxHeight, maxSpaceForMax);

        for (i = 0; i < children.size(); i++) {

            if (isLastChild(i,children.size())) {
                verticalSpaceForInnerBorder = 0;
            }

            childCalcDesc = ((AbstractAGViewDataDesc)children.get(i)).getCalcDesc();
            result += childCalcDesc.getBlockHeight() + verticalSpaceForInnerBorder;
        }

        dataDesc.getCalcDesc().setHeight(result);

        //adds paddings and inner border
        super.measureHeightForMin(dataDesc, maxHeight, maxSpaceForMax);
    }

    @Override
    public double getHorizontalSpaceForInnerBorder(AbstractAGContainerDataDesc dataDesc) {
        return 0;
    }

    @Override
    public double getVerticalSpaceForInnerBorder(AbstractAGContainerDataDesc dataDesc) {
        double spaceForInnerBorder = 0;
        if (dataDesc.getPresentControls().size() > 1) {
            spaceForInnerBorder = dataDesc.getCalcDesc().getItemSeparation();
        }
        return spaceForInnerBorder;
    }

    @Override
    public double measureHeightForChildren(AbstractAGContainerDataDesc dataDesc, double maxFreeSpace, double maxSpaceForMax) {
        if (dataDesc.isScrollVertical()) {
            maxFreeSpace = Integer.MAX_VALUE - 1000;
        }
        int objNo;
        List<AbstractAGElementDataDesc> children = dataDesc.getPresentControls();
        double tmpHeight;
        double contentHeight = 0;
        double innerBorderHeight = dataDesc.getCalcDesc().getItemSeparation();


        for (objNo = 0; objNo < children.size(); objNo++) {
            if (((AbstractAGViewDataDesc)children.get(objNo)).getHeight().getDescUnit().equals(AGUnitType.KPX) || ((AbstractAGViewDataDesc)children.get(objNo)).getHeight().getDescUnit().equals(AGUnitType.PERCENT)) {
                CalcManager.getInstance().measureBlockHeight(children.get(objNo), maxFreeSpace, maxSpaceForMax);
                tmpHeight = ((AbstractAGViewDataDesc)children.get(objNo)).getCalcDesc().getBlockHeight();

                tmpHeight += innerBorderHeight;
                contentHeight += tmpHeight;
                maxFreeSpace -= tmpHeight;

                if (maxFreeSpace < maxSpaceForMax) {
                    maxSpaceForMax = maxFreeSpace;
                }
            }
        }

        for (objNo = 0; objNo < children.size(); objNo++) {
            if (((AbstractAGViewDataDesc)children.get(objNo)).getHeight().getDescUnit().equals(AGUnitType.MIN)) {
                CalcManager.getInstance().measureBlockHeight(children.get(objNo), maxFreeSpace, maxSpaceForMax);
                tmpHeight = ((AbstractAGViewDataDesc)children.get(objNo)).getCalcDesc().getBlockHeight();

                tmpHeight += innerBorderHeight;
                contentHeight += tmpHeight;
                maxFreeSpace -= tmpHeight;

                if (maxFreeSpace < maxSpaceForMax) {
                    maxSpaceForMax = maxFreeSpace;
                }
            }
        }

        for (objNo = 0; objNo < children.size(); objNo++) {
            if (((AbstractAGViewDataDesc)children.get(objNo)).getHeight().getDescUnit().equals(AGUnitType.MAX)) {
                CalcManager.getInstance().measureBlockHeight(children.get(objNo), maxFreeSpace, maxSpaceForMax);
                tmpHeight = ((AbstractAGViewDataDesc)children.get(objNo)).getCalcDesc().getBlockHeight();

                tmpHeight += innerBorderHeight;
                contentHeight += tmpHeight;
                maxFreeSpace -= tmpHeight;

                if (maxFreeSpace < maxSpaceForMax) {
                    maxSpaceForMax = maxFreeSpace;
                }
            }
        }
        contentHeight -= innerBorderHeight;
        return contentHeight;
    }
}
