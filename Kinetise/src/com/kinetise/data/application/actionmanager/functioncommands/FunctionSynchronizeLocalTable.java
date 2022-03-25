package com.kinetise.data.application.actionmanager.functioncommands;

import android.support.annotation.NonNull;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.feedmanager.DataFeedDatabase;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedModification;
import com.kinetise.data.descriptors.SynchronizationDescriptionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.actions.functions.FunctionSynchronizeLocalTableDataDesc;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;

import java.util.concurrent.LinkedBlockingQueue;

public class FunctionSynchronizeLocalTable extends FunctionSynchronizeLocalDB {

    private String tableName;
    private SynchronizationDescriptionDataDesc mSynchronizationDataDesc;

    public FunctionSynchronizeLocalTable(FunctionSynchronizeLocalTableDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @NonNull
    @Override
    protected LinkedBlockingQueue<DataFeedModification> getDataFeedModifications() {
        return new LinkedBlockingQueue<>(DataFeedDatabase.getInstance().getModificationsLogForTable(tableName));
    }

    @Override
    protected SynchronizationDescriptionDataDesc getSynchronizationDataDesc(String tableName) {
        if (mSynchronizationDataDesc == null)
            mSynchronizationDataDesc = SynchronizationDescriptionDataDesc.getSynchronizationForTable(tableName);
        return mSynchronizationDataDesc;
    }

    @Override
    protected void parseAttributes() {
        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        tableName = attributes[0].getStringValue();
        String actionString = attributes[1].getStringValue();

        actionString = AGXmlActionParser.unescape(actionString);
        mSuccessAction = AGXmlActionParser.createMultiAction(actionString, mFunctionDataDesc.getContextDataDesc());
    }

}