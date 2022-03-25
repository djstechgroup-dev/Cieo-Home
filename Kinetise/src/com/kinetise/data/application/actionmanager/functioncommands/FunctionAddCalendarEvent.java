package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.helpers.unescapeUtils.StringEscapeUtils;

public class FunctionAddCalendarEvent extends AbstractFunction {
    public FunctionAddCalendarEvent(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();

        String title = StringEscapeUtils.unescapeHtml(attributes[0].getStringValue());
        String description = StringEscapeUtils.unescapeHtml(attributes[1].getStringValue());
        String location = StringEscapeUtils.unescapeHtml(attributes[2].getStringValue());

        String startDateString = attributes[3].getStringValue();

        String endDateString = attributes[4].getStringValue();

        String isAllDayText = attributes[5].getStringValue();

        ActionManager.getInstance().addCalendarEvent(title, description, location, startDateString, endDateString, isAllDayText);
        return null;
    }


}
