package com.kinetise.data.sourcemanager;

import android.graphics.Bitmap;

import com.kinetise.data.descriptors.datadescriptors.components.ImageDescriptor;
import com.kinetise.data.systemdisplay.bitmapsettercommands.BitmapSetterCommand;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageChangeListener;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageSetterCommandCallback;

import java.util.ArrayList;
import java.util.List;

public class ImageSource implements ImageSetterCommandCallback {
    private ImageDescriptor mImageDescriptor;
    Bitmap mBitmap;
    private String mCurrentImageSource;
    private boolean imageLoaded;
    private BitmapSetterCommand downloadCommand;
    private List<ImageChangeListener> mImageChangeListeners = new ArrayList<>();
    private LoadingStartedListener mLoadingStartedListener;


    public ImageSource(ImageDescriptor imageDescriptor, ImageChangeListener callback) {
        addImageChangeListener(callback);
        mImageDescriptor = imageDescriptor;
        imageLoaded = false;
        mLoadingStartedListener = null;
    }

    public ImageSource(ImageDescriptor imageDescriptor, ImageChangeListener callback, LoadingStartedListener loadingStartedCallback){
        this(imageDescriptor,callback);
        mLoadingStartedListener = loadingStartedCallback;
    }

    public boolean isImageLoaded() {
        return imageLoaded;
    }


    public void refresh(String baseUrl, double width ,double height) {
        refresh(baseUrl,(int)(Math.round(width)),(int)(Math.round(height)));
    }

    public void refresh(String baseUrl, int width ,int height){
        String source = mImageDescriptor.getImageSource();
        if (source!=null && (mCurrentImageSource==null || !source.equals(mCurrentImageSource))) {
            imageLoaded = false;
            cancelDownload();
            downloadCommand = new BitmapSetterCommand(baseUrl, mImageDescriptor, this, width, height);
            AssetsManager.getInstance().getAsset(downloadCommand, AssetsManager.ResultType.IMAGE, mImageDescriptor.getHeaders(), mImageDescriptor.getHttpParams(),null);
            mCurrentImageSource = source;
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void cancelDownload() {
        if(downloadCommand != null)
            downloadCommand.cancel();
    }

    @Override
    public void loadingStarted() {
        if(mLoadingStartedListener != null){
            mLoadingStartedListener.loadingStarted();
        }
    }

    @Override
    public void setImageSrc(Bitmap bitmap) {
        imageLoaded = true;
        mBitmap = bitmap;
        for (ImageChangeListener listener : mImageChangeListeners) {
            listener.onImageChanged(bitmap);
        }
    }

    public void setImageDescriptor(ImageDescriptor imageDescriptor) {
        mImageDescriptor = imageDescriptor;
    }

    public void setLoadingStartedListener(LoadingStartedListener loadingStartedListener) {
        mLoadingStartedListener = loadingStartedListener;
    }

    public void addImageChangeListener(ImageChangeListener imageChangeListener) {
        if (imageChangeListener != null) {
            mImageChangeListeners.add(imageChangeListener);
        }
    }

    public static class LoadingStartedListener{
    public void loadingStarted(){

    }
}
}

