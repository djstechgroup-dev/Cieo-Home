package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.IAGApplicationStateListener;
import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.helpers.RWrapper;
import com.kinetise.helpers.youtube.GoogleService;

public class FunctionGoogleLogin extends AbstractLoginFunction implements IAGApplicationStateListener {

    public FunctionGoogleLogin(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        parseAttributes();

        String clientId = AGApplicationState.getInstance().getContext().getString(RWrapper.string.google_sign_in_client_id);

        if (clientId != null && clientId.length() > 0) {
            GoogleService.getInstance().login(this);
        } else {
            onFailed();
        }

        return null;
    }

    protected void parseAttributes() {
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        mAlterApiUrl = attributes[0].getStringValue();

        mAlterApiHttpParams = HttpParamsDataDesc.getHttpParams(attributes[1].getStringValue(), mFunctionDataDesc.getContextDataDesc());
        mAlterApiHeaderParams = HttpParamsDataDesc.getHttpParams(attributes[2].getStringValue(), mFunctionDataDesc.getContextDataDesc());
        mAlterApiBodyParams = HttpParamsDataDesc.getHttpParams(attributes[3].getStringValue(), mFunctionDataDesc.getContextDataDesc());

        mHttpMethod = AGXmlParserHelper.getHttpMethodType(attributes[4].getStringValue());
        mContentType = attributes[5].getStringValue();

        mRequestTransform = attributes[6].getStringValue();
        mResponseTransform = attributes[7].getStringValue();

        String actionString = attributes[8].getStringValue();
        actionString = AGXmlActionParser.unescape(actionString);
        mActionDataDesc = AGXmlActionParser.createMultiAction(actionString, mFunctionDataDesc.getContextDataDesc());
    }

    @Override
    public void onLoginSuccess(String accessToken) {
        if (mAlterApiUrl != null && !mAlterApiUrl.equals("")) {
            loginToAlterApi(accessToken);
        } else {
            AlterApiManager.getAlterApiSesionID();
            onSuccess();
            finishAction();
        }
    }

    @Override
    public void onFailed() {
        super.onFailed();
        if (AGApplicationState.getInstance().isPaused()) {
            AGApplicationState.getInstance().addStateListener(this);
        } else {
            showLoginErrorPopup();
        }
    }

    private void showLoginErrorPopup() {
        PopupManager.showErrorPopup(LanguageManager.getInstance().getString(LanguageManager.ERROR_LOGIN));
    }

    @Override
    public void onResume() {
        showLoginErrorPopup();
        AGApplicationState.getInstance().removeStateListener(this);
    }

    @Override
    public void onPause() {

    }
}