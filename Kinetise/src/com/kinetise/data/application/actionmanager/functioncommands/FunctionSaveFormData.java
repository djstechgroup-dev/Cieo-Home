package com.kinetise.data.application.actionmanager.functioncommands;

import android.util.Pair;

import com.kinetise.data.VariableStorage;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.application.formdatautils.FeedFormData;
import com.kinetise.data.application.formdatautils.FormData;
import com.kinetise.data.application.formdatautils.FormDataGatherer;
import com.kinetise.data.application.formdatautils.FormItemsGroup;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.MultiActionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.actions.functions.FunctionSaveFormDataDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.ValidateFormVisitor;
import com.kinetise.data.descriptors.types.GestureInfo;
import com.kinetise.data.descriptors.types.PhotoInfo;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

public class FunctionSaveFormData extends AbstractFunction {
    AbstractAGViewDataDesc mFormContainer;
    MultiActionDataDesc mSucessAction;

    public FunctionSaveFormData(FunctionSaveFormDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        parseAttributes();
        if (canSend()) {
            FormData formData = FormDataGatherer.getFormData(mFormContainer, null, null);
            saveDataFromFormItemGroup(formData.looseItems);
            for (FeedFormData feedFormData : formData.feeds) {
                for (FormItemsGroup itemGroup : feedFormData.getItems()) {
                    saveDataFromFormItemGroup(itemGroup);
                }
            }

            ExecuteActionManager.executeMultiAction(mSucessAction);
        }
        return null;
    }

    protected boolean canSend() {
        ValidateFormVisitor visitor = new ValidateFormVisitor();
        if (mFormContainer != null) {
            mFormContainer.accept(visitor);
        }
        return visitor.isFormValid();
    }

    private void saveDataFromFormItemGroup(FormItemsGroup itemGroup) {
        for (Pair<String, Object> item : itemGroup.formItems) {
            if (!(item.second instanceof PhotoInfo) && !(item.second instanceof GestureInfo))
                VariableStorage.getInstance().addValue(item.first, item.second.toString());
        }
    }

    protected void parseAttributes() {
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        mFormContainer = getFormContainer(attributes[0]);
        String actionString = attributes[1].getStringValue();
        actionString = AGXmlActionParser.unescape(actionString);
        mSucessAction = AGXmlActionParser.createMultiAction(actionString, mFunctionDataDesc.getContextDataDesc());
    }

    private AbstractAGViewDataDesc getFormContainer(VariableDataDesc attribute) {
        String getControlAction = attribute.getStringValue().trim();
        getControlAction = AGXmlActionParser.unescape(getControlAction);
        MultiActionDataDesc multiAction = AGXmlActionParser.createMultiAction(getControlAction, mFunctionDataDesc.getContextDataDesc());
        return (AbstractAGViewDataDesc) ExecuteActionManager.executeMultiAction(multiAction);
    }
}