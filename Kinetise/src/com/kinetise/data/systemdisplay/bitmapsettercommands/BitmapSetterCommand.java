package com.kinetise.data.systemdisplay.bitmapsettercommands;

import android.graphics.Bitmap;

import com.kinetise.data.descriptors.datadescriptors.components.ImageDescriptor;
import com.kinetise.data.packagemanager.AppPackageManager;
import com.kinetise.data.sourcemanager.AbstractGetSourceCommand;
import com.kinetise.helpers.asynccaller.AsyncCaller;
import com.kinetise.helpers.drawing.CommandCallback;

public class BitmapSetterCommand extends AbstractGetSourceCommand<Bitmap> {

    protected boolean mCancelled;

    protected CommandCallback mView;
    protected int mViewHeight;
    protected int mViewWidth;
    protected ImageDescriptor mImageDescriptor;

    public BitmapSetterCommand(String baseUrl, ImageDescriptor imageDescriptor, ImageSetterCommandCallback view, int width, int height) {
        super(baseUrl, imageDescriptor.getImageSource());
        mImageDescriptor = imageDescriptor;
        mView = view;
        mViewHeight = height;
        mViewWidth = width;
    }

    @Override
    public void cancel() {
        super.cancel();
        mCancelled = true;
        mView = null;
    }

    @Override
    public Object[] getParams() {
        return new Object[]{mViewWidth, mViewHeight};
    }

    public ImageDescriptor getImageDescriptor() {
        return mImageDescriptor;
    }

    public static int[] getBitmapParams(Object[] viewParams) {
        int[] params = new int[2];
        if (viewParams != null) {
            params[0] = (Integer) viewParams[0];
            params[1] = (Integer) viewParams[1];
        } else {
            params[0] = 0;
            params[1] = 0;
        }
        return params;
    }

    @Override
    protected void setLoading() {
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mView != null && mView instanceof ImageSetterCommandCallback) {
                    ((ImageSetterCommandCallback) mView).loadingStarted();
                }
            }
        });
    }

    @Override
    public void postGetSource(final Bitmap bmp) {
        clearAssociatedTask();
        if (!mCancelled) {
            AsyncCaller.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mView != null) {
                        ((ImageSetterCommandCallback) mView).setImageSrc(bmp);
                    }
                }
            });
        }
    }

    @Override
    public void onError() {
        clearAssociatedTask();
        if (!mCancelled) {
            AsyncCaller.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mView != null) {
                        Bitmap errorPlaceholder = AppPackageManager.getInstance().getPackage().getErrorPlaceholder();
                        ((ImageSetterCommandCallback) mView).setImageSrc(errorPlaceholder);
                    }
                }
            });
        }
    }

}
