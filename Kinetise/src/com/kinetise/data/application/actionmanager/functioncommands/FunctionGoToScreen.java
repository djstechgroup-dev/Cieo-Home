package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.types.AGScreenTransition;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;

import java.security.InvalidParameterException;

public class FunctionGoToScreen extends AbstractFunction {

    private String mNextScreenId;

    public FunctionGoToScreen(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    /**
     * Redirects user to screen with given id(screen id is param of this function in xml)
     *
     * @param desc Descriptor on which action should be called
     * @return null
     */
    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        synchronized (ExecuteActionManager.FUNCTION_SYNCHRONIZER) {

            if (desc != null) {
                throw new InvalidParameterException(
                        "GoToScreen function is not execute on descriptor! The descriptor should be null!");
            }

            VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();

            if (attributes.length != 2) {
                throw new InvalidParameterException(
                        "GoToScreen function should have only two parameter");
            }

            mNextScreenId = attributes[0].getStringValue();
            AGScreenTransition transition = AGXmlParserHelper.getScreenTransition(attributes[1].getStringValue());
            ActionManager.getInstance().goToScreen(mNextScreenId, transition);
            return null;
        }
    }

    @Override
    public String toString() {
        return mNextScreenId;
    }
}
