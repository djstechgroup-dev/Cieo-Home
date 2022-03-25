package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGCompoundButtonCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.TextCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGCompoundButtonDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.ITextDescriptor;
import com.kinetise.data.descriptors.datadescriptors.components.TextDescriptor;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.descriptors.types.AGUnitType;
import com.kinetise.data.systemdisplay.TextMeasurer;

public class AGCompoundButtonCalculate extends AbstractCalculate {

    private static AGCompoundButtonCalculate mInstance;

    public static AGCompoundButtonCalculate getInstance() {
        if (mInstance == null) {
            mInstance = new AGCompoundButtonCalculate();
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    @Override
    public void layout(AbstractAGElementDataDesc desc) {
        TextDescriptor textDescriptor= ((ITextDescriptor)desc).getTextDescriptor();
        TextCalcDesc textCalcDesc = textDescriptor.getCalcDescriptor();
        layoutText(textCalcDesc.getTextWidth(), textCalcDesc.getTextHeight(), textDescriptor);
    }

    protected void layoutText(double width,double height, TextDescriptor textDesc){
        TextMeasurer textMeasurer = new TextMeasurer(textDesc);
        textMeasurer.layout(width, height);
    }

    public void measureWidthForMin(AbstractAGElementDataDesc dataDesc, double maxWidth, double maxSpaceForMax) {

        AbstractAGCompoundButtonDataDesc compoundButtonDataDesc = (AbstractAGCompoundButtonDataDesc) dataDesc;
        AGCompoundButtonCalcDesc calcDesc = (AGCompoundButtonCalcDesc) dataDesc.getCalcDesc();

        double checkedWidth = compoundButtonDataDesc.getCheckWidth().inPixels();
        double innerSpace = compoundButtonDataDesc.getInnerSpace().inPixels();

        calcDesc.setCheckedWidth(checkedWidth);
        calcDesc.setInnerSpace(innerSpace);

        measureFontSize(dataDesc);

        TextDescriptor textDescriptor = compoundButtonDataDesc.getTextDescriptor();
        TextMeasurer textMeasurer = new TextMeasurer(textDescriptor);
        textMeasurer.measure(textDescriptor.getText().getStringValue(), maxWidth - checkedWidth - innerSpace);

        double measuredWidth = textDescriptor.getCalcDescriptor().getTextWidth();
        if(measuredWidth!=0)
            measuredWidth+=innerSpace;
        measuredWidth += checkedWidth;

        if (measuredWidth > maxWidth) {
            calcDesc.setWidth(maxWidth + calcDesc.getPaddingLeft() + calcDesc.getPaddingRight());
        } else {
            calcDesc.setWidth(measuredWidth + calcDesc.getPaddingLeft() + calcDesc.getPaddingRight());
        }
    }

    public void measureHeightForMin(AbstractAGElementDataDesc dataDesc, double maxHeight, double maxSpaceForMax) {
        AbstractAGCompoundButtonDataDesc compoundButtonDataDesc = (AbstractAGCompoundButtonDataDesc) dataDesc;
        AGCompoundButtonCalcDesc calcDesc = compoundButtonDataDesc.getCalcDesc();
        TextCalcDesc textCalcDesc = compoundButtonDataDesc.getTextDescriptor().getCalcDescriptor();

        double resultHeight = Math.max(textCalcDesc.getTextHeight(), calcDesc.getCheckedHeight());
        resultHeight = Math.min(resultHeight, maxHeight);

        calcDesc.setHeight(resultHeight + calcDesc.getPaddingTop() + calcDesc.getPaddingBottom());

    }

    public void measureBlockWidth(AbstractAGElementDataDesc dataDesc, double maxFreeSpace, double maxSpaceForMax) {
        super.measureBlockWidth(dataDesc, maxFreeSpace, maxSpaceForMax);
        AbstractAGCompoundButtonDataDesc compoundButtonDataDesc = (AbstractAGCompoundButtonDataDesc) dataDesc;

        AGSizeDesc agSizeDesc = compoundButtonDataDesc.getWidth();

        if (agSizeDesc.getDescUnit().equals(AGUnitType.MIN))
            return;

        AGCompoundButtonCalcDesc calcDesc = (AGCompoundButtonCalcDesc) dataDesc.getCalcDesc();

        double checkedWidth =compoundButtonDataDesc.getCheckWidth().inPixels();
        double innerSpace = compoundButtonDataDesc.getInnerSpace().inPixels();
        calcDesc.setCheckedWidth(checkedWidth);
        calcDesc.setInnerSpace(innerSpace);

        measureFontSize(dataDesc);


        TextDescriptor textDescriptor = compoundButtonDataDesc.getTextDescriptor();
        TextMeasurer textMeasure = new TextMeasurer(textDescriptor);
        textMeasure.measure(textDescriptor.getText().getStringValue(),
                calcDesc.getWidth() - calcDesc.getPaddingLeft() - calcDesc.getPaddingRight() - checkedWidth - innerSpace);
    }

    public void measureBlockHeight(AbstractAGElementDataDesc dataDesc, double maxFreeSpace, double maxSpaceForMax) {
        AbstractAGCompoundButtonDataDesc compoundDataDesc = (AbstractAGCompoundButtonDataDesc) dataDesc;
        AGCompoundButtonCalcDesc calcDesc = (AGCompoundButtonCalcDesc) dataDesc.getCalcDesc();
        double checkedHeight = compoundDataDesc.getCheckHeight().inPixels();

        calcDesc.setCheckedHeight(checkedHeight);
        super.measureBlockHeight(dataDesc, maxFreeSpace, maxSpaceForMax);
    }

    private void measureFontSize(AbstractAGElementDataDesc dataDesc) {
        AbstractAGCompoundButtonDataDesc compoundDataDesc = (AbstractAGCompoundButtonDataDesc) dataDesc;
        compoundDataDesc.getTextDescriptor().getCalcDescriptor().setFontSize(compoundDataDesc.getTextDescriptor().getFontSize());
    }

}
