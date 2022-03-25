package com.kinetise.data.application.formdatautils;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.DataFeedContext;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.IDataDescVisitor;

public class GetFormDataVisitor implements IDataDescVisitor {
    protected FormData mFormData;
    protected FormItemsGroup mItemGroup;

    public GetFormDataVisitor(){}

    public GetFormDataVisitor(String screenContext, String screenGuid, String dataFeeedContext, String dataFeedGuid, boolean isInDataFeed){
        mFormData =  new FormData();
        mFormData.isInDataFeed = isInDataFeed;
        if(dataFeeedContext != null || dataFeedGuid != null){
            mItemGroup = createItem(dataFeeedContext, dataFeedGuid);
            mFormData.looseItems = mItemGroup;
            mFormData.screenAlterApiContext = screenContext;
            mFormData.screenGuid = screenGuid;
        } else {
            mItemGroup = createItem(screenContext, screenGuid);
            mFormData.looseItems = mItemGroup;
        }
    }

    public GetFormDataVisitor(String screenContext, String screenGuid){
        this(screenContext, screenGuid, null, null, false);
    }
    public GetFormDataVisitor(String screenContext, String screenGuid, String dataFeeedContext, String dataFeedGuid){
            this(screenContext, screenGuid, dataFeeedContext, dataFeedGuid, false);
        }

    protected FormItemsGroup createItem(String context, String guid) {
        FormItemsGroup itemGroup = new FormItemsGroup();
        itemGroup.setAlterApiContext(context);
        itemGroup.setFeedItemGUID(guid);
        return itemGroup;
    }

    @Override
    public boolean visit(AbstractAGElementDataDesc elemDesc) {
        if(elemDesc instanceof IFormControlDesc) {
            saveFormControlValue((IFormControlDesc) elemDesc);
            return false;
        } else if (elemDesc instanceof IFeedClient){
            createItemsForDataFeed((IFeedClient) elemDesc);
            return false;
        }
        return false;
    }

    private void saveFormControlValue(IFormControlDesc formControlDesc) {
        addFormControl(formControlDesc.getFormId(), formControlDesc.getFormValue().copy());
    }

    private void createItemsForDataFeed(IFeedClient elemDesc) {
        GetFeedItemDataVisitor visitor;
        FeedFormData feedFormData = new FeedFormData(elemDesc.getFormId());
        FormItemsGroup itemData;
        for(AbstractAGElementDataDesc desc: elemDesc.getFeedClientControls()) {
            DataFeedContext itemDataFeedContext = ((AbstractAGViewDataDesc) desc).getDataFeedContext();
            if (itemDataFeedContext !=null) { //for load more (dataFeedContext may be null)
                visitor = new GetFeedItemDataVisitor(itemDataFeedContext.getAlterApiContext(), itemDataFeedContext.getGUID());
                desc.accept(visitor);
                itemData=visitor.getItemData();
                if(!itemData.isEmpty())
                    feedFormData.addItem(itemData);
            }
        }
        if (!feedFormData.isEmpty()){
            mFormData.feeds.add(feedFormData);
        }
    }

    public FormData getFormData() {
        return mFormData;
    }

    protected void addFormControl(String formId,Object formValue){
        mItemGroup.addElement(formId, formValue);
    }

    private class GetFeedItemDataVisitor extends GetFormDataVisitor {
        public GetFeedItemDataVisitor(String itemContext, String guid) {
            String context = "";
            if(itemContext !=null)
                context = itemContext;
            mItemGroup = createItem(context, guid);
        }

        public FormItemsGroup getItemData(){
            return mItemGroup;
        }

    }
}
