package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;

public class FunctionGetScreenNameDesc extends AbstractFunctionDataDesc<FunctionGetScreenName>{
    public FunctionGetScreenNameDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    protected FunctionGetScreenNameDesc copyInstance(ActionDataDesc copyDesc) {
       return new FunctionGetScreenNameDesc(copyDesc);
    }

    @Override
    public FunctionGetScreenName getFunction() {
        return new FunctionGetScreenName(this, AGApplicationState.getInstance() );
    }
}
