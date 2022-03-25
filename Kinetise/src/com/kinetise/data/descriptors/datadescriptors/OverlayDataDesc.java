package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;

public class OverlayDataDesc {
    private String mId;
    private OverlayAnimationType mAnimationType;
    private boolean mMoveScreen;
    private boolean mMoveOverlay;
    private boolean mGrayoutBackground;
    private AbstractAGViewDataDesc mMainViewDesc;
    private VariableDataDesc mOnOverlayExitAction;
    private VariableDataDesc mOnOverlayEnterAction;

    public VariableDataDesc getOnOverlayExitAction() {
        return mOnOverlayExitAction;
    }

    public void setOnOverlayExitAction(VariableDataDesc onOverlayExitAction) {
        this.mOnOverlayExitAction = onOverlayExitAction;
    }

    public VariableDataDesc getOnOverlayEnterAction() {
        return mOnOverlayEnterAction;
    }

    public void setOnOverlayEnterAction(VariableDataDesc onOverlayEnterAction) {
        this.mOnOverlayEnterAction = onOverlayEnterAction;
    }

    public OverlayDataDesc(String id) {
        mId = id;
    }

    public boolean isMoveScreen() {
        return mMoveScreen;
    }

    public void setMoveScreen(boolean moveScreen) {
        mMoveScreen = moveScreen;
    }

    public boolean isMoveOverlay() {
        return mMoveOverlay;
    }

    public void setMoveOverlay(boolean moveOverlay) {
        mMoveOverlay = moveOverlay;
    }

    public boolean isGrayoutBackground() {
        return mGrayoutBackground;
    }

    public void setGrayoutBackground(boolean grayoutBackground) {
        mGrayoutBackground = grayoutBackground;
    }

    public AbstractAGViewDataDesc getMainViewDesc() {
        return mMainViewDesc;
    }

    public void setMainViewDesc(AbstractAGViewDataDesc mainViewDesc) {
        mMainViewDesc = mainViewDesc;
    }

    public String getId() {
        return mId;
    }

    public OverlayAnimationType getAnimationType() {
        return mAnimationType;
    }

    public void setAnimationType(OverlayAnimationType animationType) {
        mAnimationType = animationType;
    }

}
