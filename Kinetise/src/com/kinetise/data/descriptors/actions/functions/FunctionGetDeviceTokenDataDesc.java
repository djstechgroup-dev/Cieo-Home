package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetDeviceToken;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

/**
 * @author: Marcin Narowski
 * Date: 08.04.14
 * Time: 15:40
 */
public class FunctionGetDeviceTokenDataDesc extends AbstractFunctionDataDesc<FunctionGetDeviceToken> {
    public FunctionGetDeviceTokenDataDesc(ActionDataDesc pActionDataDesc) {
        super(pActionDataDesc);
    }

    @Override
    public AbstractFunctionDataDesc copyInstance(ActionDataDesc copyDesc) {
        return new FunctionGetDeviceTokenDataDesc(copyDesc);
    }

    @Override
    public FunctionGetDeviceToken getFunction() {
        return new FunctionGetDeviceToken(this, AGApplicationState.getInstance());
    }

}
