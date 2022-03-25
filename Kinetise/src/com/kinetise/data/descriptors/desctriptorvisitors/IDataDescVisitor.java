package com.kinetise.data.descriptors.desctriptorvisitors;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
/**
 * Base interface for all DataDesc Visitors
 * */
public interface IDataDescVisitor {

	boolean visit(AbstractAGElementDataDesc elemDesc);
}
