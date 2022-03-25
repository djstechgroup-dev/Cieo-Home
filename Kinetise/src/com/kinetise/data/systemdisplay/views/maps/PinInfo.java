package com.kinetise.data.systemdisplay.views.maps;

import android.graphics.Bitmap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Holds informations about map pin
 */
public class PinInfo  implements ClusterItem {
    private int mIndex;
    private LatLng mLatLng;
    private Bitmap mPinBitmap;
    private int mPinWidth;
    private int mPinHeight;
    private String mGUID;
    private String mImageAddress;

    public PinInfo() {
    }


    /**
     * 
     * @return index of feed element for which this pin is displayed
     */
    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public Bitmap getPinBitmap() {
        return mPinBitmap;
    }

    public void setPinBitmap(Bitmap bmp) {
        mPinBitmap = bmp;
    }

    /**
     * @return displayed pin width in pixels
     */
    public int getPinWidth() {
        return mPinWidth;
    }

    public void setPinWidth(int width) {
        mPinWidth = width;
    }

    /**
     * @return displayed pin Height in pixels
     */
    public int getPinHeight() {
        return mPinHeight;
    }

    public void setPinHeight(int height) {
        mPinHeight = height;
    }

    public void setLatLng(LatLng latLng) {mLatLng = latLng;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setGUID(String GUID) {
        mGUID = GUID;
    }

    public String getGUID() {
        return mGUID;
    }

    public void clearPinBitmap() {
        if (mPinBitmap != null) {
            mPinBitmap.recycle();
            mPinBitmap = null;
        }
    }

    public String getImageAddress() {
        return mImageAddress;
    }

    public void setImageAddress(String imageAddress) {
        mImageAddress = imageAddress;
    }

    @Override
    public LatLng getPosition() {
        return mLatLng;
    }
}