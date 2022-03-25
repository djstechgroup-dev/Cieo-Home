package com.kinetise.data.descriptors;

import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.FindDescendantsByTypeVisitor;
import com.kinetise.data.descriptors.desctriptorvisitors.IDataDescVisitor;
import com.kinetise.data.descriptors.types.AGOrientationType;

import java.util.ArrayList;

public class AGScreenDataDesc extends AbstractAGViewDataDesc {

    private String mNextScreenId;
    private AGBodyDataDesc mScreenBody;
    private AGHeaderDataDesc mScreenHeader;
    private AGNaviPanelDataDesc mScreenNaviPanel;
    private boolean mPullToRefresh;
    private String mBackgroundVideoName = null;
    private boolean mProtected;
    private String mAnaliticsTag = "";
    private AGOrientationType mOrientationType = AGOrientationType.BOTH;
    private VariableDataDesc mOnScreenExitAction;
    private VariableDataDesc mOnScreenEnterAction;
    private ArrayList<IFeedClient> mFeedClients;
    private int mStatusBarColor;
    private boolean mStatusBarColorInvert;

    public int getStatusBarColor() {
        return mStatusBarColor;
    }

    public void setStatusBarColor(int statusBarColor) {
        this.mStatusBarColor = statusBarColor;
    }

    public boolean isStatusBarColorInvert() {
        return mStatusBarColorInvert;
    }

    public void setStatusBarColorInvert(boolean statusBarMode) {
        this.mStatusBarColorInvert = statusBarMode;
    }

    public AGScreenDataDesc(String id) {
        super(id);
    }

    public String getNextScreenId() {
        return mNextScreenId;
    }

    public AGBodyDataDesc getScreenBody() {
        return mScreenBody;
    }

    public AGHeaderDataDesc getScreenHeader() {
        return mScreenHeader;
    }

    public String getScreenId() {
        return getId();
    }

    public AGNaviPanelDataDesc getScreenNaviPanel() {
        return mScreenNaviPanel;
    }

    public String getBackgroundVideoName() {
        return mBackgroundVideoName;
    }

    public void setBackgroundVideoName(String backgroundVideoName) {
        mBackgroundVideoName = backgroundVideoName;
    }

    public void setNextScreenId(String nextScreenId) {
        mNextScreenId = nextScreenId;
    }

    public void setScreenBody(AGBodyDataDesc body) {
        mScreenBody = body;
        mScreenBody.setScreenDesc(this);
    }

    public void setScreenHeader(AGHeaderDataDesc header) {
        mScreenHeader = header;
        mScreenHeader.setScreenDesc(this);
    }

    public void setScreenNaviPanel(AGNaviPanelDataDesc naviPanel) {
        mScreenNaviPanel = naviPanel;
        mScreenNaviPanel.setScreenDesc(this);
    }

    public boolean isProtected() {
        return mProtected;
    }

    public void setProtected(boolean aProtected) {
        mProtected = aProtected;
    }

    /**
     * Used to find all feed clients existing on the screen to allow some optimizations
     */

    public void updateFeeds() {
        FindDescendantsByTypeVisitor findByTypeVisitor = new FindDescendantsByTypeVisitor<>(IFeedClient.class);
        this.accept(findByTypeVisitor);
        synchronized (AGScreenDataDesc.this) {
            mFeedClients = findByTypeVisitor.getFoundDataDescriptors();
        }
    }

    public void clearFeedClients() {
        synchronized (AGScreenDataDesc.this) {
            mFeedClients.clear();
        }
    }

    public ArrayList<IFeedClient> getFeedClients() {
        return mFeedClients;
    }

    public IFeedClient getFeedClient(String value) {
        synchronized (AGScreenDataDesc.this) {
            for (IFeedClient feedClient : mFeedClients) {
                if (feedClient.getId().equals(value))
                    return feedClient;
            }
        }
        return null;
    }


    @Override
    protected boolean acceptDepthFirst(IDataDescVisitor visitor) {

        if (visitor.visit(this)) {
            return true;
        }

        if (mScreenHeader != null && mScreenHeader.acceptDepthFirst(visitor)) {
            return true;
        }
        if (mScreenBody != null && mScreenBody.acceptDepthFirst(visitor)) {
            return true;
        }
        return mScreenNaviPanel != null && mScreenNaviPanel.acceptDepthFirst(visitor);

    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mScreenBody.resolveVariables();

        if (mScreenHeader != null)
            mScreenHeader.resolveVariables();

        if (mScreenNaviPanel != null)
            mScreenNaviPanel.resolveVariables();
    }

    @Override
    public AGScreenDataDesc copy() {
        AGScreenDataDesc copy = (AGScreenDataDesc) super.copy();

        copy.mNextScreenId = mNextScreenId;
        copy.mBackgroundVideoName = mBackgroundVideoName;
        copy.mAnaliticsTag = mAnaliticsTag;
        if (mScreenBody != null)
            copy.setScreenBody(mScreenBody.copy());

        if (mScreenHeader != null)
            copy.setScreenHeader(mScreenHeader.copy());
        if (mScreenNaviPanel != null)
            copy.setScreenNaviPanel(mScreenNaviPanel.copy());

        if (mOrientationType != null)
            copy.setOrientation(mOrientationType);

        copy.setPullToRefresh(this.mPullToRefresh);

        copy.setProtected(this.mProtected);

        copy.mOnScreenEnterAction = mOnScreenEnterAction.copy(copy);
        copy.mOnScreenExitAction = mOnScreenExitAction.copy(copy);
        copy.mStatusBarColor = mStatusBarColor;
        copy.mStatusBarColorInvert = mStatusBarColorInvert;
        return copy;
    }

    @Override
    public AGScreenDataDesc createInstance() {
        return new AGScreenDataDesc(getId());
    }

    @Override
    public String toString() {
        return String.format("{%s [id: %s], [hash: %d]}", getClass().getName(), getScreenId(), hashCode());
    }

    public void setOrientation(AGOrientationType orientationType) {
        mOrientationType = orientationType;
    }

    public String getAnalitycsTag() {
        return mAnaliticsTag;
    }

    public void setAnalitycsTag(String tag) {
        mAnaliticsTag = tag;
    }

    public AGOrientationType getOrientationType() {
        return mOrientationType;
    }

    public boolean isPullToRefresh() {
        return mPullToRefresh;
    }

    public void setPullToRefresh(boolean pPullToRefresh) {
        mPullToRefresh = pPullToRefresh;
    }

    @Override
    public AGViewCalcDesc getCalcDesc() {
        if (mCalcDescriptor == null) {
            mCalcDescriptor = new AGViewCalcDesc();
        }
        return (AGViewCalcDesc) mCalcDescriptor;
    }

    public VariableDataDesc getOnScreenExitAction() {
        return mOnScreenExitAction;
    }

    public void setOnScreenExitAction(VariableDataDesc onScreenExitAction) {
        mOnScreenExitAction = onScreenExitAction;
    }

    public VariableDataDesc getOnScreenEnterAction() {
        return mOnScreenEnterAction;
    }

    public void setOnScreenEnterAction(VariableDataDesc onScreenEnterAction) {
        mOnScreenEnterAction = onScreenEnterAction;
    }

}
