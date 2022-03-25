package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.types.AGScreenTransition;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;

public class FunctionBackBySteps extends AbstractFunction {
    public FunctionBackBySteps(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        int steps;
        AGScreenTransition transition = AGScreenTransition.NONE;
        try {
            VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
            steps = Integer.parseInt(attributes[0].getStringValue());
            transition = AGXmlParserHelper.getScreenTransition(attributes[1].getStringValue());
        } catch (NumberFormatException e) {
            return null;
        }
        ActionManager.getInstance().backBySteps(steps, transition);
        return null;
    }

    protected AGApplicationState getApplicationState() {
        return AGApplicationState.getInstance();
    }
}
