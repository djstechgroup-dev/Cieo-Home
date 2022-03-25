package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionTranslate;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

/**
 * Created by Marcin Narowski on 7/23/2014.
 */
public class FunctionTranslateDataDesc extends AbstractFunctionDataDesc<FunctionTranslate> {

    public FunctionTranslateDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionTranslateDataDesc(copyDesc);
    }

    @Override
    public FunctionTranslate getFunction() {
        return new FunctionTranslate(this, AGApplicationState.getInstance());
    }
}
