package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.application.alterapimanager.AGOkHttpConfigurator;
import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.application.formdatautils.FormFormaterUrlEncoded;
import com.kinetise.data.application.formdatautils.FormFormaterV3;
import com.kinetise.data.application.screenhistory.ApplicationState;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.data.descriptors.types.EncryptionType;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.MultiActionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.helpers.jq.JQBridge;

import java.util.HashMap;

public class FunctionLogin extends AbstractSendFunction {

    public static final String JSON_CONTENT_TYPE = "application/json";
    private static HashMap<String, EncryptionType> sHashMethodTypes = new HashMap<String, EncryptionType>();
    private String mBodyParams;
    private AGHttpMethodType mHttpMethod;
    private String mContentType;
    private String mRequestTransform;
    private String mResponseTransform;

    static {
        sHashMethodTypes.put("no", EncryptionType.NONE);
        sHashMethodTypes.put("md5", EncryptionType.MD5);
        sHashMethodTypes.put("sha1", EncryptionType.SHA1);
        sHashMethodTypes.put("none", EncryptionType.NONE);
    }

    public FunctionLogin(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    protected void send() {
        AlterApiManager alterApiManager = mApplication.getAlterApiManager();
        HttpParamsDataDesc headerParams = HttpParamsDataDesc.getHttpParams(mHeaderParams, null);
        setContentType(headerParams);
        alterApiManager.login(mHttpMethod, mUrl, mBodyToSend, HttpParamsDataDesc.getHttpParams(mHttpParams, null), headerParams, mResponseTransform, this);
    }

    private void setContentType(HttpParamsDataDesc headerParams) {
        headerParams.addHttpParam(AGOkHttpConfigurator.CONTENT_TYPE_HEADER_NAME, mContentType);;
    }

    public void parseAttributes(){
        //We don't call super and override whole logic, because we are only changing parameters order in this one function and the rest will be changed later
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        String getControlAction = attributes[0].getStringValue().trim();
        getControlAction = AGXmlActionParser.unescape(getControlAction);
        MultiActionDataDesc getControl = AGXmlActionParser.createMultiAction(getControlAction, mFunctionDataDesc.getContextDataDesc());
        mFormContainer = (AbstractAGViewDataDesc) ExecuteActionManager.executeMultiAction(getControl);

        mUrl = attributes[1].getStringValue();
        mHttpParams = attributes[2].getStringValue();
        mHeaderParams = attributes[3].getStringValue();
        mBodyParams = attributes[4].getStringValue();
        mHttpMethod = AGXmlParserHelper.getHttpMethodType(attributes[5].getStringValue());
        mContentType = attributes[6].getStringValue();
        mRequestTransform = attributes[7].getStringValue();
        mResponseTransform = attributes[8].getStringValue();

        String actionString = attributes[9].getStringValue();
        actionString = AGXmlActionParser.unescape(actionString);
        mSuccessAction = AGXmlActionParser.createMultiAction(actionString, mFunctionDataDesc.getContextDataDesc());
    }

    @Override
    protected String formatBody() {
        String bodyBeforTransform;
        HttpParamsDataDesc bodyParams = HttpParamsDataDesc.getHttpParams(mBodyParams, mFunctionDataDesc.getContextDataDesc());

        if(mContentType.contains(JSON_CONTENT_TYPE)) {
            ApplicationState applicationState = AGApplicationState.getInstance().getApplicationState();
            FormFormaterV3 formater = new FormFormaterV3();
            bodyBeforTransform = formater.getFormBody(mFormContainer, applicationState.getAlterApiContext(), applicationState.getGuid(), bodyParams.getHttpParamsAsHashMap());
        }
        else {
            FormFormaterUrlEncoded formater = new FormFormaterUrlEncoded();
            bodyBeforTransform = formater.getFormBody(mFormContainer, bodyParams.getHttpParamsAsHashMap());
        }
        return JQBridge.runTransform(mRequestTransform, bodyBeforTransform, AGApplicationState.getInstance().getContext());
    }

    @Override
    public void onError(PopupMessage... errorMessages) {
        if (errorMessages == null || errorMessages.length == 0) {
            super.onError(new PopupMessage(LanguageManager.getInstance().getString(LanguageManager.POPUP_ERROR_HEADER), LanguageManager.getInstance().getString(LanguageManager.ERROR_LOGIN)));
        } else {
            super.onError(errorMessages);
        }
    }
}
