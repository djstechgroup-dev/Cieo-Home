package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

public class AGSignatureCalculate extends AbstractCalculate {

    protected AGSignatureCalculate(){}

    private static AGSignatureCalculate mInstance;

    public static AGSignatureCalculate getInstance(){
        if(mInstance == null){
            mInstance = new AGSignatureCalculate();
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

