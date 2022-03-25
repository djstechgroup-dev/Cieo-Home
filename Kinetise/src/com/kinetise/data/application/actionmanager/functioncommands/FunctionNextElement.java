package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.types.AGScreenTransition;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;

public class FunctionNextElement extends AbstractFunction {

    public FunctionNextElement(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String transitionParam = mFunctionDataDesc.getAttributes()[0].getStringValue();
        AGScreenTransition transition = AGXmlParserHelper.getScreenTransition(transitionParam);
        ActionManager.getInstance().nextElement(transition);
        return null;
    }


}
