package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

public class AGVideoViewCalculate extends AbstractCalculate {

    private static AGVideoViewCalculate mInstance;


    public static AGVideoViewCalculate getInstance(){
        if(mInstance == null){
            mInstance = new AGVideoViewCalculate();
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

    @Override
    public void layout(AbstractAGElementDataDesc desc) {

    }
}
