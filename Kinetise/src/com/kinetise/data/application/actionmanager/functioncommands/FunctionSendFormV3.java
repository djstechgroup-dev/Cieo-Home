package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.application.formdatautils.FormFormaterV3;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.application.screenhistory.ApplicationState;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.actions.functions.FunctionSendFormV3DataDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.ValidateFormVisitor;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.helpers.jq.JQBridge;

public class FunctionSendFormV3 extends AbstractSendFunction {
    private String mBodyParams;
    private AGHttpMethodType mHttpMethod;
    private String mRequestTransform;
    private String mResponseTransform;

    public FunctionSendFormV3(FunctionSendFormV3DataDesc functionSendFormV3DataDesc, AGApplicationState instance) {
        super(functionSendFormV3DataDesc, instance);
    }

    @Override
    protected void parseAttributes() {
        super.parseAttributes();
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        mBodyParams = attributes[5].getStringValue();
        mHttpMethod = AGXmlParserHelper.getHttpMethodType(attributes[6].getStringValue());
        mRequestTransform = attributes[7].getStringValue();
        mResponseTransform = attributes[8].getStringValue();
    }

    @Override
    protected boolean canSend() {
        ValidateFormVisitor visitor = new ValidateFormVisitor();
        if (mFormContainer != null) {
            mFormContainer.accept(visitor);
        }
        return visitor.isFormValid();
    }

    @Override
    protected String formatBody() {
        ApplicationState applicationState = AGApplicationState.getInstance().getApplicationState();
        FormFormaterV3 formater = new FormFormaterV3();
        HttpParamsDataDesc bodyParams = HttpParamsDataDesc.getHttpParams(mBodyParams, mFunctionDataDesc.getContextDataDesc());
        String bodyBeforTransform = formater.getFormBody(mFormContainer, applicationState.getAlterApiContext(), applicationState.getGuid(), bodyParams.getHttpParamsAsHashMap());
        return JQBridge.runTransform(mRequestTransform, bodyBeforTransform, AGApplicationState.getInstance().getContext());
    }

    @Override
    protected void send() {
        AlterApiManager alterApiManager = mApplication.getAlterApiManager();
        alterApiManager.sendForm(mHttpMethod, mUrl, mBodyToSend, this, HttpParamsDataDesc.getHttpParams(mHttpParams, mFunctionDataDesc.getContextDataDesc()), HttpParamsDataDesc.getHttpParams(mHeaderParams, mFunctionDataDesc.getContextDataDesc()), mResponseTransform);
    }

    @Override
    public void onError(PopupMessage... errorMessages) {
        if (errorMessages == null || errorMessages.length == 0) {
            super.onError(new PopupMessage(LanguageManager.getInstance().getString(LanguageManager.POPUP_ERROR_HEADER), LanguageManager.getInstance().getString(LanguageManager.ERROR_SEND_FORM)));
        } else {
            super.onError(errorMessages);
        }
    }
}
