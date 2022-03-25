package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.TextCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGTextDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.TextDescriptor;
import com.kinetise.data.descriptors.types.AGUnitType;
import com.kinetise.data.systemdisplay.TextMeasurer;


public class AGTextCalculate extends AbstractCalculate {

    private static AGTextCalculate mInstance;

    public void measureBlockWidth(AbstractAGElementDataDesc dataDesc, double maxWidth, double maxSpaceForMax) {
        super.measureBlockWidth(dataDesc, maxWidth, maxSpaceForMax);

        AGTextDataDesc textDataDesc = (AGTextDataDesc) dataDesc;
        AGViewCalcDesc calcDesc = textDataDesc.getCalcDesc();

        AGUnitType widthUnitType = textDataDesc.getWidth().getDescUnit();
        if (widthUnitType.equals(AGUnitType.MIN)) {
            return;
        }
        double maxCalculatedWidth = calcDesc.getWidth() - calcDesc.getPaddingLeft() - calcDesc.getPaddingRight();
        measureText(maxCalculatedWidth, textDataDesc.getTextDescriptor());
    }

    @Override
    public void measureBlockHeight(AbstractAGElementDataDesc dataDesc, double maxHeight, double maxSpaceForMax) {
        super.measureBlockHeight(dataDesc, maxHeight, maxSpaceForMax);

        AGTextDataDesc textDataDesc = (AGTextDataDesc) dataDesc;
        AGViewCalcDesc calcDesc = textDataDesc.getCalcDesc();

        AGUnitType heightUnit = textDataDesc.getHeight().getDescUnit();
        if (heightUnit == AGUnitType.MIN){
            double contentVerticalSpace = maxHeight  - getTotalVerticalPaddingsHeight(calcDesc);
            double contentVerticalSpaceForMax = maxSpaceForMax - getTotalVerticalPaddingsHeight(calcDesc);
            measureHeightForMin(dataDesc,contentVerticalSpace, contentVerticalSpaceForMax);
            }
    }

    @Override
    public void measureHeightForMin(AbstractAGElementDataDesc dataDesc, double maxHeight, double maxSpaceForMax) {
        AGTextDataDesc textDataDesc = (AGTextDataDesc) dataDesc;
        AGViewCalcDesc viewCalcDesc = textDataDesc.getCalcDesc();
        TextCalcDesc textCalcDesc = textDataDesc.getTextDescriptor().getCalcDescriptor();

        double measuredHeight = textCalcDesc.getTextHeight();
        measuredHeight = Math.min(measuredHeight,maxHeight);
        viewCalcDesc.setHeight(measuredHeight + viewCalcDesc.getPaddingTop() + viewCalcDesc.getPaddingBottom());
    }

    @Override
    public void measureWidthForMin(AbstractAGElementDataDesc dataDesc, double maxWidth, double maxSpaceForMax) {
        AGTextDataDesc textDataDesc = (AGTextDataDesc) dataDesc;
        AGViewCalcDesc calcDesc = textDataDesc.getCalcDesc();

        double measuredWidth = measureText(maxWidth, textDataDesc.getTextDescriptor());
        measuredWidth = Math.min(measuredWidth,maxWidth);
        calcDesc.setWidth(measuredWidth + calcDesc.getPaddingLeft() + calcDesc.getPaddingRight());
    }

    protected double measureText(double maxWidth,TextDescriptor textDesc) {
        measureFontSize(textDesc);
        TextMeasurer textMeasurer = new TextMeasurer(textDesc);
        textMeasurer.measure(textDesc.getText().getStringValue(), maxWidth);
        return textDesc.getCalcDescriptor().getTextWidth();
    }

    protected void layoutText(double width,double height, TextDescriptor textDesc){
        TextMeasurer textMeasurer = new TextMeasurer(textDesc);
        textMeasurer.layout(width,height);
    }

    public void measureFontSize(TextDescriptor textDesc) {
        textDesc.getCalcDescriptor().setFontSize(textDesc.getFontSize());
    }

    public static AGTextCalculate getInstance() {
        if (mInstance == null) {
            mInstance = new AGTextCalculate();
        }
        return mInstance;
    }

    protected AGTextCalculate(){}

    public static void clearInstance(){
        mInstance = null;
    }


    @Override
    public void layout(AbstractAGElementDataDesc desc) {
        AGTextDataDesc textDataDesc = (AGTextDataDesc) desc;
        AGViewCalcDesc calcDesc = textDataDesc.getCalcDesc();
        layoutText(calcDesc.getContentWidth(), calcDesc.getContentHeight(), textDataDesc.getTextDescriptor());
    }

}
