package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.application.externalapplications.OpenGalleryApp;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.MultiActionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.ClearFormValuesVisitor;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.helpers.asynccaller.AsyncCaller;

public abstract class AbstractSendFunction extends AbstractAlterAPIFunction {

    protected MultiActionDataDesc mSuccessAction;
    protected String mHttpParams;
    protected String mHeaderParams;
    protected String mUrl;
    protected String mBodyToSend;
    protected MultiActionDataDesc getControl;
    protected AbstractAGViewDataDesc mFormContainer;

    public AbstractSendFunction(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        SystemDisplay display = mApplication.getSystemDisplay();
        try {
            display.blockScreenWithLoadingDialog(true);
            parseAttributes();
            if (canSend()) {
                mBodyToSend = formatBody();
                send();
            } else {
                PopupManager.showInvalidFormToast(LanguageManager.getInstance().getString(LanguageManager.ERROR_INVALID_FORM));
                finishAction();
            }
        } catch (OutOfMemoryError e) {
            display.blockScreenWithLoadingDialog(false);
            String outOfMemoryMessage = LanguageManager.getInstance().getString(LanguageManager.SEND_MEMORY_ERROR);
            PopupManager.showErrorPopup(outOfMemoryMessage);
        }
        return null;
    }

    protected void clearFormValues() {
        ClearFormValuesVisitor visitor = new ClearFormValuesVisitor();
        if (mFormContainer != null)
            mFormContainer.accept(visitor);
    }

    protected abstract void send();

    protected void parseAttributes() {
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        mUrl = attributes[0].getStringValue();
        String baseUrl = ((AbstractAGViewDataDesc) mFunctionDataDesc.getContextDataDesc()).getFeedBaseAdress();
        mUrl = AssetsManager.resolveURI(mUrl, baseUrl);
        mFormContainer = getFormContainer(attributes[1]);
        String actionString = attributes[2].getStringValue();
        actionString = AGXmlActionParser.unescape(actionString);
        mSuccessAction = AGXmlActionParser.createMultiAction(actionString, mFunctionDataDesc.getContextDataDesc());
        mHttpParams = attributes[3].getStringValue();
        mHeaderParams = attributes[4].getStringValue();
    }

    private AbstractAGViewDataDesc getFormContainer(VariableDataDesc attribute) {
        String getControlAction = attribute.getStringValue().trim();
        getControlAction = AGXmlActionParser.unescape(getControlAction);
        getControl = AGXmlActionParser.createMultiAction(getControlAction, mFunctionDataDesc.getContextDataDesc());
        return (AbstractAGViewDataDesc) ExecuteActionManager.executeMultiAction(getControl);
    }

    protected abstract String formatBody();

    protected boolean canSend() {
        return true;
    }

    @Override
    public void onSuccess(PopupMessage... messages) {
           OpenGalleryApp.removeSavedPhotos();
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clearFormValues();
                if (mSuccessAction != null) {
                    ExecuteActionManager.executeMultiAction(mSuccessAction);
                }
            }
        });
        super.onSuccess(messages);
    }
}
