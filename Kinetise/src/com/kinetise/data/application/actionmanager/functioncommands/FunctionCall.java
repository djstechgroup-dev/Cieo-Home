package com.kinetise.data.application.actionmanager.functioncommands;

import android.content.Intent;
import android.net.Uri;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

/**
 * Created by Kuba Komorowski on 2014-12-09.
 */
public class FunctionCall extends AbstractFunction {
    public FunctionCall(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String telephoneNumber = mFunctionDataDesc.getAttributes()[0].getStringValue();

        ActionManager.getInstance().call(telephoneNumber);

        return null;
    }
}
