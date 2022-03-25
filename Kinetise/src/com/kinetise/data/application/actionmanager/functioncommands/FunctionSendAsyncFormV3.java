package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.application.alterapimanager.AGOkHttpConfigurator;
import com.kinetise.data.application.formdatautils.FormFormaterV3;
import com.kinetise.data.application.screenhistory.ApplicationState;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.MultiActionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.data.sourcemanager.propertymanager.Synchronizer;
import com.kinetise.helpers.jq.JQBridge;

import java.util.HashMap;

public class FunctionSendAsyncFormV3 extends AbstractFunction {

    public FunctionSendAsyncFormV3(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String mUrl;
        String mHttpParams;
        String mHeaderParams;
        String mBodyParams;
        AGHttpMethodType mHttpMethod;
        String mRequestTransform;
        String mResponseTransform;

        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        attributes[0].resolveVariable();
        mUrl = attributes[0].getStringValue();
        attributes[1].resolveVariable();
        String actionString = attributes[1].getStringValue();
        attributes[2].resolveVariable();
        mHttpParams = attributes[2].getStringValue();
        mHeaderParams = attributes[3].getStringValue();
        mBodyParams = attributes[4].getStringValue();
        mHttpMethod = AGXmlParserHelper.getHttpMethodType(attributes[5].getStringValue());
        mRequestTransform = attributes[6].getStringValue();
        mResponseTransform = attributes[7].getStringValue();
        AbstractAGElementDataDesc contextDataDesc = mFunctionDataDesc.getContextDataDesc();
       sendAsyncFormV3(mUrl, mHttpParams, mHeaderParams, mBodyParams, mHttpMethod, mRequestTransform, mResponseTransform, actionString, contextDataDesc);
        return null;
    }

    public void sendAsyncFormV3(String mUrl, String mHttpParams, String mHeaderParams, String mBodyParams, AGHttpMethodType mHttpMethod,
                                String mRequestTransform, String mResponseTransform, String actionString, AbstractAGElementDataDesc contextDataDesc) {
        IFormControlDesc mSentDescriptor;
        String actionStringUnescaped = AGXmlActionParser.unescape(actionString);
        MultiActionDataDesc action = AGXmlActionParser.createMultiAction(actionStringUnescaped, contextDataDesc);
        action.resolveVariablesInParameters();
        mSentDescriptor = (IFormControlDesc) ExecuteActionManager.executeMultiAction(action);

        AbstractAGViewDataDesc sentViewDesc = (AbstractAGViewDataDesc) mSentDescriptor;
        if (sentViewDesc == null)
            return;

        String formId = ((IFormControlDesc) sentViewDesc).getFormId();
        String value;
        String url = mUrl;
        url = AssetsManager.addHttpQueryParams(url, HttpParamsDataDesc.getHttpParams(mHttpParams, contextDataDesc));
        value = mSentDescriptor.getFormValue().toString();

        ApplicationState applicationState = AGApplicationState.getInstance().getApplicationState();
        FormFormaterV3 formater = new FormFormaterV3();
        HttpParamsDataDesc bodyParams = HttpParamsDataDesc.getHttpParams(mBodyParams, contextDataDesc);
        String bodyBeforTransform = formater.getFormBody((AbstractAGViewDataDesc) mSentDescriptor, applicationState.getAlterApiContext(), applicationState.getGuid(), bodyParams.getHttpParamsAsHashMap());

        String postParams = JQBridge.runTransform(mRequestTransform, bodyBeforTransform, AGApplicationState.getInstance().getContext());

        HashMap<String, String> headers = new HashMap<>();
        if (mHeaderParams != null) {
            headers = HttpParamsDataDesc.getHttpParams(mHeaderParams, contextDataDesc).getHttpParamsAsHashMap();
        } else {
            headers.put(AGOkHttpConfigurator.CONTENT_TYPE_HEADER_NAME, AGOkHttpConfigurator.CONTENT_TYPE_JSON);
        }

        Synchronizer.getInstance().sendRequest(formId, value, mHttpMethod, url, headers, postParams, mResponseTransform);
    }


}
