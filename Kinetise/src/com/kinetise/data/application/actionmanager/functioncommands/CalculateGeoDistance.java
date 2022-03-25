package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.functions.CalculateGeoDistanceDesc;

import java.text.NumberFormat;
import java.util.Locale;

public class CalculateGeoDistance extends AbstractFunction {
    public CalculateGeoDistance(CalculateGeoDistanceDesc calculateGeoDistanceDesc, AGApplicationState instance) {
        super(calculateGeoDistanceDesc, instance);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String fromLat = mFunctionDataDesc.getAttributes()[0].getStringValue();
        String fromLng = mFunctionDataDesc.getAttributes()[1].getStringValue();
        String toLat = mFunctionDataDesc.getAttributes()[2].getStringValue();
        String toLng = mFunctionDataDesc.getAttributes()[3].getStringValue();
        String unit = mFunctionDataDesc.getAttributes()[4].getStringValue();

        double doubleFromLat = Double.parseDouble(fromLat);
        double doubleFromLng = Double.parseDouble(fromLng);
        double doubleToLat = Double.parseDouble(toLat);
        double doubleToLng = Double.parseDouble(toLng);

        double distance = ActionManager.getInstance().calculateGeoDistance(doubleFromLat, doubleFromLng, doubleToLat, doubleToLng, unit);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat.format(distance);
    }
}
