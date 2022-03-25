package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.types.InitCameraModeType;

public class AGMapDataDesc extends AbstractAGDataFeedViewDataDesc {

    private String mLongtitudeNodeName;
    private String mLatitudeNodeName;
    private VariableDataDesc mPinImageAdress;
    private InitCameraModeType mInitCameraMode;
    private boolean mMyLocationEnabled = false;
    private int mInitMinRadius;
    private boolean mShowMapPopup;

    public AGMapDataDesc(String id) {
        super(id);
        setShowMapPopup(true);
    }

    @Override
    public AGMapDataDesc createInstance() {
        return new AGMapDataDesc(getId());
    }

    @Override
    public void resetFeed() {
        super.resetFeed();
        mPageIndex = 1;
    }

    public String getLatitudeNodeName() {
        return mLatitudeNodeName;
    }

    public void setLatitudeNodeName(String latitudeNodeName) {
        mLatitudeNodeName = latitudeNodeName;
    }

    public String getLongtitudeNodeName() {
        return mLongtitudeNodeName;
    }

    public void setLongtitudeNodeName(String longtitudeNodeName) {
        mLongtitudeNodeName = longtitudeNodeName;
    }

    public VariableDataDesc getPinImageAdress() {
        return mPinImageAdress;
    }

    public void setPinImageAdress(VariableDataDesc pinImageAdress) {
        mPinImageAdress = pinImageAdress;
    }

    @Override
    public AbstractAGDataFeedViewDataDesc copy() {
        AGMapDataDesc copied = (AGMapDataDesc) super.copy();
        copied.setPinImageAdress(mPinImageAdress.copy(copied));
        copied.setLongtitudeNodeName(mLongtitudeNodeName);
        copied.setLatitudeNodeName(mLatitudeNodeName);
        copied.setInitCameraMode(mInitCameraMode);
        copied.setMyLocationEnabled(mMyLocationEnabled);
        copied.setShowMapPopup(mShowMapPopup);
        return copied;
    }

    public InitCameraModeType getInitCameraMode() {
        return mInitCameraMode;
    }

    public int getInitMinRadius() {
        return mInitMinRadius;
    }


    public void setInitCameraMode(InitCameraModeType initCameraMode) {
        mInitCameraMode = initCameraMode;
    }

    public void setInitMinRadius(int initMinRadius) {
        mInitMinRadius = initMinRadius;
    }

    public boolean isMyLocationEnabled() {
        return mMyLocationEnabled;
    }

    public void setMyLocationEnabled(boolean myLocationEnabled) {
        mMyLocationEnabled = myLocationEnabled;
    }

    public boolean isShowMapPopup() {
        return mShowMapPopup;
    }

    public void setShowMapPopup(boolean showMapPopup) {
        mShowMapPopup = showMapPopup;
    }
}
