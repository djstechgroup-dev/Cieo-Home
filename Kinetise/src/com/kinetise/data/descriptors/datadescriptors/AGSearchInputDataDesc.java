package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.descriptors.actions.MultiActionDataDesc;
import com.kinetise.data.descriptors.types.FormString;

/**
 * In general this class implements form-sending related options like form name, form value, and watermark(hint on inputs)
 */
public class AGSearchInputDataDesc extends AGTextInputDataDesc<FormString> {

    private MultiActionDataDesc mOnAcceptActionDesc;

    public AGSearchInputDataDesc(String id) {
        super(id);
    }

    public AGSearchInputDataDesc createInstance() {
        return new AGSearchInputDataDesc(getId());
    }

    @Override
    public AGTextDataDesc copy() {
        AGSearchInputDataDesc copy = (AGSearchInputDataDesc) super.copy();
        if (mOnAcceptActionDesc != null) {
            copy.mOnAcceptActionDesc = mOnAcceptActionDesc.copy(copy);
        }
        return copy;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
    }

    public void setOnAcceptActionDesc(MultiActionDataDesc action) {
        this.mOnAcceptActionDesc = action;
    }

    public MultiActionDataDesc getOnAcceptActionDesc() {
        return mOnAcceptActionDesc;
    }

    public void onAccept() {
        MultiActionDataDesc action = getOnAcceptActionDesc();
        if (action != null) {
            action.resolveVariablesInParameters();
            ExecuteActionManager.executeMultiAction(action);
        }
    }
}
