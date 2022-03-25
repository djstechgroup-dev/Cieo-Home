package com.kinetise.data.descriptors.desctriptorvisitors;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.IFeedClient;

public class ResetScrollAndFeedVisitor implements IDataDescVisitor {

    @Override
    public boolean visit(AbstractAGElementDataDesc elemDesc) {
        elemDesc.setScrollX(0);
        elemDesc.setScrollY(0);
        if (elemDesc instanceof IFeedClient) {
            ((IFeedClient) elemDesc).setLastFeedItemCount(0);
        }
        return false;
    }
}
