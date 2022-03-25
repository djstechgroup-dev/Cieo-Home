package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGDropdownDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.DecoratorDescriptor;

public class AGDropdownCalculate extends AGTextImageCalculate{
    private static AGDropdownCalculate mInstance;

    public static AGDropdownCalculate getInstance(){
        if(mInstance == null){
            mInstance = new AGDropdownCalculate();
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    @Override
    public void measureBlockWidth(AbstractAGElementDataDesc dataDesc, double maxWidth, double maxSpaceForMax) {
        super.measureBlockWidth(dataDesc, maxWidth, maxSpaceForMax);
        AGDropdownDataDesc descriptor = (AGDropdownDataDesc) dataDesc;
        DecoratorDescriptor decoratorDescriptor = descriptor.getDecoratorDescriptor();
        AGDecoratorCalculate.measureWidth(decoratorDescriptor.getCalcDescriptor(), decoratorDescriptor);
    }

    @Override
    public void measureBlockHeight(AbstractAGElementDataDesc dataDesc, double maxHeight, double maxSpaceForMax) {
        super.measureBlockHeight(dataDesc, maxHeight, maxSpaceForMax);
        AGDropdownDataDesc descriptor = (AGDropdownDataDesc) dataDesc;
        DecoratorDescriptor decoratorDescriptor = descriptor.getDecoratorDescriptor();
        AGDecoratorCalculate.measureHeight(decoratorDescriptor.getCalcDescriptor(), decoratorDescriptor);
    }

    @Override
    public void layout(AbstractAGElementDataDesc desc) {
        super.layout(desc);
        AGDropdownDataDesc descriptor = (AGDropdownDataDesc) desc;
        DecoratorDescriptor decoratorDescriptor = descriptor.getDecoratorDescriptor();
        AGDecoratorCalculate.layout(decoratorDescriptor.getCalcDescriptor(), decoratorDescriptor, ((AbstractAGViewDataDesc)desc).getCalcDesc());
    }
}
