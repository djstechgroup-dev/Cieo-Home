package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.application.formdatautils.FormFormater;
import com.kinetise.data.application.formdatautils.FormFormaterV2;
import com.kinetise.data.application.alterapimanager.AGOkHttpConfigurator;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.MultiActionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGToggleButtonDataDesc;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.data.sourcemanager.propertymanager.Synchronizer;

import java.util.HashMap;

public class FunctionSendAsyncForm extends AbstractFunction {

    public FunctionSendAsyncForm(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    private String mUrl;
    private IFormControlDesc mSentDescriptor;
    private String mHttpParams;
    protected String mHeaderParams;

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        parseAttributes();

        if(!(mSentDescriptor instanceof AGToggleButtonDataDesc)){
            return null;
        }

        AGToggleButtonDataDesc sentViewDesc = (AGToggleButtonDataDesc) mSentDescriptor;
        if (sentViewDesc==null)
            return null;
        String formId = sentViewDesc.getFormId();

        String value;
        String url = mUrl;
        url = AssetsManager.addHttpQueryParams(url, HttpParamsDataDesc.getHttpParams(mHttpParams, mFunctionDataDesc.getContextDataDesc()));

        value = mSentDescriptor.getFormValue().toString();
        FormFormater formater = new FormFormaterV2();
        String postParams = formater.getFormBody(sentViewDesc);

        HashMap<String, String> headers = new HashMap<String, String>();
        if (mHeaderParams!=null) {
            headers = HttpParamsDataDesc.getHttpParams(mHeaderParams,mFunctionDataDesc.getContextDataDesc()).getHttpParamsAsHashMap();
        } else {
            headers.put(AGOkHttpConfigurator.CONTENT_TYPE_HEADER_NAME, AGOkHttpConfigurator.CONTENT_TYPE_JSON);
        }

        Synchronizer.getInstance().sendRequest(formId, value, AlterApiManager.DEFAULT_METHOD, url, headers, postParams, null);
        return null;
    }


    private void parseAttributes() {
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        attributes[0].resolveVariable();
        mUrl = attributes[0].getStringValue();
        attributes[1].resolveVariable();
        String actionString = attributes[1].getStringValue();
        actionString = AGXmlActionParser.unescape(actionString);
        MultiActionDataDesc action = AGXmlActionParser.createMultiAction(actionString, mFunctionDataDesc.getContextDataDesc());
        action.resolveVariablesInParameters();
        mSentDescriptor = (IFormControlDesc) ExecuteActionManager.executeMultiAction(action);
        attributes[2].resolveVariable();
        mHttpParams = attributes[2].getStringValue();
        mHeaderParams = attributes[3].getStringValue();
    }

}

