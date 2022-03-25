package com.kinetise.data.calcmanager;

import com.kinetise.data.calcmanager.struct.AlignData;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGSectionDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.IAGCollectionDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGSectionCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.types.AGAlignType;
import com.kinetise.data.descriptors.types.AGVAlignType;

import java.util.List;

public class AGBodyCalculate extends AGSectionCalculate {

    private static AGBodyCalculate mInstance;
    double mHeaderHeight = 0;
    double mNaviPanelHeight = 0;

    public static AGBodyCalculate getInstance() {
        if (mInstance == null) {
            mInstance = new AGBodyCalculate();
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    @Override
    public void layout(AbstractAGElementDataDesc desc) {
        AlignData alignData = this.getAlignAndVAlignDataForLayout(desc);

        double controlPositionX = 0;
        double controlPositionY = mHeaderHeight;
        AGSectionCalcDesc calcDesc = (AGSectionCalcDesc) desc.getCalcDesc();
        List<AbstractAGElementDataDesc> children = ((IAGCollectionDataDesc) desc).getPresentControls();
        AbstractAGViewDataDesc child;
        AGViewCalcDesc childCalcDesc;
        double contentWidth = calcDesc.getContentWidth();
        double contentHeight = calcDesc.getContentHeight() - mHeaderHeight - mNaviPanelHeight;

        double posForTopVAlign = mHeaderHeight;
        double posForBottomVAlign = posForTopVAlign + (contentHeight - alignData.totalHeightForBottomVAlign);
        double posForCenterVAlign =(posForTopVAlign + (contentHeight - alignData.totalHeightForCenterVAlign) * 0.5);
        double posForDistributedVAlign = posForTopVAlign;

        double freeSpaceForDistributedVAlign;
        if (alignData.elementsNoHeightVAlignDistributed == 1) {
            freeSpaceForDistributedVAlign = (contentHeight - alignData.totalHeightForDistributedVAlign) * 0.5;
        } else {
            freeSpaceForDistributedVAlign = ((contentHeight - alignData.totalHeightForDistributedVAlign) / (alignData.elementsNoHeightVAlignDistributed - 1));
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
            child = (AbstractAGViewDataDesc)children.get(i);
            childCalcDesc = child.getCalcDesc();

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

    @Override
    public void measureBlockHeight(AbstractAGElementDataDesc desc,
                                   double maxFreeSpaceHeight, double maxSpaceForMax) {
        List<AbstractAGElementDataDesc> children = ((IAGCollectionDataDesc) desc).getPresentControls();
        double childrenHeightSum = 0;
        double maxHeightForChildren = maxFreeSpaceHeight;
        //body jest zawsze scrollowane vertykalnie wiec nie potrzebuje warunku
        if (((AbstractAGSectionDataDesc) desc).isScrollVertical()) {
            maxHeightForChildren = Integer.MAX_VALUE - 1000;
        }

        for (int i = 0; i < children.size(); i++) {
            CalcManager.getInstance().measureBlockHeight(children.get(i), maxHeightForChildren, maxSpaceForMax);
            AGViewCalcDesc calcDesc = ((AGViewCalcDesc) children.get(i).getCalcDesc());
            double heightTmp = calcDesc.getBlockHeight();
            childrenHeightSum += heightTmp;
        }

        double minHeight = maxFreeSpaceHeight;//Math.min(childrenHeightSum, maxFreeSpaceHeight);
        minHeight += mHeaderHeight + mNaviPanelHeight;
        childrenHeightSum += mHeaderHeight + mNaviPanelHeight;
        desc.getCalcDesc().setHeight(minHeight);
        AGSectionCalcDesc bodyCalcDesc = (AGSectionCalcDesc) desc.getCalcDesc();

        bodyCalcDesc.setContentHeight(childrenHeightSum);
    }

    public double getHeaderHeight() {
        return mHeaderHeight;
    }

    public void setHeaderHeight(double headerHeight) {
        mHeaderHeight = headerHeight;
    }

    public double getNaviPanelHeight() {
        return mNaviPanelHeight;
    }

    public void setNaviPanelHeight(double naviPanelHeight) {
        mNaviPanelHeight = naviPanelHeight;
    }

}
