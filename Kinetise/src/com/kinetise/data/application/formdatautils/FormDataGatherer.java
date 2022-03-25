package com.kinetise.data.application.formdatautils;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.DataFeedContext;

public class FormDataGatherer {

    public static FormData getFormData(AbstractAGViewDataDesc desc, String screenContext, String screenDetailGuid) {
        GetFormDataVisitor visitor;
        String itemContext = resolveAlterApiContext(desc);
        String itemGuid = resolveItemGuid(desc);

        if (itemContext != null) {
            visitor = new GetFormDataVisitor(screenContext, screenDetailGuid, itemContext, itemGuid, isInDataFeed(desc));
        } else {
            visitor = new GetFormDataVisitor(screenContext, screenDetailGuid);
        }
        desc.accept(visitor);
        return visitor.getFormData();
    }

    protected static String resolveItemGuid(AbstractAGViewDataDesc desc) {
        DataFeedContext dataFeedContext = desc.getDataFeedContext();
        if (dataFeedContext != null)
            return dataFeedContext.getGUID();
        return null;
    }

    private static String resolveAlterApiContext(AbstractAGViewDataDesc desc) {
        DataFeedContext dataFeedContext = desc.getDataFeedContext();
        String context = null;
        if (dataFeedContext != null && dataFeedContext.isInDataFeed()) {
            context = dataFeedContext.getAlterApiContext();
            if (context == null) //Feed in non alter-api context
                context = "";
        }
        return context;
    }

    private static boolean isInDataFeed(AbstractAGViewDataDesc desc) {
        DataFeedContext dataFeedContext = desc.getDataFeedContext();
        if (dataFeedContext != null)
            return dataFeedContext.isInDataFeed();
        return false;
    }
}
