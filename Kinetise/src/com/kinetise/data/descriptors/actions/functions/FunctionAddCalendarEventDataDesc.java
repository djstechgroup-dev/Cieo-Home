package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.AbstractFunction;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionAddCalendarEvent;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionAddCalendarEventDataDesc extends AbstractFunctionDataDesc {
    public FunctionAddCalendarEventDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionAddCalendarEventDataDesc(copyDesc);
    }

    @Override
    public AbstractFunction getFunction() {
        return new FunctionAddCalendarEvent(this, AGApplicationState.getInstance());
    }
}
