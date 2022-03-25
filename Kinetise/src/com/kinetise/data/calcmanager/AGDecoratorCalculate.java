package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.DecoratorCalcDescriptor;
import com.kinetise.data.descriptors.datadescriptors.components.DecoratorDescriptor;
import com.kinetise.data.descriptors.types.AGAlignType;
import com.kinetise.data.descriptors.types.AGVAlignType;
import com.kinetise.data.descriptors.types.Quad;

public class AGDecoratorCalculate {
    public static void measureWidth(DecoratorCalcDescriptor calcDescriptor, DecoratorDescriptor decoratorDescriptor) {
        if (hasDecorator(decoratorDescriptor)) {
            calcDescriptor.setWidth(decoratorDescriptor.getWidth().inPixels());
        } else {
            calcDescriptor.setWidth(0);
        }
    }

    public static void measureHeight(DecoratorCalcDescriptor calcDescriptor, DecoratorDescriptor decoratorDescriptor) {
        if (hasDecorator(decoratorDescriptor)) {
            calcDescriptor.setHeight(decoratorDescriptor.getHeight().inPixels());
        } else {
            calcDescriptor.setHeight(0);
        }
    }

    private static boolean hasDecorator(DecoratorDescriptor decoratorDescriptor) {
        return decoratorDescriptor.getImageDescriptor().hasDecorator();
    }

    public static void layout(DecoratorCalcDescriptor calcDescriptor, DecoratorDescriptor decoratorDescriptor, AGViewCalcDesc parentDescriptor) {
        calcDescriptor.setPositionY(calculateYPosition(calcDescriptor, decoratorDescriptor.getVAlign(), parentDescriptor));
        calcDescriptor.setPositionX(calculateXPosition(calcDescriptor, decoratorDescriptor.getAlign(), parentDescriptor));
    }

    public static int calculateYPosition(DecoratorCalcDescriptor calcDescriptor, AGVAlignType align, AGViewCalcDesc parentCalcDescriptor) {
        Quad boardedSize = parentCalcDescriptor.getBorder();
        int paddingTop = (int) parentCalcDescriptor.getPaddingTop();
        int paddingBottom = (int) parentCalcDescriptor.getPaddingBottom();
        int additionalFreeSpace = (int) Math.round(parentCalcDescriptor.getBlockHeight() - calcDescriptor.getHeight());
        int middleOfTheDrawingSurface = (int) (parentCalcDescriptor.getContentHeight() / 2 + paddingTop);
        switch (align) {
            case TOP:
                return (int) boardedSize.getTop() + paddingTop;
            case BOTTOM:
                return additionalFreeSpace - (int) boardedSize.getBottom() - paddingBottom;
            case CENTER:
            default:
                return middleOfTheDrawingSurface - (int) calcDescriptor.getHeight() / 2;
        }
    }

    public static int calculateXPosition(DecoratorCalcDescriptor calcDescriptor, AGAlignType align, AGViewCalcDesc parentCalcDescriptor) {
        Quad boardedSize = parentCalcDescriptor.getBorder();
        int paddingLeft = (int) parentCalcDescriptor.getPaddingLeft();
        int paddingRight = (int) parentCalcDescriptor.getPaddingRight();

        switch (align) {

            case RIGHT:
                return (int) Math.round(parentCalcDescriptor.getViewWidth() - paddingRight - calcDescriptor.getWidth());
            case LEFT:
            default:
                return (int) Math.round(boardedSize.getLeft() + paddingLeft);
        }
    }
}
