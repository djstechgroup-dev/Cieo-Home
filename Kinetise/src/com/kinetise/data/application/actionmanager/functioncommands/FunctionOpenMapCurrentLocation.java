package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionOpenMapCurrentLocation extends AbstractFunction {

    public FunctionOpenMapCurrentLocation(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public AbstractAGElementDataDesc execute(Object desc) {
        super.execute(desc);
        String startLat = mFunctionDataDesc.getAttributes()[0].getStringValue();
        String startLng = mFunctionDataDesc.getAttributes()[1].getStringValue();
        ActionManager.getInstance().openMapCurrentLocation(startLat, startLng);
        return null;
    }
}
