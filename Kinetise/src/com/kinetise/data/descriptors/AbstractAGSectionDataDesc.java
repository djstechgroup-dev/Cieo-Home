package com.kinetise.data.descriptors;

import com.kinetise.data.descriptors.calcdescriptors.AGSectionCalcDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.IDataDescVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all 3 sections:
 * <ul>
 * <li>Header</li>
 * <li>Body</li>
 * <li>Navipanel</li>
 * </ul>
 */
public abstract class AbstractAGSectionDataDesc extends AbstractAGElementDataDesc implements IAGCollectionDataDesc {
    private ArrayList<AbstractAGElementDataDesc> mControls = new ArrayList<AbstractAGElementDataDesc>();
    private List<AbstractAGElementDataDesc> mPresentControls = new ArrayList<AbstractAGElementDataDesc>();
    private AGScreenDataDesc mScreenDesc;
    private boolean mScrollVertical;

    public boolean isScrollVertical() {
        return mScrollVertical;
    }

    public void setScrollVertical(boolean scrollVertical) {
        mScrollVertical = scrollVertical;
    }

    public AbstractAGSectionDataDesc() {
        super();
        setScrollVertical(true);
    }

    public AGScreenDataDesc getScreenDesc() {
        return mScreenDesc;
    }

    public void setScreenDesc(AGScreenDataDesc desc) {
        mScreenDesc = desc;
    }

    @Override
    public AbstractAGElementDataDesc getParent() {
        return mScreenDesc;
    }

    /**
     * Section is top level part of screen so its depth is always 1
     */
    @Override
    public int getDepthCount() {
        return 1;
    }

    @Override
    public void addControl(AbstractAGElementDataDesc control) {
        AbstractAGViewDataDesc view = (AbstractAGViewDataDesc) control;
        view.setSection(this);
        mControls.add(control);
        if (!view.isRemoved())
            mPresentControls.add(control);
    }

    @Override
    public List<AbstractAGElementDataDesc> getAllControls() {
        return mControls;
    }

    @Override
    public List<AbstractAGElementDataDesc> getPresentControls() {
        return mPresentControls;
    }

    @Override
    public void removeAllControls() {
        mControls.clear();
        mPresentControls.clear();
    }

    @Override
    public void removeControl(AbstractAGElementDataDesc control) {
        mControls.remove(control);
        mPresentControls.remove(control);
    }

    @Override
    public AGSectionCalcDesc getCalcDesc() {

        if (mCalcDescriptor == null) {
            mCalcDescriptor = new AGSectionCalcDesc();
        }

        return (AGSectionCalcDesc) mCalcDescriptor;
    }

    @Override
    public void resolveVariables() {
        mPresentControls.clear();
        for (AbstractAGElementDataDesc desc : mControls) {
            desc.resolveVariables();
            AbstractAGViewDataDesc view = (AbstractAGViewDataDesc) desc;
            if (!view.isRemoved()) {
                mPresentControls.add(desc);
            }
        }
    }

    @Override
    public boolean acceptDepthFirst(IDataDescVisitor visitor) {

        if (visitor.visit(this)) {
            return true;
        }

        for (AbstractAGElementDataDesc desc : getAllControls()) {
            if (desc.acceptDepthFirst(visitor)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public AbstractAGSectionDataDesc copy() {
        AbstractAGSectionDataDesc copied = (AbstractAGSectionDataDesc) super.copy();
        copied.setCalcDesc(mCalcDescriptor.createCalcDesc());
        for (AbstractAGElementDataDesc desc : mControls) {
            copied.addControl(desc.copy());
        }

        return copied;
    }

    @Override
    public String toString() {
        return String.format("{%s}", getClass().getName());
    }
}
