package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

public class FunctionShowAlert extends AbstractFunction {


    public FunctionShowAlert(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String mTitle;
        String mOkButtonLabel;
        String mMessage;
        String okActionString;
        String mCancelButtonLabel;
        String cancelActionString;

        mTitle = mFunctionDataDesc.getAttributes()[0].getStringValue();
        mMessage = mFunctionDataDesc.getAttributes()[1].getStringValue();
        mOkButtonLabel = mFunctionDataDesc.getAttributes()[2].getStringValue();
        okActionString = mFunctionDataDesc.getAttributes()[3].getStringValue();
        mCancelButtonLabel = mFunctionDataDesc.getAttributes()[4].getStringValue();
        cancelActionString = mFunctionDataDesc.getAttributes()[5].getStringValue();

        okActionString = AGXmlActionParser.unescape(okActionString);

        VariableDataDesc mOkAction;
        VariableDataDesc mCancelAction;
        mOkAction = AGXmlActionParser.getVariableForEscapedActionString(okActionString);

        cancelActionString = AGXmlActionParser.unescape(cancelActionString);
        mCancelAction = AGXmlActionParser.getVariableForEscapedActionString(cancelActionString);

        Runnable okRunnable = createActionRunnable(mOkAction);
        Runnable cancelRunnable = createActionRunnable(mCancelAction);

        ActionManager.getInstance().showAlert(mTitle, mOkButtonLabel, mMessage, okRunnable, mCancelButtonLabel, cancelRunnable);
        return null;
    }

    private Runnable createActionRunnable(final VariableDataDesc action) {
        if (!action.isDynamic())
            return null;
        return action::resolveVariable;
    }
}
