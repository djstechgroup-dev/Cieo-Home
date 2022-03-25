package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.formdatautils.FormFormaterForEmail;
import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.application.screenhistory.ApplicationState;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.ValidateFormVisitor;
import com.kinetise.data.sourcemanager.LanguageManager;

public class FunctionSendEmail extends AbstractSendFunction {
    public static final String ALTER_API_EMAIL_HOST = "ALTER_API_EMAIL_HOST";
    public static String mEmail;

    public FunctionSendEmail(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
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
    protected void send() {
        AlterApiManager alterApiManager = AGApplicationState.getInstance().getAlterApiManager();
        HttpParamsDataDesc params = HttpParamsDataDesc.getHttpParams(mHttpParams, null);
        params.addHttpParam("_email", mEmail);
        alterApiManager.sendForm(mUrl, mBodyToSend, this, params, HttpParamsDataDesc.getHttpParams(mHeaderParams, null));
    }

    @Override
    protected String formatBody() {
        ApplicationState applicationState = AGApplicationState.getInstance().getApplicationState();
        FormFormaterForEmail formater = new FormFormaterForEmail();
        return formater.getFormBody(mFormContainer, applicationState.getAlterApiContext(), applicationState.getGuid());
    }

    @Override
    public void onError(PopupMessage... errorMessages) {
        if (errorMessages == null || errorMessages.length == 0) {
            super.onError(new PopupMessage(LanguageManager.getInstance().getString(LanguageManager.POPUP_ERROR_HEADER), LanguageManager.getInstance().getString(LanguageManager.ERROR_SEND_EMAIL)));
        } else {
            super.onError(errorMessages);
        }
    }

    @Override
    protected void parseAttributes() {
        super.parseAttributes();
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        mEmail = attributes[0].getStringValue();
        mUrl = getEmailHost();
    }

    public String getEmailHost() {
        return LanguageManager.getInstance().getString(ALTER_API_EMAIL_HOST);
    }
}
