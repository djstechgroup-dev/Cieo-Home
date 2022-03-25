package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGDatePickerDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.DecoratorDescriptor;

public class AGDatePickerCalculate extends AGTextImageCalculate{
    private static AGDatePickerCalculate mInstance;

    public static AGDatePickerCalculate getInstance(){
        if(mInstance == null){
            mInstance = new AGDatePickerCalculate();
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    @Override
    public void measureBlockWidth(AbstractAGElementDataDesc dataDesc, double maxWidth, double maxSpaceForMax) {
        super.measureBlockWidth(dataDesc, maxWidth, maxSpaceForMax);
        AGDatePickerDataDesc descriptor = (AGDatePickerDataDesc) dataDesc;
        DecoratorDescriptor decoratorDescriptor = descriptor.getDecoratorDescriptor();
        AGDecoratorCalculate.measureWidth(decoratorDescriptor.getCalcDescriptor(), decoratorDescriptor);
    }

    @Override
    public void measureBlockHeight(AbstractAGElementDataDesc dataDesc, double maxHeight, double maxSpaceForMax) {
        super.measureBlockHeight(dataDesc, maxHeight, maxSpaceForMax);
        AGDatePickerDataDesc descriptor = (AGDatePickerDataDesc) dataDesc;
        DecoratorDescriptor decoratorDescriptor = descriptor.getDecoratorDescriptor();
        AGDecoratorCalculate.measureHeight(decoratorDescriptor.getCalcDescriptor(), decoratorDescriptor);
    }

    @Override
    public void layout(AbstractAGElementDataDesc desc) {
        super.layout(desc);
        AGDatePickerDataDesc descriptor = (AGDatePickerDataDesc) desc;
        DecoratorDescriptor decoratorDescriptor = descriptor.getDecoratorDescriptor();
        AGDecoratorCalculate.layout(decoratorDescriptor.getCalcDescriptor(), decoratorDescriptor, ((AbstractAGViewDataDesc)desc).getCalcDesc());
    }
}
