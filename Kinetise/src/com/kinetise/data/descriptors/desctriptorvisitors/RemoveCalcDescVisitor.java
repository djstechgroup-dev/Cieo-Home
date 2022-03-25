package com.kinetise.data.descriptors.desctriptorvisitors;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

/**
 * This descriptor is used to Traverse descriptors tree and remove their calcDescriptors
 */
public class RemoveCalcDescVisitor implements IDataDescVisitor {

    public RemoveCalcDescVisitor(){
    }

    @Override
    public boolean visit(AbstractAGElementDataDesc elemDesc) {
        elemDesc.removeCalcDesc();
        return false;
    }
}
