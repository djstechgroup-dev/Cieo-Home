package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.*;
import com.kinetise.data.descriptors.calcdescriptors.AGElementCalcDesc;

public class AGScreenCalculate implements ICalculate {

    private static AGScreenCalculate mInstance;

    public static AGScreenCalculate getInstance() {
        if (mInstance == null) {
            mInstance = new AGScreenCalculate();
        }

        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }


    @Override
    public void layout(AbstractAGElementDataDesc desc) {

        CalcManager calcManager = CalcManager.getInstance();
        AbstractAGElementDataDesc body = ((AGScreenDataDesc) desc).getScreenBody();
        AbstractAGElementDataDesc header = ((AGScreenDataDesc) desc).getScreenHeader();
        AbstractAGElementDataDesc navipanel = ((AGScreenDataDesc) desc).getScreenNaviPanel();

        if (header != null) {
            calcManager.layout(header);
            header.getCalcDesc().setPositionX(0);
            header.getCalcDesc().setPositionY(0);
        }

        if (navipanel != null) {
            calcManager.layout(navipanel);
            double naviPanelYPosition = CalcManager.getInstance().getScreenHeight() - navipanel.getCalcDesc().getHeight();
            navipanel.getCalcDesc().setPositionY(naviPanelYPosition);
            navipanel.getCalcDesc().setPositionX(0);
        }

        if (body != null) {
            calcManager.layout(body);
            body.getCalcDesc().setPositionY(0);
            body.getCalcDesc().setPositionX(0);
        }
    }

    @Override
    public void measureBlockHeight(AbstractAGElementDataDesc desc,
                                   double maxFreeSpaceHeight, double maxSpaceForMax) {

        CalcManager calcManager = CalcManager.getInstance();
        AGBodyDataDesc body = ((AGScreenDataDesc) desc).getScreenBody();
        AGHeaderDataDesc header = ((AGScreenDataDesc) desc).getScreenHeader();
        AGNaviPanelDataDesc naviPanel = ((AGScreenDataDesc) desc).getScreenNaviPanel();

        desc.getCalcDesc().setHeight(maxFreeSpaceHeight);

        double headerHeight = 0;
        if (header != null) {
            calcManager.measureBlockHeight(header, maxFreeSpaceHeight, maxSpaceForMax);
            headerHeight = header.getCalcDesc().getHeight();
        }

        double naviPanelHeight = 0;
        if (naviPanel != null) {
            calcManager.measureBlockHeight(naviPanel, maxFreeSpaceHeight, maxSpaceForMax);
            naviPanelHeight = naviPanel.getCalcDesc().getHeight();
        }

        if (body != null) {
            AGBodyCalculate bodyCalc = (AGBodyCalculate) calcManager.getCalculate(body);
            bodyCalc.setHeaderHeight(headerHeight);
            bodyCalc.setNaviPanelHeight(naviPanelHeight);
            bodyCalc.measureBlockHeight(body, maxFreeSpaceHeight - headerHeight - naviPanelHeight, maxFreeSpaceHeight - headerHeight - naviPanelHeight);
        }
    }

    @Override
    public void measureBlockWidth(AbstractAGElementDataDesc desc,
                                  double maxFreeSpaceWidth, double maxSpaceForMax) {

        CalcManager calcManager = CalcManager.getInstance();
        AbstractAGElementDataDesc body = ((AGScreenDataDesc) desc).getScreenBody();
        AbstractAGElementDataDesc header = ((AGScreenDataDesc) desc).getScreenHeader();
        AbstractAGElementDataDesc naviPanel = ((AGScreenDataDesc) desc).getScreenNaviPanel();

        AGElementCalcDesc calcDesc = desc.getCalcDesc();
        calcDesc.setWidth(maxFreeSpaceWidth);

        if (header != null) {
            calcManager.measureBlockWidth(header, maxFreeSpaceWidth, maxSpaceForMax);
        }
        if (naviPanel != null) {
            calcManager.measureBlockWidth(naviPanel, maxFreeSpaceWidth, maxSpaceForMax);
        }
        if (body != null) {
            calcManager.measureBlockWidth(body, maxFreeSpaceWidth, maxSpaceForMax);
        }
    }
}
