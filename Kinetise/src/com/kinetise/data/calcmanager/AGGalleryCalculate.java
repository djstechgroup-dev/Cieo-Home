package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGGalleryDataDesc;

public class AGGalleryCalculate extends AbstractCalculate {

    private static AGGalleryCalculate mInstance;

    public static AGGalleryCalculate getInstance() {
        if (mInstance == null) {
            mInstance = new AGGalleryCalculate();
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

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

    public void layout(AbstractAGElementDataDesc dataDesc) {
        for (AbstractAGElementDataDesc agCollection : ((AGGalleryDataDesc) dataDesc).getFeedClientControls()) {
            CalcManager.getInstance().layout(agCollection);
        }
    }

    public void measureBlockWidth(AbstractAGElementDataDesc dataDesc,
                                  double maxWidth, double maxSpaceForMax) {
        super.measureBlockWidth(dataDesc, maxWidth, maxSpaceForMax);
        AGViewCalcDesc calcDesc = (AGViewCalcDesc) dataDesc.getCalcDesc();

        for (AbstractAGElementDataDesc desc : ((AGGalleryDataDesc) dataDesc).getFeedClientControls()) {
            CalcManager.getInstance().measureBlockWidth(desc, calcDesc.getContentSpaceWidth(), calcDesc.getContentSpaceWidth());
        }
    }

    public void measureBlockHeight(AbstractAGElementDataDesc dataDesc,
                                   double maxHeight, double maxSpaceForMax) {
        super.measureBlockHeight(dataDesc, maxHeight, maxSpaceForMax);
        AGViewCalcDesc calcDesc = (AGViewCalcDesc) dataDesc.getCalcDesc();

        for (AbstractAGElementDataDesc desc : ((AGGalleryDataDesc) dataDesc).getFeedClientControls()) {
            CalcManager.getInstance().measureBlockHeight(desc, calcDesc.getContentSpaceHeight(), calcDesc.getContentSpaceHeight());
        }
    }



}
