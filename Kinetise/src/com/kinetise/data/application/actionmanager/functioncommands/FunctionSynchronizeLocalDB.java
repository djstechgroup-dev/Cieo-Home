package com.kinetise.data.application.actionmanager.functioncommands;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.application.alterapimanager.AGOkHttpConfigurator;
import com.kinetise.data.application.alterapimanager.IRequestCallback;
import com.kinetise.data.application.feedmanager.DataFeedDatabase;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedModification;
import com.kinetise.data.application.formdatautils.FormData;
import com.kinetise.data.application.formdatautils.FormFormaterV3;
import com.kinetise.data.application.formdatautils.FormItemsGroup;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.descriptors.SynchronizationDescriptionDataDesc;
import com.kinetise.data.descriptors.SynchronizationMethodDataDesc;
import com.kinetise.data.descriptors.TableIdentifierDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.MultiActionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.ItemPath;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.helpers.http.HttpRequestManager;
import com.kinetise.helpers.http.NetworkUtils;
import com.kinetise.helpers.jq.JQBridge;
import com.kinetise.helpers.parser.JsonParser;
import com.kinetise.helpers.parser.JsonParserException;
import com.kinetise.helpers.threading.ThreadPool;
import com.kinetise.helpers.threading.UncancelableTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.kinetise.helpers.parser.JsonParser.parseToObjects;

public class FunctionSynchronizeLocalDB<T extends FunctionSynchronizeLocalDB> extends AbstractFunction implements IRequestCallback {

    protected MultiActionDataDesc mSuccessAction;
    private Queue<DataFeedModification> mModifications;

    public FunctionSynchronizeLocalDB(AbstractFunctionDataDesc<T> functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        parseAttributes();

        AGApplicationState.getInstance().getSystemDisplay().blockScreenWithLoadingDialog(true);
        mModifications = getDataFeedModifications();

        synchronizeNextModification();

        return null;
    }

    @NonNull
    protected LinkedBlockingQueue<DataFeedModification> getDataFeedModifications() {
        return new LinkedBlockingQueue<>(DataFeedDatabase.getInstance().getModificationsLog());
    }

    protected SynchronizationDescriptionDataDesc getSynchronizationDataDesc(String tableName) {
        return SynchronizationDescriptionDataDesc.getSynchronizationForTable(tableName);
    }

    void synchronizeNextModification() {
        DataFeedModification modification = mModifications.peek();
        if (modification != null) {
            SynchronizationMethodDataDesc synchronizationMethodDataDesc;
            if (modification.getModificationType().equals(DataFeedDatabase.CREATE)) {
                synchronizationMethodDataDesc = getSynchronizationDataDesc(modification.getTableName()).getCreateMethodDataDesc();
            } else if (modification.getModificationType().equals(DataFeedDatabase.UPDATE)) {
                synchronizationMethodDataDesc = getSynchronizationDataDesc(modification.getTableName()).getUpdateMethodDataDesc();
            } else if (modification.getModificationType().equals(DataFeedDatabase.DELETE)) {
                synchronizationMethodDataDesc = getSynchronizationDataDesc(modification.getTableName()).getDeleteMethodDataDesc();
            } else {
                return;
            }

            FormFormaterV3 formater = new FormFormaterV3();

            FormData formData = new FormData();
            FormItemsGroup formItemsGroup = new FormItemsGroup();

            for (Map.Entry<String, Object> entry : modification.getDataFeedItem().getNameValuePairs().entrySet()) {
                formItemsGroup.addElement(entry.getKey(), entry.getValue());
            }

            formData.looseItems = formItemsGroup;

            JsonObject json = formater.format(formData, null);
            String jsonAfterTransform = JQBridge.runTransform(synchronizationMethodDataDesc.getRequestBodyTrasform(), json.toString(), AGApplicationState.getInstance().getContext());

            synchronize(modification, synchronizationMethodDataDesc, jsonAfterTransform);
        } else {
            AGApplicationState.getInstance().getSystemDisplay().blockScreenWithLoadingDialog(false);
            ExecuteActionManager.executeMultiAction(mSuccessAction);
        }
    }

    private void synchronize(DataFeedModification modification, SynchronizationMethodDataDesc synchronizationMethodDataDesc, String jsonAfterTransform) {
        Runnable runnable = () -> {
            Map headers = synchronizationMethodDataDesc.getHeaders().getHttpParamsAsHashMap();
            if (!headers.containsKey(AGOkHttpConfigurator.CONTENT_TYPE_HEADER_NAME))
                headers.put(AGOkHttpConfigurator.CONTENT_TYPE_HEADER_NAME, AGOkHttpConfigurator.CONTENT_TYPE_JSON);
            NetworkUtils.sendRequest(
                    AssetsManager.addHttpQueryParams(synchronizationMethodDataDesc.getSource(), synchronizationMethodDataDesc.getHttpParams()),
                    headers,
                    jsonAfterTransform,
                    new FunctionSynchronizeLocalDB.RequestCallback(modification));
        };
        ThreadPool.getInstance().executeBackground(new UncancelableTask(runnable));
    }

    protected void parseAttributes() {
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        String actionString = attributes[0].getStringValue();

        actionString = AGXmlActionParser.unescape(actionString);
        mSuccessAction = AGXmlActionParser.createMultiAction(actionString, mFunctionDataDesc.getContextDataDesc());
    }

    @Override
    public void onError(PopupMessage... messages) {
        AGApplicationState.getInstance().getSystemDisplay().blockScreenWithLoadingDialog(false);
    }

    @Override
    public void onSuccess(PopupMessage... messages) {
        DataFeedDatabase.getInstance().removeModification(mModifications.poll());
        synchronizeNextModification();
    }


    private class RequestCallback implements NetworkUtils.RequestCallback {

        private DataFeedModification modification;

        public RequestCallback(DataFeedModification modification) {
            this.modification = modification;
        }

        @Override
        public void onFailed(HttpRequestManager requestManager, PopupMessage... messages) {
            AGApplicationState.getInstance().getSystemDisplay().blockScreenWithLoadingDialog(false);
        }

        @Override
        public void onResponse(HttpRequestManager requestManager) {
            if (modification.getModificationType() == DataFeedDatabase.CREATE) {
                try {
                    String response = IOUtils.toString(requestManager.getContent());
                    for (TableIdentifierDataDesc identifierDataDesc : SynchronizationDescriptionDataDesc.getModificationIdentifiers(modification.getTableName()).getIdentifiers()) {
                        Object jsonObject = parseToObjects(response);
                        Object id = JsonParser.getItem(jsonObject, identifierDataDesc.getCreateResponsePath());
                        if (id != null) {
                            addIdToItem(modification, id, identifierDataDesc.getFieldPath());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (JsonParserException e1) {
                    e1.printStackTrace();
                }
            }
            DataFeedDatabase.getInstance().removeModification(mModifications.poll());
            synchronizeNextModification();
        }

        @Override
        public void onLogout() {
            AGApplicationState.getInstance().getSystemDisplay().blockScreenWithLoadingDialog(false);
        }

    }

    private void addIdToItem(DataFeedModification modification, Object id, ItemPath itemPath) {
        String jPath = itemPath.getXPath();
        String[] paths = JsonParser.parseItemPath(jPath);

        for (DataFeedItem item : DataFeedDatabase.getInstance().get(modification.getTableName()).getItems()) {
            if (item.equalsById(modification.getDataFeedItem())) {
                updateDataFeedItem(id, jPath, paths[0], item);
            }
        }

        for (DataFeedModification mod : DataFeedDatabase.getInstance().getModificationsLogForTable(modification.getTableName())) {
            DataFeedItem item = mod.getDataFeedItem();
            if (item.equalsById(modification.getDataFeedItem())) {
                updateDataFeedItem(id, jPath, paths[0], item);
            }
        }
    }

    private void updateDataFeedItem(Object id, String jPath, String path, DataFeedItem item) {
        Map<String, Object> object = item.getNameValuePairs();
        String rootPath = path;
        if (object.containsKey(rootPath)) {
            object.remove(rootPath);
        }
        String idKey = jPath.substring(jPath.indexOf('.') + 1);
        object.put(idKey, id);
        item.putIdentifier(idKey, id);
    }

}