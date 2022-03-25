package com.kinetise.data.descriptors;

import com.kinetise.data.descriptors.calcdescriptors.AGElementCalcDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.IDataDescVisitor;

import java.io.Serializable;

/**
 * Base class for all descriptors
 */
public abstract class AbstractAGElementDataDesc implements Serializable {
    /**
     * Used by {@link com.kinetise.data.descriptors.desctriptorvisitors.DepthDescVisitor} to verify
     * if current descriptors hierarchy isn't greater than max 9
     */
    public int getDepthCount() {
        return 0;
    }

    private static final java.lang.Object ELEMENT_COPY_LOCK = new java.lang.Object();
    protected AGElementCalcDesc mCalcDescriptor;
    private int mScrollX;
    private int mScrollY;

    /**
     * Workaround for abstract classes to initialize new mInstance,
     * used in control copy process {@link #copy()}
     *
     * @return new Descriptor
     */
    public abstract AbstractAGElementDataDesc createInstance();

    /**
     * Adds new calc descriptor for dataDesc with given displayId
     *
     * @param calcDesc Descriptor to be stored
     */
    public void setCalcDesc(AGElementCalcDesc calcDesc) {
        mCalcDescriptor = calcDesc;
    }

    public AGElementCalcDesc getCalcDesc() {
        if (mCalcDescriptor == null)
            mCalcDescriptor = new AGElementCalcDesc();
        return mCalcDescriptor;
    }

    /**
     * Deletes calc desc for given display
     */
    public void removeCalcDesc() {
        mCalcDescriptor = null;
    }

    /**
     * Method for processing all Visitors, to allow them access every dataDescriptor
     *
     * @return in general false
     */
    public boolean accept(IDataDescVisitor visitor) {
        return acceptDepthFirst(visitor);
    }

    /**
     * Abstract method that allows process visitor functionality on class
     *
     * @param visitor Descriptor to be processed
     */
    protected abstract boolean acceptDepthFirst(IDataDescVisitor visitor);

    public abstract void resolveVariables();

    /**
     * Method allows to copy control to new mInstance of same class(same field values) with different references.
     * This method is thread safe in {@link AbstractAGElementDataDesc}.
     */
    public AbstractAGElementDataDesc copy() {

        synchronized (ELEMENT_COPY_LOCK) {

            AbstractAGElementDataDesc obj = createInstance();
            obj.setCalcDesc(this.getCalcDesc().createCalcDesc());

            return obj;
        }
    }

    public int getScrollX() {
        return mScrollX;
    }

    public void setScrollX(int scrollX) {
        mScrollX = scrollX;
    }

    public int getScrollY() {
        return mScrollY;
    }

    public void setScrollY(int scrollY) {
        mScrollY = scrollY;
    }
}
