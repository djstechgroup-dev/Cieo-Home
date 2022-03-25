package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.types.AGScreenTransition;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;

public class FunctionGoToPreviousScreen extends AbstractFunction {

    public FunctionGoToPreviousScreen(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc,application);
    }
    /**
     * Redirect user to previously visited screen
     * @param desc Descriptor on which action should be called
     * @return null
     * */
    @Override
    public Object execute(Object desc) {
        super.execute(desc);

        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();

        if (attributes.length != 1) {
            throw new IllegalArgumentException("FunctionGoToPreviousScreen function should have only one parameter!");
        }

        AGScreenTransition transition = AGXmlParserHelper.getScreenTransition(attributes[0].getStringValue());

        ActionManager.getInstance().goToPreviousScreen(transition);
        return null;
    }

}
