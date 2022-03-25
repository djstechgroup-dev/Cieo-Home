package com.kinetise.data.descriptors;

import com.kinetise.data.descriptors.calcdescriptors.AGContainerCalcDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.IDataDescVisitor;
import com.kinetise.data.descriptors.types.AGAlignType;
import com.kinetise.data.descriptors.types.AGLayoutType;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.descriptors.types.AGVAlignType;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAGContainerDataDesc extends AbstractAGViewDataDesc implements IAGCollectionDataDesc {

    private ArrayList<AbstractAGElementDataDesc> mControls = new ArrayList<>();
    private List<AbstractAGElementDataDesc> mPresentControls = new ArrayList<>();
    private AGLayoutType mLayoutType;
    private int mSeparatorColor;
    private AGSizeDesc mItemSeparation;
    private AGSizeDesc mItemBorder;
    private AGSizeDesc mItemBorderMarginStart;
    private AGSizeDesc mItemBorderMarginEnd;
    private AGAlignType mInnerAlign;
    private AGVAlignType mInnerVAlign;
    private boolean mScrollVertical;
    private boolean mScrollHorizontal;
    private boolean mInverted = false;

    public int getSeparatorColor() {
        return mSeparatorColor;
    }

    public void setSeparatorColor(int color) {
        mSeparatorColor = color;
    }

    @Override
    public int getDepthCount() {
        return getParent().getDepthCount() + 1;
    }

    public AbstractAGContainerDataDesc(String id, AGLayoutType layoutType) {
        super(id);

        mLayoutType = layoutType;
    }

    public AGSizeDesc getItemSeparation() {
        return mItemSeparation;
    }

    public void setItemSeparation(AGSizeDesc border) {
        this.mItemSeparation = border;
    }

    public void setItemBorder(AGSizeDesc itemBorder) {
        mItemBorder = itemBorder;
    }

    public AGSizeDesc getItemBorder() {
        return mItemBorder;
    }

    public AGSizeDesc getItemBorderMarginStart() {
        return mItemBorderMarginStart;
    }

    public void setItemBorderMarginStart(AGSizeDesc itemBorderMarginStart) {
        mItemBorderMarginStart = itemBorderMarginStart;
    }

    public AGSizeDesc getItemBorderMarginEnd() {
        return mItemBorderMarginEnd;
    }

    public void setItemBorderMarginEnd(AGSizeDesc itemBorderMarginEnd) {
        mItemBorderMarginEnd = itemBorderMarginEnd;
    }

    public AGLayoutType getLayout() {
        return mLayoutType;
    }

    public void setLayout(AGLayoutType layout) {
        this.mLayoutType = layout;
    }

    public boolean isScrollHorizontal() {
        return mScrollHorizontal;
    }

    public void setScrollHorizontal(boolean isHorizontal) {
        this.mScrollHorizontal = isHorizontal;
    }

    public AGAlignType getInnerAlign() {
        return mInnerAlign;
    }

    public void setInnerAlign(AGAlignType alignType) {
        mInnerAlign = alignType;
    }

    public boolean isScrollVertical() {
        return mScrollVertical;
    }

    public void setScrollVertical(boolean isVertical) {
        this.mScrollVertical = isVertical;
    }

    public AGVAlignType getInnerVAlign() {
        return mInnerVAlign;
    }

    public void setInnerVAlign(AGVAlignType vAlignType) {
        mInnerVAlign = vAlignType;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mPresentControls.clear();
        for (AbstractAGElementDataDesc desc : mControls) {
            desc.resolveVariables();
            AbstractAGViewDataDesc view = (AbstractAGViewDataDesc) desc;
            if (!view.isRemoved()) {
                mPresentControls.add(desc);
            }
        }
    }

    /**
     * Modified version of {@link AbstractAGElementDataDesc#acceptDepthFirst(IDataDescVisitor)}
     * to allow visiting in order: container itself and then its children
     *
     * @param visitor Visitor that want to traverse hierarchy of current descriptor
     * @return True if visitor found searched value
     */
    @Override
    protected boolean acceptDepthFirst(IDataDescVisitor visitor) {

        if (visitor.visit(this)) {
            return true;
        }

        for (AbstractAGElementDataDesc desc : getAllControls()) {
            if (desc != null && desc.acceptDepthFirst(visitor)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Allow obtain parent descriptor of current one. If no container is a parent of one its section is returned.
     * eg.
     * <ul>
     * <li>body</li>
     * <li>container1</li> getParent() returns:#body
     * <li>container2</li> getParent() returns:#container1
     * </ul>
     *
     * @return Parent descriptor in hierarchy
     */
    @Override
    public AbstractAGElementDataDesc getParent() {
        AbstractAGElementDataDesc parentDesc = getParentContainer();
        return parentDesc != null ? parentDesc : (AbstractAGElementDataDesc) getSection();
    }

    @Override
    public AGContainerCalcDesc getCalcDesc() {

        if (mCalcDescriptor == null)
            mCalcDescriptor = new AGContainerCalcDesc();

        return (AGContainerCalcDesc) mCalcDescriptor;
    }

    /**
     * Setter for Section - describes where in screen hierarchy
     * (body,navipanel or header)
     * is located current control/container. Besides setting section on current container this method also sets
     * section param for all of its controls.
     *
     * @param section One of 3 sections on screen
     */
    @Override
    public void setSection(IAGCollectionDataDesc section) {
        super.setSection(section);

        for (AbstractAGElementDataDesc control : getAllControls()) {
            ((AbstractAGViewDataDesc) control).setSection(section);
        }
    }

    /**
     * Adds control to hierarchy of container adding it(container) as control parent
     * and updating control information about section its located in.
     */
    @Override
    public void addControl(AbstractAGElementDataDesc control) {
        mControls.add(control);
        ((AbstractAGViewDataDesc) control).setParentContainer(this);
        if (!((AbstractAGViewDataDesc) control).isRemoved())
            mPresentControls.add(control);

        IAGCollectionDataDesc section = getSection();
        ((AbstractAGViewDataDesc) control).setSection(section);
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
    public AbstractAGContainerDataDesc copy() {
        AbstractAGContainerDataDesc copied = (AbstractAGContainerDataDesc) super.copy();

        copyParameters(copied);


        for (AbstractAGElementDataDesc childDesc : getAllControls()) {
            AbstractAGElementDataDesc copiedChildDesc = childDesc.copy();
            copied.addControl(copiedChildDesc);
        }

        return copied;
    }

    public AbstractAGContainerDataDesc copyWithoutCopyingChildren() {
        AbstractAGContainerDataDesc copied = (AbstractAGContainerDataDesc) super.copy();
        copyParameters(copied);

        copied.mControls = this.mControls;
        copied.mPresentControls = this.mPresentControls;

        return copied;
    }


    public void copyParameters(AbstractAGContainerDataDesc copied) {
        if (mLayoutType != null) {
            copied.mLayoutType = mLayoutType;
        }
        if (mItemSeparation != null) {
            copied.mItemSeparation = mItemSeparation.copy();
        }

        copied.setSeparatorColor(getSeparatorColor());
        if (getItemBorder() != null)
            copied.setItemBorder(getItemBorder().copy());
        if (getItemBorderMarginEnd() != null)
            copied.setItemBorderMarginStart(getItemBorderMarginStart().copy());
        if (getItemBorderMarginEnd() != null)
            copied.setItemBorderMarginEnd(getItemBorderMarginEnd().copy());

        if (mInnerAlign != null) {
            copied.mInnerAlign = mInnerAlign;
        }
        if (mInnerVAlign != null) {
            copied.mInnerVAlign = mInnerVAlign;
        }
        copied.mScrollVertical = this.mScrollVertical;
        copied.mScrollHorizontal = this.mScrollHorizontal;

        copied.setInverted(mInverted);
    }

    public boolean isInverted() {
        return mInverted;
    }

    public void setInverted(boolean inverted) {
        mInverted = inverted;
    }
}
