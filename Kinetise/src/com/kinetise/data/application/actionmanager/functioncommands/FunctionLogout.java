package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.helpers.asynccaller.AsyncCaller;

public class FunctionLogout extends AbstractAlterAPIFunction {

    String mPostLogoutScreenId;

    public FunctionLogout(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    /**
     * Performs alterApi logout on given WebService
     *
     * @param desc Descriptor on which action should be called
     * @return null
     */
    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        AlterApiManager alterApiManager = AGApplicationState.getInstance().getAlterApiManager();
        SystemDisplay display = AGApplicationState.getInstance().getSystemDisplay();

        display.blockScreenWithLoadingDialog(true);

        //attributes
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        String url = attributes[0].getStringValue();
        String httpParams = attributes[1].getStringValue();
        if (attributes.length > 2) {
            mPostLogoutScreenId = attributes[2].getStringValue();
        }
        alterApiManager.logout(url, this, HttpParamsDataDesc.getHttpParams(httpParams, null));

        return null;
    }

    /**
     * After action is executed check if should redirect to next screen and after clears screens history.
     * Also show popups with information about logout
     */
    @Override
    public void onSuccess(PopupMessage... messages) {
        logoutUserOnUIThread();
        super.onSuccess(messages);
    }


    @Override
    public void onError(PopupMessage... errorMessages) {
        logoutUserOnUIThread();
        finishAction();
    }

    private void logoutUserOnUIThread() {
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AGApplicationState.getInstance().logoutUser(mPostLogoutScreenId);
            }
        });
    }
}
