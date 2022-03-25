package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.alterapimanager.IRequestCallback;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;

public abstract class AbstractAlterAPIFunction extends AbstractFunction implements IRequestCallback {

    public AbstractAlterAPIFunction(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public void onError(PopupMessage... errorMessages) {
        onFinish(errorMessages);
    }

    @Override
    public void onSuccess(PopupMessage... messages) {
        onFinish(messages);
    }

    private void onFinish(PopupMessage[] messages) {
        if (messages != null)
            for (PopupMessage message : messages)
                PopupManager.showPopup(message.getDescription(), message.getTitle());
        finishAction();
    }

    public void finishAction() {
        SystemDisplay systemDisplay = mApplication.getSystemDisplay();
        if (systemDisplay != null)
            systemDisplay.blockScreenWithLoadingDialog(false);
    }
}
