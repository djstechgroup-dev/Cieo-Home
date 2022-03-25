package com.kinetise.data.descriptors.desctriptorvisitors;

import com.kinetise.data.descriptors.AbstractAGSectionDataDesc;
import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

/**
 * This visitor is used to traverse screen hierarchy searching maximum container/collection tree
 */
public class DepthDescVisitor implements IDataDescVisitor {

    private int mDepth = 0;

    /**
     * Traverses descriptors tree searching for maximum containers tree depth
     * */
    @Override
    public boolean visit(AbstractAGElementDataDesc elemDesc) {

        if (elemDesc instanceof AbstractAGSectionDataDesc || elemDesc instanceof AbstractAGContainerDataDesc) {
            int elementDepth = elemDesc.getDepthCount();

            if (mDepth < elementDepth) {
                mDepth = elementDepth;
            }
        }
        return false;
    }

    public int getDepth() {
        return mDepth;
    }
}
