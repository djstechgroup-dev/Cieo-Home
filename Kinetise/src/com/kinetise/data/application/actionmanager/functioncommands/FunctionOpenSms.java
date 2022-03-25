package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

import org.apache.commons.lang.StringEscapeUtils;

public class FunctionOpenSms extends AbstractFunction {
    public FunctionOpenSms(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);

        /**
         * If multiple recipients were to be added - samsung uses different separators for multiple numbers commas
         * instead if semicolons.
         *
         * String separator = (android.os.Build.MANUFACTURER.toLowerCase().contains("samsung")) ? ", " : "; ";
         *
         **/

        String phoneNumber = mFunctionDataDesc.getAttributes()[0].getStringValue();
        String message = StringEscapeUtils.unescapeXml(mFunctionDataDesc.getAttributes()[1].getStringValue());
        ActionManager.getInstance().openSms(phoneNumber, message);
        return null;
    }


}
