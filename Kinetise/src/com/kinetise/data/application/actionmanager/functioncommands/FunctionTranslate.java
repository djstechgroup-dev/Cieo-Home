package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;

/**
 * Created by Marcin Narowski on 7/23/2014.
 */
public class FunctionTranslate extends AbstractFunction {

    public FunctionTranslate(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);

        VariableDataDesc attributeDataDesc = mFunctionDataDesc.getAttributes()[0];
        String key = attributeDataDesc.getStringValue();

        String result = ActionManager.getInstance().translate(key);

        return result;
    }
}
