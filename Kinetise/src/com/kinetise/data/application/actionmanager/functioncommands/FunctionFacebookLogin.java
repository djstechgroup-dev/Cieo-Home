package com.kinetise.data.application.actionmanager.functioncommands;

import com.facebook.Session;
import com.facebook.SessionState;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.alterapimanager.IRequestCallback;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.helpers.facebook.FacebookService;

public class FunctionFacebookLogin extends AbstractLoginFunction implements IRequestCallback, Session.StatusCallback {

    public FunctionFacebookLogin(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        parseAttributes();

        Session session = Session.getActiveSession();
        if (session != null && session.getState().isOpened()) {
            String accessToken = session.getAccessToken();
            onLoginSuccess(accessToken);
        } else {
            FacebookService.getInstance().loginToFacebook(this);
        }
        return null;
    }

    @Override
    public void call(Session session, SessionState state, Exception exception) {
        String accessToken = session.getAccessToken();
        if (state.isOpened() && !accessToken.equals("")) {
            onLoginSuccess(accessToken);
        } else if (state.toString().equals("CLOSED_LOGIN_FAILED")) {
            Session activeSession = Session.getActiveSession();
            if (activeSession != null) {
                Session.getActiveSession().closeAndClearTokenInformation();
                Session.setActiveSession(null);
                SystemDisplay systemDisplay = AGApplicationState.getInstance().getSystemDisplay();
                if (systemDisplay != null)
                    systemDisplay.blockScreenWithLoadingDialog(false);
            }
        }
    }

    protected void parseAttributes() {
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        String actionString = attributes[2].getStringValue();
        actionString = AGXmlActionParser.unescape(actionString);
        mActionDataDesc = AGXmlActionParser.createMultiAction(actionString, mFunctionDataDesc.getContextDataDesc());

        mAlterApiUrl = attributes[1].getStringValue();
        mAlterApiHttpParams = HttpParamsDataDesc.getHttpParams(attributes[3].getStringValue(), mFunctionDataDesc.getContextDataDesc());
        if(attributes.length>=5)
            mAlterApiHeaderParams = HttpParamsDataDesc.getHttpParams(attributes[4].getStringValue(), mFunctionDataDesc.getContextDataDesc());
        mAlterApiHeaderParams = null;
    }
}
