package com.kinetise.data.descriptors.desctriptorvisitors;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
/**
 * This descriptor traverses descriptors tree and gets one with specified id.
 * */
public class FindDescendantByIdVisitor implements IDataDescVisitor {

    private String mId;
    private AbstractAGElementDataDesc mFoundDataDesc;

    public FindDescendantByIdVisitor(String id) {
        mId = id;
        mFoundDataDesc = null;
    }



    public AbstractAGElementDataDesc getFoundDataDesc() {
        return mFoundDataDesc;
    }

    /**
     * Traverses descriptors tree and gets one with specified id.
     */
    @Override
    public boolean visit(AbstractAGElementDataDesc elemDesc) {

        if (!(elemDesc instanceof AbstractAGViewDataDesc))
            return false;

        AbstractAGViewDataDesc viewElemDataDesc = (AbstractAGViewDataDesc) elemDesc;

        if (mId.equals(viewElemDataDesc.getId())) {
            mFoundDataDesc = viewElemDataDesc;
            return true;
        }

        return false;
    }

}
