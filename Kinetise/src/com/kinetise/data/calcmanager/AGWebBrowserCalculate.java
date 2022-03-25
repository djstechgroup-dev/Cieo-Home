package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

public class AGWebBrowserCalculate extends AbstractCalculate {

    static AGWebBrowserCalculate mInstance;

    @Override
    public void measureHeightForMin(AbstractAGElementDataDesc desc,
                                    double maxHeight, double maxSpaceForMax) {
        // nothing to do, abstract method implementation

    }

    @Override
    public void measureWidthForMin(AbstractAGElementDataDesc desc,
                                   double maxWidth, double maxSpaceForMax) {
        // nothing to do, abstract method implementation

    }

    public static AGWebBrowserCalculate getInstance() {
        if (mInstance == null) {
            mInstance = new AGWebBrowserCalculate();
        }

        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    @Override
    public void layout(AbstractAGElementDataDesc desc) {
        // nothing to do, abstract method implementation

    }


}
