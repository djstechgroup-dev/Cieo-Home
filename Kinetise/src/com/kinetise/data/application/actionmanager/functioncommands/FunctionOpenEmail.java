package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Created by Kuba Komorowski on 2014-12-10.
 */
public class FunctionOpenEmail extends AbstractFunction {
    public FunctionOpenEmail(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);

        String subject = StringEscapeUtils.unescapeXml(mFunctionDataDesc.getAttributes()[0].getStringValue());
        String emailBody = StringEscapeUtils.unescapeXml(mFunctionDataDesc.getAttributes()[1].getStringValue());
        String emailAddress = StringEscapeUtils.unescapeXml(mFunctionDataDesc.getAttributes()[2].getStringValue());

        ActionManager.getInstance().openEmail(subject, emailBody, emailAddress);
        return null;
    }


}
