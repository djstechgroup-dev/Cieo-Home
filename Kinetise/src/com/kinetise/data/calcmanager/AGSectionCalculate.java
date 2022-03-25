package com.kinetise.data.calcmanager;


import com.kinetise.data.calcmanager.struct.AlignData;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGSectionDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.IAGCollectionDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGSectionCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.IAGCollectionCalcDesc;
import com.kinetise.data.descriptors.types.AGAlignType;
import com.kinetise.data.descriptors.types.AGVAlignType;

import java.util.List;

public class AGSectionCalculate implements ICalculate{

	private static AGSectionCalculate mInstance;

	public static AGSectionCalculate getInstance(){
		if(mInstance == null){
			mInstance = new AGSectionCalculate();
		}
		
		return mInstance;
	}

    @Override
    public void layout(AbstractAGElementDataDesc desc) {
        AlignData alignData = this.getAlignAndVAlignDataForLayout(desc);

        double controlPositionX = 0;
        double controlPositionY = 0;

        IAGCollectionCalcDesc calcDesc = (IAGCollectionCalcDesc) desc.getCalcDesc();
        List<AbstractAGElementDataDesc> children = ((IAGCollectionDataDesc) desc).getPresentControls();

        double contentWidth = calcDesc.getContentWidth();
        double contentHeight = calcDesc.getContentHeight();

        double posForTopVAlign = 0;
        double posForBottomVAlign = contentHeight
                - alignData.totalHeightForBottomVAlign;
        double posForCenterVAlign = (contentHeight - alignData.totalHeightForCenterVAlign) * 0.5;
        double posForDistributedVAlign = 0;

        double freeSpaceForDistributedVAlign;
        if (alignData.elementsNoHeightVAlignDistributed == 1) {
            freeSpaceForDistributedVAlign = (contentHeight - alignData.totalHeightForDistributedVAlign) * 0.5;
        } else {
            freeSpaceForDistributedVAlign = ((contentHeight - alignData.totalHeightForDistributedVAlign) /
                    (alignData.elementsNoHeightVAlignDistributed - 1));

        }

        // repositiong of center group in case it overlaps height top or
        // bottom group
        if (posForCenterVAlign + alignData.totalHeightForCenterVAlign > posForBottomVAlign) {
            posForCenterVAlign = posForBottomVAlign
                    - alignData.totalHeightForCenterVAlign;
        }
        if (posForCenterVAlign < posForTopVAlign
                + alignData.totalHeightForTopVAlign) {
            posForCenterVAlign = posForTopVAlign + alignData.totalHeightForTopVAlign;
        }

        for (int i = 0; i < children.size(); i++) {
            AbstractAGViewDataDesc child = (AbstractAGViewDataDesc)children.get(i);
            AGViewCalcDesc childCalcDesc = child.getCalcDesc();

            if (child.getAlign().equals(AGAlignType.LEFT)) {
                controlPositionX = 0;
            } else if (child.getAlign().equals(AGAlignType.RIGHT)) {
                controlPositionX = contentWidth - childCalcDesc.getBlockWidth();
            } else if (child.getAlign().equals(AGAlignType.CENTER)
                    || child.getAlign().equals(AGAlignType.DISTRIBUTED)) {
                controlPositionX = (contentWidth - childCalcDesc.getBlockWidth()) * 0.5;
            }

            if (child.getVAlign().equals(AGVAlignType.TOP)) {
                controlPositionY = (posForTopVAlign);
                posForTopVAlign += childCalcDesc.getBlockHeight();
            } else if (child.getVAlign().equals(AGVAlignType.CENTER)) {
                controlPositionY = (posForCenterVAlign);
                posForCenterVAlign += childCalcDesc.getBlockHeight();
            } else if (child.getVAlign().equals(AGVAlignType.BOTTOM)) {
                controlPositionY = (posForBottomVAlign);
                posForBottomVAlign += childCalcDesc.getBlockHeight();
            } else if (child.getVAlign().equals(AGVAlignType.DISTRIBUTED)) {
                if (alignData.elementsNoHeightVAlignDistributed == 1) {
                    controlPositionY = posForDistributedVAlign
                            + freeSpaceForDistributedVAlign;
                } else {
                    controlPositionY = posForDistributedVAlign;
                    posForDistributedVAlign += childCalcDesc.getBlockHeight()
                            + freeSpaceForDistributedVAlign;
                }
            }

            childCalcDesc.setPositionX(controlPositionX);
            childCalcDesc.setPositionY(controlPositionY);
        }

        this.layoutChildren(children);
    }

    public void layoutChildren(List<AbstractAGElementDataDesc> children) {
        for (AbstractAGElementDataDesc child : children) {
            CalcManager.getInstance().layout(child);
        }
    }

    @Override
    public void measureBlockWidth(AbstractAGElementDataDesc desc,
                                  double maxFreeSpaceWidth, double maxSpaceForMax) {

        List<AbstractAGElementDataDesc> children = ((IAGCollectionDataDesc) desc).getPresentControls();

        // setWidth
        desc.getCalcDesc().setWidth(maxFreeSpaceWidth);

        // setContentWidth
        ((AGSectionCalcDesc) desc.getCalcDesc()).setContentWidth(
                maxFreeSpaceWidth);

        // measure children
        for (int i = 0; i < children.size(); i++) {
            CalcManager.getInstance().measureBlockWidth(children.get(i),
                    maxFreeSpaceWidth, maxSpaceForMax);
        }


    }

    @Override
    public void measureBlockHeight(AbstractAGElementDataDesc desc, double maxFreeSpaceHeight, double maxSpaceForMax) {
        AbstractAGSectionDataDesc collectionDesc = (AbstractAGSectionDataDesc) desc;
        List<AbstractAGElementDataDesc> children = ((IAGCollectionDataDesc) desc).getPresentControls();
        double childrenHeightSum = 0;
        double maxHeightForChildren = maxFreeSpaceHeight;

        if (collectionDesc.isScrollVertical()) {
            maxHeightForChildren = Integer.MAX_VALUE - 1000;
        }

        for (int i = 0; i < children.size(); i++) {
            CalcManager.getInstance().measureBlockHeight(children.get(i),
                    maxHeightForChildren, maxSpaceForMax);
            childrenHeightSum += ((AbstractAGViewDataDesc)children.get(i))
                    .getCalcDesc().getBlockHeight();
        }

        double minHeight = Math.min(childrenHeightSum, maxFreeSpaceHeight);

        collectionDesc.getCalcDesc().setHeight(minHeight);

        ((AGSectionCalcDesc) desc.getCalcDesc()).setContentHeight(
                childrenHeightSum);
    }

    public AlignData getAlignAndVAlignDataForLayout(
            AbstractAGElementDataDesc dataDesc) {

        AlignData alignData = new AlignData();
        double horizontalSpaceForInnerBorder = 0;

        List<AbstractAGElementDataDesc> children = ((IAGCollectionDataDesc) dataDesc).getPresentControls();

        for (int i = 0; i < children.size(); i++) {
            AbstractAGViewDataDesc child = (AbstractAGViewDataDesc)children.get(i);
            AGViewCalcDesc childDataDesc = child.getCalcDesc();

            // Align
            if (child.getAlign() != null) {  //for unit tests purposes only!!!!!
                if (child.getAlign().equals(AGAlignType.LEFT)) {
                    alignData.totalWidthForLeftAlign += childDataDesc.getBlockWidth()
                            + horizontalSpaceForInnerBorder;
                } else if (child.getAlign().equals(AGAlignType.RIGHT)) {
                    alignData.totalWidthForRightAlign += childDataDesc.getBlockWidth();
                } else if (child.getAlign().equals(AGAlignType.CENTER)) {
                    alignData.totalWidthForCenterAlign += childDataDesc.getBlockWidth();
                } else if (child.getAlign().equals(AGAlignType.DISTRIBUTED)) {
                    alignData.totalWidthForDistributedAlign += childDataDesc
                            .getBlockWidth();
                    alignData.elementsNoWidthAlignDistributed += 1;
                }
            }

            // VAlign
            if (child.getVAlign() != null) { //for unit tests purposes only!!!!!
                if (child.getVAlign().equals(AGVAlignType.TOP)) {
                    alignData.totalHeightForTopVAlign += childDataDesc.getBlockHeight();
                } else if (child.getVAlign().equals(AGVAlignType.CENTER)) {
                    alignData.totalHeightForCenterVAlign += childDataDesc.getBlockHeight();
                } else if (child.getVAlign().equals(AGVAlignType.BOTTOM)) {
                    alignData.totalHeightForBottomVAlign += childDataDesc.getBlockHeight();
                } else if (child.getVAlign().equals(AGVAlignType.DISTRIBUTED)) {
                    alignData.totalHeightForDistributedVAlign += childDataDesc
                            .getBlockHeight();
                    alignData.elementsNoHeightVAlignDistributed += 1;
                }
            }
        }

        return alignData;
    }

    public static void clearInstance(){
        mInstance = null;
    }
	
}
