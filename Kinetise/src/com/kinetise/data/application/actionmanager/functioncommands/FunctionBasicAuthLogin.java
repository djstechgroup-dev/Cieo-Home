package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.MultiActionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.helpers.DataDescHelper;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

import java.util.Locale;

public class FunctionBasicAuthLogin extends AbstractFunction {

    private MultiActionDataDesc mActions;

    public FunctionBasicAuthLogin(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();

        String url = attributes[0].getStringValue();
        String usernameControlId = attributes[1].getStringValue();
        String passwordControlId = attributes[2].getStringValue();
        String actionString = attributes[3].getStringValue();
        String httpParams = attributes[4].getStringValue();
        AbstractAGElementDataDesc contextDataDesc = mFunctionDataDesc.getContextDataDesc();

        String usernameString;
        AGScreenDataDesc screenDesc =  AGApplicationState.getInstance().getCurrentScreenDesc();
        AbstractAGElementDataDesc usernameControl = DataDescHelper.findDescendantById(screenDesc, usernameControlId);
        if (usernameControl != null && usernameControl instanceof IFormControlDesc) {
            usernameString = ((IFormControlDesc) usernameControl).getFormValue().toString();
            if (usernameString == null) {
                usernameString = "";
            }
        } else {
            PopupManager.showErrorPopup(String.format(Locale.US,
                    "Not found control [id:%s] on current screen",
                    usernameControlId));
            return null;
        }


        AbstractAGElementDataDesc passwordControl = DataDescHelper.findDescendantById(screenDesc, passwordControlId);

        String passwordString;
        if (passwordControl != null && passwordControl instanceof IFormControlDesc) {
            passwordString = ((IFormControlDesc) passwordControl).getFormValue().toString();
            if (passwordString == null) {
                passwordString = "";
            }
        } else {
            PopupManager.showErrorPopup(String.format(Locale.US,
                    "Not found control [id:%s] on current screen",
                    passwordControlId));
            return null;
        }

        actionString = AGXmlActionParser.unescape(actionString);
        mActions = AGXmlActionParser.createMultiAction(actionString, contextDataDesc);
        ActionManager.getInstance(). basicAuthLogin(url, usernameString, passwordString, this::executeMultiAction, HttpParamsDataDesc.getHttpParams(httpParams, null));
        return null;
    }

    private void executeMultiAction(PopupMessage... messages) {
        ExecuteActionManager.executeMultiAction(mActions);
        if (messages != null) {
            for (PopupMessage message : messages)
                PopupManager.showPopup(message.getDescription(), message.getTitle());
        }
    }
}
