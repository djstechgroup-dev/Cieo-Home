package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

public class AGCustomControlViewCalculate extends AbstractCalculate {

    private static AGCustomControlViewCalculate mInstance;


    public static AGCustomControlViewCalculate getInstance(){
        if(mInstance == null){
            mInstance = new AGCustomControlViewCalculate();
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
