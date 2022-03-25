package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionRegex;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

/**
 * Created by Kuba Komorowski on 2014-10-16.
 */
public class FunctionRegexDataDesc extends AbstractFunctionDataDesc<FunctionRegex> {
    public FunctionRegexDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionRegexDataDesc(copyDesc);
    }

    @Override
    public FunctionRegex getFunction() {
        return new FunctionRegex(this, AGApplicationState.getInstance());
    }
}
