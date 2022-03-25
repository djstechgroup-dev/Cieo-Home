package com.kinetise.data.descriptors.actions;

import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

public class ActionVariableDataDesc extends VariableDataDesc {
    private MultiActionDataDesc mActions;

    public ActionVariableDataDesc(Object value, MultiActionDataDesc actions) {
        super(value);
        mActions = actions;
    }

    public ActionVariableDataDesc(MultiActionDataDesc actions) {
        mActions = actions;
    }

    public MultiActionDataDesc getActions() {
        return mActions;
    }

    public ActionVariableDataDesc copy(AbstractAGElementDataDesc copyDesc) {
        MultiActionDataDesc actions;
        if (mActions != null) {
            actions = mActions.copy(copyDesc);
        } else {
            actions = null;
        }
        ActionVariableDataDesc copied = new ActionVariableDataDesc(actions);
        copied.copyValuesFrom(this);
        return copied;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    public void resolveVariable() {
        setResolvedValue(ExecuteActionManager.executeMultiAction(mActions));
    }
}
