package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.formdatautils.FormFormater;
import com.kinetise.data.application.formdatautils.FormFormaterV2;
import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.application.screenhistory.ApplicationState;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.sourcemanager.LanguageManager;

public class FunctionSendFormV2 extends AbstractSendFunction {

    public FunctionSendFormV2(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    protected void send() {
        AlterApiManager alterApiManager = mApplication.getAlterApiManager();
        alterApiManager.sendForm(mUrl, mBodyToSend, this, HttpParamsDataDesc.getHttpParams(mHttpParams, mFunctionDataDesc.getContextDataDesc()), HttpParamsDataDesc.getHttpParams(mHeaderParams, mFunctionDataDesc.getContextDataDesc()));
    }

    @Override
    public void onError(PopupMessage... errorMessages) {
        if (errorMessages == null || errorMessages.length == 0) {
            super.onError(new PopupMessage(LanguageManager.getInstance().getString(LanguageManager.POPUP_ERROR_HEADER), LanguageManager.getInstance().getString(LanguageManager.ERROR_SEND_FORM)));
        } else {
            super.onError(errorMessages);
        }
    }

    protected String formatBody() {
        ApplicationState applicationState = AGApplicationState.getInstance().getApplicationState();
        FormFormater formater = new FormFormaterV2();
        return formater.getFormBody(mFormContainer, applicationState.getAlterApiContext(), applicationState.getGuid());
    }
}
