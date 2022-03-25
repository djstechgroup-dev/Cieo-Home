package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetAlterApiContext;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

/**
 * @author: Marcin Narowski
 * Date: 04.04.14
 * Time: 09:39
 */
public class FunctionGetAlterApiContextDataDesc extends AbstractFunctionDataDesc<FunctionGetAlterApiContext> {
    public FunctionGetAlterApiContextDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
        return new FunctionGetAlterApiContextDataDesc(copyDesc);
    }

    @Override
    public FunctionGetAlterApiContext getFunction() {
        return new FunctionGetAlterApiContext(this, AGApplicationState.getInstance());
    }

}
