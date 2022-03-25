package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.*;
import com.kinetise.data.descriptors.datadescriptors.*;

public class CalcManager {

    private static CalcManager mInstance;
    private double mScreenWidth;
    private double mScreenHeight;
    private double mUnitProportion;

    private CalcManager() {
        initCalcManager(0,0);
    }

    public void initCalcManager(double screenWidth, double screenHeight) {
        setScreenWidth(screenWidth);
        setScreenHeight(screenHeight);
        setUnitProportion(Math.min(screenHeight, screenWidth));
    }

    public static CalcManager getInstance() {
        if (mInstance == null) {
            synchronized (CalcManager.class){
                if (mInstance == null) {
                    mInstance = new CalcManager();
                }
            }
        }
        return mInstance;
    }

    public double getScreenHeight() {
        return mScreenHeight;
    }

    public double getScreenWidth() {
        return mScreenWidth;
    }

    public double getUnitProportion() {
        return mUnitProportion;
    }

    private void setScreenWidth(double pScreenWidth) {
        mScreenWidth = pScreenWidth;
    }

    private void setScreenHeight(double pScreenHeight) {
        mScreenHeight = pScreenHeight;
    }

    private void setUnitProportion(double pUnitProportion) {
        mUnitProportion = pUnitProportion;
    }

    public void layout(AbstractAGElementDataDesc dataDesc) {
            ICalculate calc = getCalculate(dataDesc);
            calc.layout(dataDesc);
    }

    public void measureBlockWidth(AbstractAGElementDataDesc dataDesc,
                                  double maxFreeSpaceWidth, double maxSpaceForMax) {
            ICalculate calc = getCalculate(dataDesc);
            calc.measureBlockWidth(dataDesc, maxFreeSpaceWidth, maxSpaceForMax);
    }

    public void measureBlockHeight(AbstractAGElementDataDesc desc,
                                   double maxFreeSpaceHeight, double maxSpaceForMax) {
            ICalculate calc = getCalculate(desc);
            calc.measureBlockHeight(desc, maxFreeSpaceHeight, maxSpaceForMax);
    }

    public static void clearCalculatesInstances() {
        AGBodyCalculate.clearInstance();
        AGCompoundButtonCalculate.clearInstance();
        AGContainerHorizontalCalculate.clearInstance();
        AGContainerThumbnailsCalculate.clearInstance();
        AGContainerVerticalCalculate.clearInstance();
        AGDateCalculate.clearInstance();
        AGGalleryCalculate.clearInstance();
        AGImageCalculate.clearInstance();
        AGMapCalculate.clearInstance();
        AGScreenCalculate.clearInstance();
        AGTextAreaCalculate.clearInstance();
        AGTextCalculate.clearInstance();
        AGTextInputCalculate.clearInstance();
        AGWebBrowserCalculate.clearInstance();
        AGTextImageCalculate.clearInstance();
    }

    public <T extends AbstractAGElementDataDesc> ICalculate getCalculate(T descType) {
        if (descType instanceof AGContainerHorizontalDataDesc || descType instanceof AGDataFeedHorizontalDataDesc || descType instanceof AGRadioGroupHorizontalDataDesc)
            return AGContainerHorizontalCalculate.getInstance();
        if (descType instanceof AGContainerVerticalDataDesc || descType instanceof AGDataFeedVerticalDataDesc || descType instanceof AGRadioGroupVerticalDataDesc)
            return AGContainerVerticalCalculate.getInstance();
        if (descType instanceof AGContainerThumbnailsDataDesc || descType instanceof AGDataFeedThumbnailsDataDesc || descType instanceof AGRadioGroupThumbnailsDataDesc)
            return AGContainerThumbnailsCalculate.getInstance();
        if (descType instanceof AGTextAreaDataDesc)
            return AGTextAreaCalculate.getInstance();
        if (descType instanceof AGDatePickerDataDesc)
            return AGDatePickerCalculate.getInstance();
        if (descType instanceof AGDropdownDataDesc)
            return AGDropdownCalculate.getInstance();
        if (descType instanceof AGTextInputDataDesc)
            return AGTextInputCalculate.getInstance();
        if (descType instanceof AGScreenDataDesc)
            return AGScreenCalculate.getInstance();
        if (descType instanceof AGBodyDataDesc)
            return AGBodyCalculate.getInstance();
        if (descType instanceof AbstractAGSectionDataDesc)
            return AGSectionCalculate.getInstance();
        if (descType instanceof AbstractAGCompoundButtonDataDesc)
            return AGCompoundButtonCalculate.getInstance();
        if (descType instanceof AGGalleryDataDesc)
            return AGGalleryCalculate.getInstance();
        if (descType instanceof AGTextImageDataDesc)
            return AGTextImageCalculate.getInstance();
        if (descType instanceof AGWebBrowserDataDesc)
            return AGWebBrowserCalculate.getInstance();
        if (descType instanceof AGDateDataDesc)
            return AGDateCalculate.getInstance();
        if (descType instanceof AGTextDataDesc)
            return AGTextCalculate.getInstance();
        if (descType instanceof AGMapDataDesc)
            return AGMapCalculate.getInstance();
        if (descType instanceof AGVideoViewDataDesc)
            return AGVideoViewCalculate.getInstance();
        if (descType instanceof AGLoadingDataDesc)
            return AGLoadingCalculate.getInstance();
        if (descType instanceof AGChartDataDesc)
            return AGChartCalculate.getInstance();
        if (descType instanceof AGSignatureDataDesc)
            return AGSignatureCalculate.getInstance();
        if (descType instanceof AGCustomControlDataDesc)
            return AGCustomControlViewCalculate.getInstance();
        
        throw new IllegalArgumentException("Cannot find calculate");
    }

}
