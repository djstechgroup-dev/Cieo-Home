package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGTextInputCalcDescriptor;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.BasicViewCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.DecoratorCalcDescriptor;
import com.kinetise.data.descriptors.calcdescriptors.TextCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGTextInputDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.DecoratorDescriptor;
import com.kinetise.data.descriptors.datadescriptors.components.TextDescriptor;
import com.kinetise.data.descriptors.types.AGAlignType;
import com.kinetise.data.descriptors.types.IntQuad;
import com.kinetise.data.descriptors.types.SizeQuad;

public class AGTextInputCalculate extends AbstractCalculate {

    private static AGTextInputCalculate mInstance;

    protected static final double TEXT_FIELD_LINE_HEIGHT = 1.36;

    @Override
    public void measureHeightForMin(AbstractAGElementDataDesc desc, double maxHeight, double maxSpaceForMax) {
        AGTextInputDataDesc textInputDesc = (AGTextInputDataDesc) desc;
        AGViewCalcDesc viewCalcDesc = textInputDesc.getCalcDesc();
        TextDescriptor textDescriptor = textInputDesc.getTextDescriptor();
        TextCalcDesc textCalcDesc = textDescriptor.getCalcDescriptor();

        double lineHeight = Math.floor(textCalcDesc.getFontSize() * TEXT_FIELD_LINE_HEIGHT);
        double measuredHeight = lineHeight + viewCalcDesc.getPaddingBottom() + viewCalcDesc.getPaddingTop();
        viewCalcDesc.setHeight(measuredHeight);
    }

    private void calculateTextVerticalPaddings(AbstractAGElementDataDesc desc) {
        AGTextInputDataDesc textInputDesc = (AGTextInputDataDesc) desc;
        SizeQuad textPaddingDesc = textInputDesc.getTextDescriptor().getPadding();
        IntQuad textPadding = textInputDesc.getCalcDesc().getTextPadding();
        textPadding.bottom = (int) Math.round(textPaddingDesc.getBottom().inPixels());
        textPadding.top = (int) Math.round(textPaddingDesc.getTop().inPixels());
    }

    private void calculateTextHorizontalPaddings(AbstractAGElementDataDesc desc) {
        AGTextInputDataDesc textInputDesc = (AGTextInputDataDesc) desc;
        SizeQuad textPaddingDesc = textInputDesc.getTextDescriptor().getPadding();
        IntQuad textPadding = textInputDesc.getCalcDesc().getTextPadding();
        textPadding.left = (int) Math.round(textPaddingDesc.getLeft().inPixels());
        textPadding.right = (int) Math.round(textPaddingDesc.getRight().inPixels());
    }

    @Override
    public void measureBlockHeight(AbstractAGElementDataDesc dataDesc, double maxHeight, double maxSpaceForMax) {
        AGTextInputDataDesc textInputDesc = (AGTextInputDataDesc) dataDesc;
        super.measureBlockHeight(dataDesc, maxHeight, maxSpaceForMax);
        DecoratorDescriptor decoratorDescriptor = textInputDesc.getDecoratorDescriptor();
        AGDecoratorCalculate.measureHeight(decoratorDescriptor.getCalcDescriptor(),decoratorDescriptor);
        calculateTextVerticalPaddings(dataDesc);
        AGTextInputCalcDescriptor textInputCalcDesc = textInputDesc.getCalcDesc();
        BasicViewCalcDesc editTextCalcDesc = textInputCalcDesc.getEditTextCalcDesc();
        double editTextViewHeight = textInputCalcDesc.getContentSpaceHeight() - textInputCalcDesc.getTextPadding().top - textInputCalcDesc.getTextPadding().bottom;
        editTextCalcDesc.setHeight((int) Math.round(editTextViewHeight));
    }

    @Override
    public void measureBlockWidth(AbstractAGElementDataDesc dataDesc, double maxWidth, double maxSpaceForMax) {
        AGTextInputDataDesc textInputDesc = (AGTextInputDataDesc) dataDesc;
        AGTextCalculate calculate = new AGTextCalculate();
        calculate.measureFontSize(textInputDesc.getTextDescriptor());
        super.measureBlockWidth(dataDesc, maxWidth, maxSpaceForMax);

        calculateTextHorizontalPaddings(dataDesc);

        DecoratorDescriptor decoratorDescriptor = textInputDesc.getDecoratorDescriptor();
        AGDecoratorCalculate.measureWidth(decoratorDescriptor.getCalcDescriptor(),decoratorDescriptor);

        AGTextInputCalcDescriptor textInputCalcDesc = textInputDesc.getCalcDesc();
        BasicViewCalcDesc editTextCalcDesc = textInputCalcDesc.getEditTextCalcDesc();
        double editTextViewWidth = textInputCalcDesc.getContentSpaceWidth() - textInputCalcDesc.getTextPadding().left - textInputCalcDesc.getTextPadding().right - decoratorDescriptor.getCalcDescriptor().getWidth();
        editTextCalcDesc.setWidth((int) Math.round(editTextViewWidth));

    }

    @Override
    public void layout(AbstractAGElementDataDesc desc) {
        AGTextInputDataDesc textDataDesc = (AGTextInputDataDesc) desc;
        super.layout(desc);
        DecoratorDescriptor decoratorDescriptor = textDataDesc.getDecoratorDescriptor();
        AGDecoratorCalculate.layout(decoratorDescriptor.getCalcDescriptor(),decoratorDescriptor, textDataDesc.getCalcDesc());
        calculateYPosition(textDataDesc.getCalcDesc());
        calculateXPosition(decoratorDescriptor.getCalcDescriptor(), decoratorDescriptor.getAlign(), textDataDesc.getCalcDesc());
    }

    public static void calculateYPosition( AGTextInputCalcDescriptor parentCalcDescriptor){
        parentCalcDescriptor.getEditTextCalcDesc().setPositionY((int)(parentCalcDescriptor.getBorder().getTop() + parentCalcDescriptor.getPaddingTop() + parentCalcDescriptor.getTextPadding().top));
    }

    public static void calculateXPosition(DecoratorCalcDescriptor calcDescriptor, AGAlignType decoratorAlign, AGTextInputCalcDescriptor parentCalcDescriptor){
        int paddingLeft = (int) parentCalcDescriptor.getPaddingLeft();
        int textPaddingLeft = parentCalcDescriptor.getTextPadding().left;

        switch (decoratorAlign) {

            case RIGHT:
                parentCalcDescriptor.getEditTextCalcDesc().setPositionX(paddingLeft+textPaddingLeft);
                return;
            case LEFT:
            default:
                parentCalcDescriptor.getEditTextCalcDesc().setPositionX((int) (paddingLeft+textPaddingLeft+calcDescriptor.getWidth()));
        }
    }

    @Override
    public void measureWidthForMin(AbstractAGElementDataDesc desc,
                                   double maxWidth, double maxSpaceForMax) {
        // nothing to do, abstract method implementation
    }

    public static AGTextInputCalculate getInstance() {
        if (mInstance == null) {
            mInstance = new AGTextInputCalculate();
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

}
