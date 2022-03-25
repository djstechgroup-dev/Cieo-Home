package com.kinetise.data.descriptors.desctriptorvisitors;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

import java.util.ArrayList;
/**
 * This descriptor is used to Traverse descriptors tree and get all with given class
 * */
public class FindDescendantsByTypeVisitor<T> implements IDataDescVisitor {

	private Class<T> mType;
	private ArrayList<T> mFoundDataDescriptors = new ArrayList<T>();

	public FindDescendantsByTypeVisitor(Class<T> type) {
		mType = type;
	}
	
	public ArrayList<T> getFoundDataDescriptors() {
		return mFoundDataDescriptors;
	}
	/**
     * Traverse descriptors tree and get all with given class
     * */
	@Override
    public boolean visit(AbstractAGElementDataDesc elemDesc) {
		if (mType.isAssignableFrom(elemDesc.getClass())) {
			mFoundDataDescriptors.add((T)elemDesc);
		}

		return false;
	}

}
