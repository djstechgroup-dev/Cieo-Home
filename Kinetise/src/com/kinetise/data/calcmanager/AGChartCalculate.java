package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

public class AGChartCalculate extends AbstractCalculate {

    protected AGChartCalculate(){}

    private static AGChartCalculate mInstance;

    public static AGChartCalculate getInstance(){
        if(mInstance == null){
            mInstance = new AGChartCalculate();
        }

        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    @Override
    public void measureHeightForMin(AbstractAGElementDataDesc desc, double maxHeight, double maxSpaceForMax) {

    }

    @Override
    public void measureWidthForMin(AbstractAGElementDataDesc desc, double maxWidth, double maxSpaceForMax) {

    }
}

