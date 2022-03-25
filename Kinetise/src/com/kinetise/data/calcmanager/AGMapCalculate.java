package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

/**
 * Class is on Modules Map but not in js app version
 * So lets leave it clear for a while
 */
public class AGMapCalculate extends AbstractCalculate {

    static AGMapCalculate mInstance;

    @Override
    public void measureHeightForMin(AbstractAGElementDataDesc desc,
                                    double maxHeight, double maxSpaceForMax) {
        // Abstract method implementation, nothing to do
    }

    @Override
    public void measureWidthForMin(AbstractAGElementDataDesc desc,
                                   double maxWidth, double maxSpaceForMax) {

    }

    public static AGMapCalculate getInstance() {
        if (mInstance == null) {
            mInstance = new AGMapCalculate();
        }

        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    @Override
    public void layout(AbstractAGElementDataDesc desc) {
        // Abstract method implementation, nothing to do

    }

}
