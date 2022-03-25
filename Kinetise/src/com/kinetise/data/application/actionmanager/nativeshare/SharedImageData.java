package com.kinetise.data.application.actionmanager.nativeshare;

import android.net.Uri;

import com.kinetise.data.descriptors.datadescriptors.components.ImageDescriptor;
import com.kinetise.data.descriptors.types.AGSizeModeType;

public class SharedImageData{
    Uri mImageUri;
    String mImageSource;
    DownloadStatus mStatus;


    public SharedImageData(String imageSource){
        mImageSource = imageSource;
        mStatus = DownloadStatus.WAITING;
    }

    public enum DownloadStatus{
        DOWNLOADED, DOWNLOAD_ERROR, WAITING
    }

    public void setImageUri(Uri imageUri) {
        mImageUri = imageUri;
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    public String getImageSource() {
        return mImageSource;
    }

    public DownloadStatus getStatus() {
        return mStatus;
    }

    public void setStatus(DownloadStatus status) {
        mStatus = status;
    }

    public ImageDescriptor getAsImageDescriptor() {
        ImageDescriptor imageDescriptor = new ImageDescriptor();
        imageDescriptor.setSizeMode(AGSizeModeType.LONGEDGE);
        imageDescriptor.setImageSrc(mImageSource);
        return imageDescriptor;
    }
}