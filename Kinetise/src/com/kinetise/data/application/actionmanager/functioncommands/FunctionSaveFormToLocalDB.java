package com.kinetise.data.application.actionmanager.functioncommands;

import android.graphics.Bitmap;
import android.util.Pair;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedDBManager;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.application.formdatautils.FeedFormData;
import com.kinetise.data.application.formdatautils.FormData;
import com.kinetise.data.application.formdatautils.FormDataGatherer;
import com.kinetise.data.application.formdatautils.FormItemsGroup;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.MultiActionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.actions.functions.FunctionSaveFormToLocalDBDataDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.ValidateFormVisitor;
import com.kinetise.data.descriptors.types.GestureInfo;
import com.kinetise.data.descriptors.types.FormString;
import com.kinetise.data.descriptors.types.PhotoInfo;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.helpers.BitmapHelper;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static com.kinetise.data.application.feedmanager.DataFeedDatabase.CREATE;
import static com.kinetise.data.application.feedmanager.DataFeedDatabase.DELETE;
import static com.kinetise.data.application.feedmanager.DataFeedDatabase.UPDATE;

public class FunctionSaveFormToLocalDB extends AbstractFunction {

    private AbstractAGViewDataDesc mFormContainer;
    private HttpParamsDataDesc params;
    private String actionType;
    private String tableName;
    private String matcherJS;
    private MultiActionDataDesc mSucessAction;

    public FunctionSaveFormToLocalDB(FunctionSaveFormToLocalDBDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        parseAttributes();

        if (canSend()) {
            FormData formData = FormDataGatherer.getFormData(mFormContainer, null, null);

            DataFeedItem dataFeedItem = createDataFeedItemFromForm(formData);

            for (Map.Entry<String, String> entry : params.getHttpParamsAsHashMap().entrySet()) {
                dataFeedItem.put(entry.getKey(), entry.getValue());
            }

            if (actionType != null) {
                switch (actionType.toUpperCase()) {
                    case CREATE:
                        create(tableName, dataFeedItem);
                        break;

                    case UPDATE:
                        update(tableName, dataFeedItem, matcherJS);
                        break;

                    case DELETE:
                        delete(tableName, dataFeedItem, matcherJS);
                        break;
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


    private static void create(String table, DataFeedItem dataFeedItemFromForm) {
        DataFeedDBManager.insertItemIntoTable(table, dataFeedItemFromForm);
    }

    private static void update(String tableName, DataFeedItem dataFeedItemFromForm, String matcherJS) {
        DataFeedDBManager.updateItemInTable(tableName, dataFeedItemFromForm, matcherJS);
    }

    private static void delete(String tableName, DataFeedItem dataFeedItemFromForm, String keys) {
        DataFeedDBManager.delete(tableName, dataFeedItemFromForm, keys);
    }


    protected void parseAttributes() {
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        mFormContainer = getFormContainer(attributes[0]);
        tableName = attributes[1].getStringValue();
        actionType = attributes[2].getStringValue();
        params = HttpParamsDataDesc.getHttpParams(attributes[3].getStringValue(), mFunctionDataDesc.getContextDataDesc());
        matcherJS = attributes[4].getStringValue();
        String actionString = attributes[5].getStringValue();

        actionString = AGXmlActionParser.unescape(actionString);
        mSucessAction = AGXmlActionParser.createMultiAction(actionString, mFunctionDataDesc.getContextDataDesc());
    }

    private AbstractAGViewDataDesc getFormContainer(VariableDataDesc attribute) {
        String getControlAction = attribute.getStringValue().trim();
        getControlAction = AGXmlActionParser.unescape(getControlAction);
        MultiActionDataDesc multiAction = AGXmlActionParser.createMultiAction(getControlAction, mFunctionDataDesc.getContextDataDesc());
        return (AbstractAGViewDataDesc) ExecuteActionManager.executeMultiAction(multiAction);
    }

    private DataFeedItem createDataFeedItemFromForm(FormData formData) {
        DataFeedItem item = new DataFeedItem("");

        saveDataFromFormItemGroup(formData.looseItems, item);
        for (FeedFormData feedFormData : formData.feeds) {
            for (FormItemsGroup itemGroup : feedFormData.getItems()) {
                saveDataFromFormItemGroup(itemGroup, item);
            }
        }

        return item;
    }

    private void saveDataFromFormItemGroup(FormItemsGroup itemGroup, DataFeedItem dataFeedItem) {
        for (Pair<String, Object> item : itemGroup.formItems) {
            if (!(item.second instanceof PhotoInfo) && !(item.second instanceof GestureInfo) && !(item.second instanceof FormString)) {
                dataFeedItem.put(item.first, item.second);
            } else if (item.second instanceof PhotoInfo) {
                PhotoInfo photoInfo = (PhotoInfo) item.second;
                String path = photoInfo.getPath();
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapHelper.getBitmapWithCorrectRotation(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                saveBitmapData(dataFeedItem, item, bitmap);
            } else if (item.second instanceof GestureInfo) {
                GestureInfo gestureInfo = (GestureInfo) item.second;
                Bitmap bitmap = gestureInfo.getGestureBitmap();

                if(bitmap == null){
                    dataFeedItem.put(item.first, null);
                } else {
                    saveBitmapData(dataFeedItem, item, bitmap);
                }
            } else if (item.second instanceof FormString){
                dataFeedItem.put(item.first, item.second.toString());
            }
        }
    }

    private void saveBitmapData(DataFeedItem dataFeedItem, Pair<String, Object> item, Bitmap bitmap) {
        if (bitmap!=null) {
            String filename = UUID.randomUUID().toString();
            filename = filename.concat(".png");

            String absolutePath = BitmapHelper.saveToInternalStorage(AGApplicationState.getInstance().getContext(), bitmap, filename);
            dataFeedItem.put(item.first, "local:/" + absolutePath.concat("/").concat(filename));

            //TODO skasować ale zostawiłem żeby było info jak pobierać.:
            //Bitmap b = BitmapHelper.getFromInternalStorage(AGApplicationState.getInstance().getContext(), absolutePath.concat("/").concat(filename));
        }
    }
}