package com.kinetise.data.application.formdatautils;


import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.DataFeedContext;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.datadescriptors.AGDropdownDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGSignatureDataDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.FindDescendantsByTypeVisitor;
import com.kinetise.data.descriptors.types.GestureInfo;
import com.kinetise.data.descriptors.types.PhotoInfo;

import java.util.List;

public class FormDataSerializer {

    public static FeedFormData serializeFormData(AbstractAGElementDataDesc desc) {
        GetFormDataVisitor visitor = new GetFormDataVisitor(null, null);
        desc.accept(visitor);
        FormData formData = visitor.getFormData();
        return formData.feeds.size() > 0 ? formData.feeds.get(0) : null;
    }

    public static void recreateFeedFormData(IFeedClient feedClient, FeedFormData formData) {
        if (formData == null)
            return;
        List<AbstractAGElementDataDesc> feedItems = feedClient.getFeedClientControls();
        List<IFormControlDesc> formElements;
        FindDescendantsByTypeVisitor<IFormControlDesc> visitor;
        for (AbstractAGElementDataDesc item : feedItems) {
            visitor = new FindDescendantsByTypeVisitor<>(IFormControlDesc.class);
            item.accept(visitor);
            formElements = visitor.getFoundDataDescriptors();
            if (!formElements.isEmpty()) {
                FormItemsGroup formItemsGroup = findMatchingData(formData, (AbstractAGViewDataDesc) item);
                if (formItemsGroup != null) {
                    recreateElement(formElements, formItemsGroup);
                }
            }
        }
    }

    private static void recreateElement(List<IFormControlDesc> formElements, FormItemsGroup formItemsGroup) {
        formItemsGroup.getFormItems();
        Object formValue;
        for (IFormControlDesc formControl : formElements) {
            formValue = formItemsGroup.getValue(formControl.getFormId());
            if (formValue != null) {
                if (formControl instanceof AGDropdownDataDesc)
                    ((AGDropdownDataDesc) formControl).setOptionByFormValue(formValue.toString());
                else if (formValue instanceof PhotoInfo)
                    formControl.setFormValue(((PhotoInfo) formValue).getPath());
                else if (formValue instanceof GestureInfo)
                    ((AGSignatureDataDesc) formControl).setGestureInfo((GestureInfo) formValue);
                else
                    formControl.setFormValue(formValue.toString());
            }
        }
    }

    private static FormItemsGroup findMatchingData(FeedFormData formData, AbstractAGViewDataDesc item) {
        DataFeedContext itemDataFeedContext = item.getDataFeedContext();
        //dataFeedContext can be null f.e. for Show More button
        if (itemDataFeedContext == null)
            return null;
        String guid = itemDataFeedContext.getGUID();
        for (FormItemsGroup formItemsGroup : formData.items) {
            if (formItemsGroup.matches(guid))
                return formItemsGroup;
        }
        return null;
    }
}
