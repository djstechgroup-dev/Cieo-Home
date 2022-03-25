package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.types.AGAlignType;
import com.kinetise.data.descriptors.types.AGVAlignType;

import java.util.List;

public class ThumbnailsLayouter {

    private AbstractAGContainerDataDesc mContainerDesc;
    private List<AbstractAGElementDataDesc> mChildren;
    private ChildrenLayout mLayout;
    double mHorizontalSpace;

    public ThumbnailsLayouter(AbstractAGContainerDataDesc containerDataDesc, double maxWidth){
        mContainerDesc = containerDataDesc;
        mChildren = containerDataDesc.getPresentControls();
        mHorizontalSpace =  maxWidth;
    }

    public void layoutChildrenforLayoutThumbnails(){
        mLayout = fitChildrenIntoRows();
        layoutChildren(mLayout);
    }

    public ChildrenLayout fitChildrenIntoRows() {
        ChildrenLayout layout = new ChildrenLayout();
        double hightOfHighestChildrenInRow = 0;
        double widthOfChildrenInRow = 0;
        int numberOfChildrenInRow = 0;
        AGViewCalcDesc childCalcDesc;
        for(int childIndex=0;childIndex<mChildren.size();childIndex++) {
            childCalcDesc = (AGViewCalcDesc) mChildren.get(childIndex).getCalcDesc();
            double childrenWidth = childCalcDesc.getBlockWidth();
            double childrenHeight = childCalcDesc.getBlockHeight();
            if (!childFitsInRow(widthOfChildrenInRow, childrenWidth)) {
                if (numberOfChildrenInRow == 0) {
                    layout.addRow(1, childrenWidth, childrenHeight);
                } else {
                    layout.addRow(numberOfChildrenInRow, widthOfChildrenInRow, hightOfHighestChildrenInRow);
                    numberOfChildrenInRow = 1;
                    widthOfChildrenInRow = childrenWidth;
                    hightOfHighestChildrenInRow = childrenHeight;
                }
            } else {
                widthOfChildrenInRow += childrenWidth;
                hightOfHighestChildrenInRow = Math.max(childrenHeight,hightOfHighestChildrenInRow);
                numberOfChildrenInRow++;
            }
        }
        if(numberOfChildrenInRow>0)
            layout.addRow(numberOfChildrenInRow,widthOfChildrenInRow,hightOfHighestChildrenInRow);
        return layout;
    }


    private boolean childFitsInRow(double currentWidthOfChildrenInRow, double childWidth) {
        return currentWidthOfChildrenInRow + childWidth <= mHorizontalSpace;
    }

    private void layoutChildren(ChildrenLayout childrenLayout) {
        AGAlignType align = mContainerDesc.getInnerAlign();
        AGVAlignType vAlign;
        double horizontalPosition;
        double verticalPosition;
        double rowHight;
        double rowsVerticalPosition = 0;
        AGViewCalcDesc childCalcDesc;
        int childIndex = 0;
        for(int row=0;row<childrenLayout.getNumberOfRows();row++) {
            horizontalPosition = getHorizontalPositionOfFirstChildInARow(childrenLayout.getRowWidth(row), align);
            rowHight = childrenLayout.getRowHight(row);

            for(int i=0;i<childrenLayout.getNumberOfChildrenInRow(row);i++){
                childCalcDesc = (AGViewCalcDesc) mChildren.get(childIndex).getCalcDesc();
                vAlign = ((AbstractAGViewDataDesc)mChildren.get(childIndex)).getVAlign();
                childIndex++;
                childCalcDesc.setPositionX(horizontalPosition);
                horizontalPosition += childCalcDesc.getBlockWidth();
                verticalPosition = rowsVerticalPosition+getVerticalPositionOfChildInsideRow(rowHight, childCalcDesc.getBlockHeight(), vAlign);
                childCalcDesc.setPositionY(verticalPosition);
            }
            rowsVerticalPosition += childrenLayout.getRowHight(row);
        }
    }

    private double getVerticalPositionOfChildInsideRow(double rowHight, double childHight, AGVAlignType vAlign) {
        double verticalFreeSpace = rowHight - childHight;
        switch (vAlign) {
            case TOP:
                return 0;
            case CENTER:
                return verticalFreeSpace * 0.5;
            case BOTTOM:
            default:
                return verticalFreeSpace;
        }
    }

    private double getHorizontalPositionOfFirstChildInARow(double combinedWidthOfAllChildrenInRow, AGAlignType align) {
        double horizontalFreeSpace = mHorizontalSpace - combinedWidthOfAllChildrenInRow;
        switch (align) {
            case LEFT:
                return 0;
            case CENTER:
                return horizontalFreeSpace * 0.5;
            case RIGHT:
            default:
                return horizontalFreeSpace;
        }
    }
}
