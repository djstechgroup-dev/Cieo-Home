package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

public class AGLoadingCalculate extends AbstractCalculate {
    private static AGLoadingCalculate mInstance;

    private AGLoadingCalculate(){}

    public static AGLoadingCalculate getInstance(){
        if(mInstance == null){
            mInstance = new AGLoadingCalculate();
        }

        return mInstance;
    }

    @Override
    public void measureHeightForMin(AbstractAGElementDataDesc desc, double maxHeight, double maxSpaceForMax) {

    }

    @Override
    public void measureWidthForMin(AbstractAGElementDataDesc desc, double maxWidth, double maxSpaceForMax) {

    }

    @Override
    public void layout(AbstractAGElementDataDesc desc) {

    }
}
