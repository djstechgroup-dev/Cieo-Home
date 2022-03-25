package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.screenhistory.ApplicationState;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.DataFeedContext;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.sourcemanager.propertymanager.Property;
import com.kinetise.data.sourcemanager.propertymanager.PropertyStorage;

public class AGToggleButtonDataDesc extends AGCheckBoxDataDesc {
    public AGToggleButtonDataDesc(String id) {
        super(id);
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        String propertyKey = getFormId();
        Property property = PropertyStorage.getInstance().getValue(propertyKey);
        if (property == null)
            return;
        if (mFormDescriptor.getInitValue().isDynamic()) {
            long dataFeedDate = getDataFeedDate();
            long propertyDate = property.getTimestamp();
            if (dataFeedDate >= propertyDate) {
                PropertyStorage.getInstance().removeValue(propertyKey);
            } else {
                checkIfTrue(property.getValue());
            }
        } else {
            checkIfTrue(property.getValue());
        }
    }

    @Override
    public AbstractAGCompoundButtonDataDesc createInstance() {
        return new AGToggleButtonDataDesc(String.valueOf(getId()));
    }

    protected long getDataFeedDate() {
        long dataFeedDate = 0;
        DataFeedContext dataFeedContext = getDataFeedContext();
        if (dataFeedContext != null && dataFeedContext.isInDataFeed()) {
            dataFeedDate = dataFeedContext.getFeedTimestamp();
        } else {
            ApplicationState state = AGApplicationState.getInstance().getApplicationState();
            AbstractAGViewDataDesc screenContext = null;
            if (state != null)
                screenContext = (AbstractAGViewDataDesc) state.getContext();
            if (screenContext != null) {
                dataFeedDate = ((IFeedClient) screenContext).getFeedDescriptor().getTimestamp();
            }
        }
        return dataFeedDate;
    }

}
