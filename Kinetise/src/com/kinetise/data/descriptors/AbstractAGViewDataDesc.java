package com.kinetise.data.descriptors;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.actions.NullVariableDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.IDataDescVisitor;
import com.kinetise.data.descriptors.types.AGAlignType;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.descriptors.types.AGSizeModeType;
import com.kinetise.data.descriptors.types.AGVAlignType;
import com.kinetise.data.descriptors.types.SizeQuad;
import com.kinetise.data.systemdisplay.views.OnUpdateListener;

/**
 * Base for all views descriptors, contains most top notch properties that view has to relay on
 */
public abstract class AbstractAGViewDataDesc extends AbstractAGElementDataDesc {
    private String mId;
    private VariableDataDesc mOnClickActionDesc;
    private VariableDataDesc mOnChangeActionDesc;
    private VariableDataDesc mOnUpdateActionDesc;
    private AGAlignType mAlign;
    private AGVAlignType mVAlign;
    private int mBackgroundColor;
    private VariableDataDesc mBackground;
    private AGSizeModeType mBackgroundSizeMode;
    private AGSizeDesc mHeight;
    private AGSizeDesc mWidth;
    private int mBorderColor;
    private int mCurrentBorderColor;
    private SizeQuad mBorderQuad;
    private SizeQuad mMarginQuad;
    private AGSizeDesc mPaddingBottom;
    private AGSizeDesc mPaddingLeft;
    private AGSizeDesc mPaddingRight;
    private AGSizeDesc mPaddingTop;
    private AbstractAGViewDataDesc mParentContainer;
    private IAGCollectionDataDesc mSection;
    private AGSizeDesc mRadiusBottomRight;
    private AGSizeDesc mRadiusBottomLeft;
    private AGSizeDesc mRadiusTopRight;
    private AGSizeDesc mRadiusTopLeft;
    private DataFeedContext mDataFeedContext;
    private VariableDataDesc mRemovedAction;
    private VariableDataDesc mHiddenAction;
    private boolean mRemoved = false;
    private boolean mHidden = false;
    private OnUpdateListener mOnUpdateListener;

    public AbstractAGViewDataDesc(String id) {
        super();
        mId = id;
        mBorderQuad = new SizeQuad();
        mMarginQuad = new SizeQuad();
    }

    public int getTemplateNumber() {
        if (mDataFeedContext == null)
            return 0;
        return mDataFeedContext.getTemplateNumber();
    }

    public VariableDataDesc getOnClickActionDesc() {
        return mOnClickActionDesc;
    }

    public void setOnClickActionDesc(VariableDataDesc action) {
        this.mOnClickActionDesc = action;
    }

    public VariableDataDesc getOnChangeActionDesc() {
        return mOnChangeActionDesc;
    }

    public void setOnChangeActionDesc(VariableDataDesc action) {
        this.mOnChangeActionDesc = action;
    }

    public VariableDataDesc getOnUpdateActionDesc() {
        return mOnUpdateActionDesc;
    }

    public void setOnUpdateActionDesc(VariableDataDesc onUpdateActionDesc) {
        mOnUpdateActionDesc = onUpdateActionDesc;
    }

    /**
     * If controls parent is mInstance of {@link com.kinetise.data.descriptors.AbstractAGContainerDataDesc} then its parent innerAlign is returned.
     * Otherwise align of control is returned.
     *
     * @return Applicable align for control
     */
    public AGAlignType getAlign() {
        if (mParentContainer != null && mParentContainer instanceof AbstractAGContainerDataDesc) {
            AGAlignType parentAlign = ((AbstractAGContainerDataDesc) mParentContainer).getInnerAlign();
            if (parentAlign != null) {
                return parentAlign;
            }
        }
        return mAlign;
    }

    public void setAlign(AGAlignType align) {
        this.mAlign = align;
    }

    public VariableDataDesc getBackground() {
        return mBackground;
    }

    public void setBackground(VariableDataDesc background) {
        this.mBackground = background;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int color) {
        this.mBackgroundColor = color;
    }

    public SizeQuad getBorder() {
        return mBorderQuad;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int color) {
        this.mBorderColor = color;
        this.mCurrentBorderColor = color;
    }

    public int getCurrentBorderColor() {
        return mCurrentBorderColor;
    }

    public void setCurrentBorderColor(int currentBorderColor) {
        mCurrentBorderColor = currentBorderColor;
    }

    public AGSizeDesc getHeight() {
        return mHeight;
    }

    public void setHeight(AGSizeDesc height) {
        this.mHeight = height;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public SizeQuad getMargin() {
        return mMarginQuad;
    }

    public AGSizeDesc getPaddingBottom() {
        return mPaddingBottom;
    }

    public void setPaddingBottom(AGSizeDesc padding) {
        this.mPaddingBottom = padding;
    }

    public AGSizeDesc getPaddingLeft() {
        return mPaddingLeft;
    }

    public void setPaddingLeft(AGSizeDesc padding) {
        this.mPaddingLeft = padding;
    }

    public AGSizeDesc getPaddingRight() {
        return mPaddingRight;
    }

    public void setPaddingRight(AGSizeDesc padding) {
        this.mPaddingRight = padding;
    }

    public AGSizeDesc getPaddingTop() {
        return mPaddingTop;
    }

    public void setPaddingTop(AGSizeDesc padding) {
        this.mPaddingTop = padding;
    }

    public AbstractAGViewDataDesc getParentContainer() {
        return mParentContainer;
    }

    public void setParentContainer(AbstractAGViewDataDesc parent) {
        this.mParentContainer = parent;
    }

    public IAGCollectionDataDesc getSection() {
        return mSection;
    }

    public void setSection(IAGCollectionDataDesc section) {
        this.mSection = section;
    }

    /**
     * If controls parent is mInstance of {@link com.kinetise.data.descriptors.AbstractAGContainerDataDesc} then its parent inner vertical align is returned.
     * Otherwise vertical align of control is returned.
     *
     * @return Applicable vertical align for control
     */
    public AGVAlignType getVAlign() {
        if (mParentContainer != null && mParentContainer instanceof AbstractAGContainerDataDesc) {
            AGVAlignType parentVAlign = ((AbstractAGContainerDataDesc) mParentContainer).getInnerVAlign();
            if (parentVAlign != null) {
                return parentVAlign;
            }
        }
        return mVAlign;
    }

    public void setVAlign(AGVAlignType vAlign) {
        this.mVAlign = vAlign;
    }

    public AGSizeDesc getWidth() {
        return mWidth;
    }

    public void setWidth(AGSizeDesc width) {
        this.mWidth = width;
    }

    public AGSizeDesc getRadiusBottomRight() {
        return mRadiusBottomRight;
    }

    public void setRadiusBottomRight(AGSizeDesc agSizeDesc) {
        this.mRadiusBottomRight = agSizeDesc;
    }

    public AGSizeDesc getRadiusBottomLeft() {
        return mRadiusBottomLeft;
    }

    public void setRadiusBottomLeft(AGSizeDesc agSizeDesc) {
        this.mRadiusBottomLeft = agSizeDesc;
    }

    public AGSizeDesc getRadiusTopRight() {
        return mRadiusTopRight;
    }

    public void setRadiusTopRight(AGSizeDesc agSizeDesc) {
        this.mRadiusTopRight = agSizeDesc;
    }

    public AGSizeDesc getRadiusTopLeft() {
        return mRadiusTopLeft;
    }

    public void setRadiusTopLeft(AGSizeDesc agSizeDesc) {
        this.mRadiusTopLeft = agSizeDesc;
    }

    public int getFeedItemIndex() {
        if (mDataFeedContext == null)
            return -1;
        return mDataFeedContext.getFeedItemIndex();
    }

    public void setFeedItemIndex(int feedItemIndex) {
        if (mDataFeedContext == null)
            mDataFeedContext = new DataFeedContext();
        mDataFeedContext.setFeedItemIndex(feedItemIndex);
    }

    public void onChange() {
        VariableDataDesc action = getOnChangeActionDesc();
        if (action != null && !(action instanceof NullVariableDataDesc)) {
            action.resolveVariable();
        }
    }

    public void onUpdate() {
        VariableDataDesc action = getOnUpdateActionDesc();
        if (action != null && !(action instanceof NullVariableDataDesc)) {
            action.resolveVariable();
        }
    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        mOnUpdateListener = onUpdateListener;
    }

    public OnUpdateListener getOnUpdateListener() {
        return mOnUpdateListener;
    }

    @Override
    public void resolveVariables() {
        if (mBackground != null)
            mBackground.resolveVariable();
        if (mRemovedAction != null) {
            mRemovedAction.resolveVariable();
            setRemoved(Boolean.parseBoolean(mRemovedAction.getStringValue()));
        }
        if (mHiddenAction != null) {
            mHiddenAction.resolveVariable();
            setHidden(Boolean.parseBoolean(mHiddenAction.getStringValue()));
        }
        onUpdate();
    }

    @Override
    protected boolean acceptDepthFirst(IDataDescVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public AGViewCalcDesc getCalcDesc() {
        if (mCalcDescriptor == null)
            mCalcDescriptor = new AGViewCalcDesc();
        return (AGViewCalcDesc) mCalcDescriptor;
    }

    @Override
    public AbstractAGViewDataDesc copy() {
        AbstractAGViewDataDesc copied = (AbstractAGViewDataDesc) super.copy();
        copied.mId = String.valueOf(mId);
        if (mOnClickActionDesc != null) {
            copied.mOnClickActionDesc = mOnClickActionDesc.copy(copied);
        }
        if (mOnChangeActionDesc != null) {
            copied.mOnChangeActionDesc = mOnChangeActionDesc.copy(copied);
        }
        if (mAlign != null) {
            copied.mAlign = mAlign;
        }
        if (mVAlign != null) {
            copied.mVAlign = mVAlign;
        }
        if (mBackground != null) {
            copied.mBackground = this.mBackground.copy(copied);
        }
        copied.mBackgroundColor = this.mBackgroundColor;
        copied.getBorder().copyFrom(getBorder());
        copied.getMargin().copyFrom(getMargin());
        copied.mBorderColor = this.mBorderColor;
        copied.mCurrentBorderColor = this.mCurrentBorderColor;
        if (this.mHeight != null) {
            copied.mHeight = new AGSizeDesc(this.mHeight.getDescValue(), (mHeight.getDescUnit()));
        }
        if (this.mWidth != null) {
            copied.mWidth = new AGSizeDesc(this.mWidth.getDescValue(), (this.mWidth.getDescUnit()));
        }

        if (this.mPaddingBottom != null) {
            copied.mPaddingBottom = new AGSizeDesc(this.mPaddingBottom.getDescValue(), (this.mPaddingBottom.getDescUnit()));
        }
        if (this.mPaddingLeft != null) {
            copied.mPaddingLeft = new AGSizeDesc(this.mPaddingLeft.getDescValue(), (this.mPaddingLeft.getDescUnit()));
        }
        if (this.mPaddingRight != null) {
            copied.mPaddingRight = new AGSizeDesc(this.mPaddingRight.getDescValue(), (this.mPaddingRight.getDescUnit()));
        }
        if (this.mPaddingTop != null) {
            copied.mPaddingTop = new AGSizeDesc(this.mPaddingTop.getDescValue(), (this.mPaddingTop.getDescUnit()));
        }
        if (mRadiusBottomRight != null) {
            copied.mRadiusBottomRight = new AGSizeDesc(this.mRadiusBottomRight.getDescValue(), (this.mRadiusBottomRight.getDescUnit()));
        }
        if (mRadiusBottomLeft != null) {
            copied.mRadiusBottomLeft = new AGSizeDesc(this.mRadiusBottomLeft.getDescValue(), (this.mRadiusBottomLeft.getDescUnit()));
        }
        if (this.mRadiusTopRight != null) {
            copied.mRadiusTopRight = new AGSizeDesc(this.mRadiusTopRight.getDescValue(), (this.mRadiusTopRight.getDescUnit()));
        }
        if (this.mRadiusTopLeft != null) {
            copied.mRadiusTopLeft = new AGSizeDesc(this.mRadiusTopLeft.getDescValue(), (this.mRadiusTopLeft.getDescUnit()));
        }
        if (mDataFeedContext != null) {
            copied.mDataFeedContext = mDataFeedContext.copy();
        }
        if (mBackgroundSizeMode != null) {
            copied.setBackgroundSizeMode(mBackgroundSizeMode);
        }
        if (mRemovedAction != null)
            copied.mRemovedAction = mRemovedAction.copy(copied);
        if (mHiddenAction != null)
            copied.mHiddenAction = mHiddenAction.copy(copied);
        return copied;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (o instanceof AbstractAGViewDataDesc) {
            if (mId.equals(((AbstractAGViewDataDesc) o).mId)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return String.format("{%s [id: %s]}", getClass().getName(), getId());
    }

    public void setDataFeedContext(DataFeedContext dataFeedContext) {
        mDataFeedContext = dataFeedContext;
    }

    public DataFeedContext getDataFeedContext() {
        return mDataFeedContext;
    }

    public void setBackgroundSizeMode(AGSizeModeType backgroundSizeMode) {
        mBackgroundSizeMode = backgroundSizeMode;
    }

    public AGSizeModeType getBackgroundSizeMode() {
        return mBackgroundSizeMode;
    }

    public boolean isRemoved() {
        return mRemoved;
    }

    public void setRemoved(boolean removed) {
        mRemoved = removed;
    }

    public boolean isHidden() {
        return mHidden;
    }

    public void setHidden(boolean hidden) {
        mHidden = hidden;
    }

    public void setRemovedAction(VariableDataDesc removedAction) {
        mRemovedAction = removedAction;
    }

    public void setHiddenAction(VariableDataDesc hiddenAction) {
        mHiddenAction = hiddenAction;
    }

    public String getFeedBaseAdress() {
        if (getDataFeedContext() != null && getDataFeedContext().isInDataFeed()) {
            return getDataFeedContext().getFeedBaseAdress();
        }
        if (AGApplicationState.getInstance().getApplicationState() != null) {
            AbstractAGElementDataDesc screenContext = AGApplicationState.getInstance().getApplicationState().getContext();
            if (screenContext instanceof IFeedClient)
                return ((IFeedClient) screenContext).getResolvedUrl();
        }
        return "";
    }
}
