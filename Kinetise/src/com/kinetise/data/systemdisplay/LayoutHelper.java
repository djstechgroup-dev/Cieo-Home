package com.kinetise.data.systemdisplay;

import android.view.View;

import com.kinetise.data.descriptors.calcdescriptors.AGElementCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.components.DecoratorDescriptor;
import com.kinetise.data.descriptors.types.AGAlignType;

public class LayoutHelper {
    public static void centerHorizontal(AGElementCalcDesc calcDesc, int parentViewWidth){
        int innerViewWidth = calcDesc.getViewWidth();
        int position = (parentViewWidth-innerViewWidth)/2;
        if(position<0)
            position = 0;
        calcDesc.setPositionX(position);

    }

    public static void centerVertical(AGElementCalcDesc calcDesc, int parentViewHeight){
        int innerViewHeight = calcDesc.getViewHeight();
        int position = (parentViewHeight-innerViewHeight)/2;
        if(position<0)
            position = 0;
        calcDesc.setPositionY(position);
    }

    public static void layoutCenter(View innerView, AGViewCalcDesc innerViewCalcDesc, AGViewCalcDesc parentCalcDesc){
        LayoutHelper.centerHorizontal(innerViewCalcDesc, (int) parentCalcDesc.getContentSpaceWidth());
        LayoutHelper.centerVertical(innerViewCalcDesc,  (int) parentCalcDesc.getContentSpaceHeight());
        int top = (int) Math.round(parentCalcDesc.getBorder().getTop() + parentCalcDesc.getPaddingTop() + innerViewCalcDesc.getPositionY());
        int left = (int) Math.round(parentCalcDesc.getBorder().getLeft() + parentCalcDesc.getPaddingLeft() + innerViewCalcDesc.getPositionX());
        int right = left + innerViewCalcDesc.getViewWidth();
        int bottom = top + innerViewCalcDesc.getViewHeight();

        innerView.layout(left, top, right, bottom);
    }

    public static void layoutFill(View innerView, AGViewCalcDesc parentCalcDesc){
        int top = (int) Math.round(parentCalcDesc.getBorder().getTop() + parentCalcDesc.getPaddingTop());
        int left = (int) Math.round(parentCalcDesc.getBorder().getLeft() + parentCalcDesc.getPaddingLeft());
        int right = left + (int) Math.round(parentCalcDesc.getContentSpaceWidth());
        int bottom = top + (int) Math.round(parentCalcDesc.getContentSpaceHeight());

        innerView.layout(left, top, right, bottom);
    }



    public static void layoutWithDecorator(View innerView, AGViewCalcDesc parentCalcDesc, DecoratorDescriptor decoratorDesc){
        int top = (int) Math.round(parentCalcDesc.getBorder().getTop() + parentCalcDesc.getPaddingTop());
        int bottom = top + (int) Math.round(parentCalcDesc.getContentSpaceHeight());
        int left;
        double decoratorWidth = decoratorDesc.getCalcDescriptor().getWidth();
        if(decoratorDesc.getAlign() == AGAlignType.LEFT){
            left = (int) Math.round(parentCalcDesc.getBorder().getLeft() + parentCalcDesc.getPaddingLeft() + decoratorWidth);
        } else {
            left = (int) Math.round(parentCalcDesc.getBorder().getLeft() + parentCalcDesc.getPaddingLeft());
        }
        int right = left + (int) Math.round(parentCalcDesc.getContentSpaceWidth() - decoratorWidth);

        innerView.layout(left, top, right, bottom);
    }

    public static void measureFill(View innerView, AGViewCalcDesc parentCalcDesc){
        innerView.measure(View.MeasureSpec.makeMeasureSpec((int)Math.round(parentCalcDesc.getContentSpaceWidth()), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec((int)Math.round(parentCalcDesc.getContentSpaceHeight()), View.MeasureSpec.EXACTLY));
    }

    public static void measureExactly(View innerView, int width, int height){
        innerView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));

    }


}
