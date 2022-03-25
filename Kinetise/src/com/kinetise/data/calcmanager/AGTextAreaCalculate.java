package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.TextCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGTextAreaDataDesc;

public class AGTextAreaCalculate extends AGTextInputCalculate {
    private static AGTextAreaCalculate mInstance;

    @Override
    public void measureHeightForMin(AbstractAGElementDataDesc desc,
                                    double maxHeight, double maxSpaceForMax) {

        AGTextAreaDataDesc textAreaDataDesc = (AGTextAreaDataDesc) desc;
        AGViewCalcDesc calcDesc = textAreaDataDesc.getCalcDesc();
        TextCalcDesc textCalcDesc = textAreaDataDesc.getTextDescriptor().getCalcDescriptor();
        int rows = textAreaDataDesc.getRows();
        double oneLineHeight = Math.floor(textCalcDesc.getFontSize() * TEXT_FIELD_LINE_HEIGHT);

        double measuredHeight = (double)rows * oneLineHeight + calcDesc.getPaddingBottom() + calcDesc.getPaddingTop();

        calcDesc.setHeight(measuredHeight);

    }


    @Override
    public void measureWidthForMin(AbstractAGElementDataDesc desc,
                                   double maxWidth, double maxSpaceForMax) {
    //textAreas width cannot be set to min
    }

    public static AGTextAreaCalculate getInstance() {
        if (mInstance == null) {
            mInstance = new AGTextAreaCalculate();
        }

        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }
}
