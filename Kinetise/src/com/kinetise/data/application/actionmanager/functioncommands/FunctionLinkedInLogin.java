package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.helpers.LinkedinService;

public class FunctionLinkedInLogin extends AbstractLoginFunction {

    public FunctionLinkedInLogin(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        parseAttributes();

        new LinkedinService(mApplication.getActivity()).loginToLinkedIn(this); //TODO czy to nie można by statycznymi metodami w tym serwisie zamiast new
        return null;
    }

    protected void parseAttributes() {
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        mAlterApiUrl = attributes[0].getStringValue();

        String actionString = attributes[1].getStringValue();
        actionString = AGXmlActionParser.unescape(actionString);
        mActionDataDesc = AGXmlActionParser.createMultiAction(actionString, mFunctionDataDesc.getContextDataDesc());

        mAlterApiHttpParams = HttpParamsDataDesc.getHttpParams(attributes[2].getStringValue(), mFunctionDataDesc.getContextDataDesc());
        if(attributes.length>=4)
            mAlterApiHeaderParams = HttpParamsDataDesc.getHttpParams(attributes[3].getStringValue(), mFunctionDataDesc.getContextDataDesc());
        mAlterApiHeaderParams = null;

    }
}