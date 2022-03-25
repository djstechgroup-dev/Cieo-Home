package com.kinetise.data.descriptors;

import com.kinetise.data.parsermanager.ParserManager;


public class SynchronizationDescriptionDataDesc {

    private boolean mEnabled;
    private String mType;
    private SynchronizationMethodDataDesc mGetMethodDataDesc;
    private SynchronizationMethodDataDesc mCreateMethodDataDesc;
    private SynchronizationMethodDataDesc mUpdateMethodDataDesc;
    private SynchronizationMethodDataDesc mDeleteMethodDataDesc;

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public void setGetMethodDataDesc(SynchronizationMethodDataDesc getMethodDataDesc) {
        mGetMethodDataDesc = getMethodDataDesc;
    }

    public SynchronizationMethodDataDesc getGetMethodDataDesc() {
        return mGetMethodDataDesc;
    }

    public void setCreateMethodDataDesc(SynchronizationMethodDataDesc createMethodDataDesc) {
        mCreateMethodDataDesc = createMethodDataDesc;
    }

    public SynchronizationMethodDataDesc getCreateMethodDataDesc() {
        return mCreateMethodDataDesc;
    }

    public void setUpdateMethodDataDesc(SynchronizationMethodDataDesc updateMethodDataDesc) {
        mUpdateMethodDataDesc = updateMethodDataDesc;
    }

    public SynchronizationMethodDataDesc getUpdateMethodDataDesc() {
        return mUpdateMethodDataDesc;
    }

    public void setDeleteMethodDataDesc(SynchronizationMethodDataDesc deleteMethodDataDesc) {
        mDeleteMethodDataDesc = deleteMethodDataDesc;
    }

    public SynchronizationMethodDataDesc getDeleteMethodDataDesc() {
        return mDeleteMethodDataDesc;
    }

    public static SynchronizationDescriptionDataDesc getSynchronizationForTable(String tableName) {
        for (TableDescriptionDataDesc tableDesc : ParserManager.getInstance().getLocalStorageDataDesc().getTablesDescriptionDataDesc().getTableListDescriptionDataDescs()) {
            if (tableDesc.getTableName().equals(tableName)) {
                return tableDesc.getSynchronizationDescriptionDataDesc();
            }
        }
        return null;
    }

    public static TableIdentifiersDataDesc getModificationIdentifiers(String tableName) {
        for (TableDescriptionDataDesc tableDesc : ParserManager.getInstance().getLocalStorageDataDesc().getTablesDescriptionDataDesc().getTableListDescriptionDataDescs()) {
            if (tableDesc.getTableName().equals(tableName)) {
                return tableDesc.getTableIdentifiersDataDesc();
            }
        }
        return null;
    }
}
