package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;

import java.util.HashMap;

public class FunctionStartGPSTracking extends AbstractFunction {
    public FunctionStartGPSTracking(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);

        String mUrl;
        HashMap<String, String> mHttpParams;
        HashMap<String, String> mHeaderParams;
        long mMinTime;
        int mMinDistance;

        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        mUrl = attributes[0].getStringValue();
        mHttpParams = HttpParamsDataDesc.getHttpParams(attributes[1].getStringValue(), mFunctionDataDesc.getContextDataDesc()).getHttpParamsAsHashMap();
        mHeaderParams = HttpParamsDataDesc.getHttpParams(attributes[2].getStringValue(), mFunctionDataDesc.getContextDataDesc()).getHttpParamsAsHashMap();
        mMinTime = Long.parseLong(attributes[3].getStringValue());
        mMinDistance = Integer.parseInt(attributes[4].getStringValue());

        ActionManager.getInstance().startGPSTracking(mUrl, mHttpParams, mHeaderParams, mMinTime, mMinDistance);
        return null;
    }
}
