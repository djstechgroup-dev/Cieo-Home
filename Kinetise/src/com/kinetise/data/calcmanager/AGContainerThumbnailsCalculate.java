package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

import java.util.List;

public class AGContainerThumbnailsCalculate extends AbstractAGContainerCalculate {

    static AGContainerThumbnailsCalculate mInstance;

    public static AGContainerThumbnailsCalculate getInstance() {
        if (mInstance == null) {
            mInstance = new AGContainerThumbnailsCalculate();
        }

        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    public void layout(AbstractAGElementDataDesc dataDesc) {
        AbstractAGContainerDataDesc containerDesc = (AbstractAGContainerDataDesc) dataDesc;
        double contentWidth = containerDesc.getCalcDesc().getContentWidth();
        ThumbnailsLayouter layouter = new ThumbnailsLayouter(containerDesc, contentWidth);
        layouter.layoutChildrenforLayoutThumbnails();
        super.layout(dataDesc);
    }

    public void measureWidthForMin(AbstractAGElementDataDesc dataDesc, double maxWidth, double maxSpaceForMax) {
        AbstractAGContainerDataDesc containerDescriptor = (AbstractAGContainerDataDesc) dataDesc;
        double childrenWidth = measureWidthForChildren(containerDescriptor, maxWidth, maxSpaceForMax);
        containerDescriptor.getCalcDesc().setContentWidth(childrenWidth);
        super.measureWidthForMin(dataDesc, maxWidth, maxSpaceForMax);
    }

    public double measureWidthForChildren(AbstractAGContainerDataDesc containerDescriptor, double maxFreeSpace, double maxSpaceForMax) {
        if (containerDescriptor.isScrollHorizontal()) {
            maxFreeSpace = Integer.MAX_VALUE;
        }
        measureBlockWidthOfEachChildren(maxFreeSpace, maxSpaceForMax, containerDescriptor);
        return calculateWidestRow(containerDescriptor,maxFreeSpace);
    }

    protected void measureBlockWidthOfEachChildren(double maxFreeSpace, double maxSpaceForMax, AbstractAGContainerDataDesc containerDescriptor) {
        List<AbstractAGElementDataDesc> children = containerDescriptor.getPresentControls();
        for (int childIndex = 0; childIndex < children.size(); childIndex++) {
            CalcManager.getInstance().measureBlockWidth(children.get(childIndex), maxFreeSpace, maxSpaceForMax);
        }
    }

    protected double calculateWidestRow(AbstractAGContainerDataDesc dataDesc,double maxFreeSpace) {
        ChildrenLayout layout = getChildrenLayout(maxFreeSpace, dataDesc);
        return layout.getWidestRowsWidth();
    }

    public void measureHeightForMin(AbstractAGElementDataDesc dataDesc, double maxFreeSpace, double maxSpaceForMax) {
        double childrenHight = measureHeightForChildren((AbstractAGContainerDataDesc)dataDesc, maxFreeSpace, maxSpaceForMax);
        dataDesc.getCalcDesc().setHeight(childrenHight);
        super.measureHeightForMin(dataDesc, maxFreeSpace, maxSpaceForMax);
    }

    public double measureHeightForChildren(AbstractAGContainerDataDesc containerDataDesc, double maxFreeSpace, double maxSpaceForMax) {
        if (containerDataDesc.isScrollVertical()) {
            maxFreeSpace = Integer.MAX_VALUE;
        }
        measureBlockHightOfEachChildren(maxFreeSpace, maxSpaceForMax, containerDataDesc);
        ChildrenLayout layout = getChildrenLayout(containerDataDesc.getCalcDesc().getContentWidth(), containerDataDesc);
        return layout.getCombinedRowsHight();
    }

    protected ChildrenLayout getChildrenLayout(double maxFreeSpace, AbstractAGContainerDataDesc containerDataDesc) {
        ThumbnailsLayouter layouter = new ThumbnailsLayouter(containerDataDesc,maxFreeSpace);
        return layouter.fitChildrenIntoRows();
    }

    protected void measureBlockHightOfEachChildren(double maxFreeSpace, double maxSpaceForMax, AbstractAGContainerDataDesc containerDataDesc) {
        List<AbstractAGElementDataDesc> children = containerDataDesc.getPresentControls();
        for (int childIndex = 0; childIndex < children.size(); childIndex++) {
            CalcManager.getInstance().measureBlockHeight(children.get(childIndex), maxFreeSpace, maxSpaceForMax);
        }
    }

    public double getVerticalSpaceForInnerBorder(AbstractAGContainerDataDesc dataDesc) {
        return 0;
    }

    public double getHorizontalSpaceForInnerBorder(AbstractAGContainerDataDesc dataDesc) {
        return 0;
    }
}
