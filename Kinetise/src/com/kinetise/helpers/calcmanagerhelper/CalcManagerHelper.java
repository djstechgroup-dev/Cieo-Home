package com.kinetise.helpers.calcmanagerhelper;

import com.kinetise.data.calcmanager.CalcManager;

public abstract class CalcManagerHelper {

    public final static double UNITSCALE = 1000;


    public final static double KPXtoPixels(double kpxValue) {
        return ((kpxValue * CalcManager.getInstance().getUnitProportion()) / CalcManagerHelper.UNITSCALE);
    }

    public final static double pixelsToKPX(double px) {
        return ((px * CalcManagerHelper.UNITSCALE) / CalcManager.getInstance().getUnitProportion());
    }

}
