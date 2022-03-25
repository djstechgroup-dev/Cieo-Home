package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.types.AGScreenTransition;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;

public class FunctionBackToScreen extends AbstractFunction {
    public FunctionBackToScreen(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);

        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        String screenId = attributes[0].getStringValue();
        AGScreenTransition transition = AGXmlParserHelper.getScreenTransition(attributes[1].getStringValue());
        ActionManager.getInstance().backToScreen(screenId, transition);
        return null;
    }
}
