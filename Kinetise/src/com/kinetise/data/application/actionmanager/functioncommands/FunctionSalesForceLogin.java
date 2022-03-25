package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.alterapimanager.IRequestCallback;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.helpers.SalesForceHelper;

import java.util.HashMap;

public class FunctionSalesForceLogin extends AbstractLoginFunction implements IRequestCallback {

    public static final String PARAM_REDIRECT_URI = "redirect_uri";
    private String mUrl;
    private HashMap<String, String> mHttpParams;
    private HashMap<String, String> mHeaderParams;
    private String mRedirectUrl;

    public FunctionSalesForceLogin(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public void onLoginSuccess(String accessToken) {
        SalesForceHelper.setToken(accessToken);
        super.onSuccess();
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        parseAttributes();

        SalesForceHelper.loginToSalesForce(AGApplicationState.getInstance().getActivity(), mUrl, mHttpParams, mHeaderParams, mRedirectUrl, FunctionSalesForceLogin.this);
        return null;
    }

    protected void parseAttributes() {
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        mUrl = attributes[0].getStringValue();
        mHttpParams = HttpParamsDataDesc.getHttpParams(attributes[1].getStringValue(), mFunctionDataDesc.getContextDataDesc()).getHttpParamsAsHashMap();
        mHeaderParams = HttpParamsDataDesc.getHttpParams(attributes[2].getStringValue(), mFunctionDataDesc.getContextDataDesc()).getHttpParamsAsHashMap();
        mRedirectUrl = mHttpParams.get(PARAM_REDIRECT_URI);

        String actionString = attributes[3].getStringValue();
        actionString = AGXmlActionParser.unescape(actionString);
        mActionDataDesc = AGXmlActionParser.createMultiAction(actionString, mFunctionDataDesc.getContextDataDesc());
        mAlterApiUrl = attributes[4].getStringValue();
        mAlterApiHttpParams = HttpParamsDataDesc.getHttpParams(attributes[5].getStringValue(), mFunctionDataDesc.getContextDataDesc());
        mAlterApiHeaderParams = HttpParamsDataDesc.getHttpParams(attributes[6].getStringValue(), mFunctionDataDesc.getContextDataDesc());
    }
}
