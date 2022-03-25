package com.kinetise.data.commands;

import android.graphics.Bitmap;

import com.kinetise.data.descriptors.datadescriptors.components.ImageDescriptor;
import com.kinetise.data.sourcemanager.AbstractGetSourceCommand;
import com.kinetise.helpers.asynccaller.AsyncCaller;

public class NativeShareGetBitmapCommand extends AbstractGetSourceCommand<Bitmap> {
    public static final int BITMAP_WIDTH = 1024;
    public static final int BITMAP_HEIGHT = 1024;
    protected boolean mCancelled;

    protected NativeShareCommandCallback mCallback;
    protected ImageDescriptor mImageDescriptor;

    public NativeShareGetBitmapCommand(String baseUrl, ImageDescriptor imageDescriptor, NativeShareCommandCallback callback) {
        super(baseUrl, imageDescriptor.getImageSource());
        mImageDescriptor = imageDescriptor;
        mCallback = callback;
    }

    @Override
    public void postGetSource(final Bitmap obj) {
        clearAssociatedTask();
        if (!mCancelled) {
            AsyncCaller.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCallback != null) {
                        mCallback.onDownloadSuccess(obj);
                    }
                }
            });
        }
    }

    @Override
    public void onError() {
        clearAssociatedTask();
        if (!mCancelled) {
            mCancelled = true;
            AsyncCaller.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCallback != null) {
                        mCallback.onDownloadError();
                    }
                }
            });
        }
    }

    @Override
    public Object[] getParams() {
        return new Object[]{BITMAP_WIDTH, BITMAP_HEIGHT};
    }

    public interface NativeShareCommandCallback{
        void onDownloadError();
        void onDownloadSuccess(Bitmap b);
    }

    @Override
    public void cancel() {
        super.cancel();
        mCancelled = true;
        mCallback = null;
    }
}
