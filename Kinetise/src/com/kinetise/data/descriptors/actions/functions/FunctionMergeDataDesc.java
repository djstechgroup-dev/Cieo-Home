package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionMerge;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

/**
 * Created by Kuba on 21.05.2014.
 */
public class FunctionMergeDataDesc extends AbstractFunctionDataDesc<FunctionMerge> {

    public FunctionMergeDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionMergeDataDesc(copyDesc);
    }

    @Override
    public FunctionMerge getFunction() {
        return new FunctionMerge(this, AGApplicationState.getInstance());
    }
}
