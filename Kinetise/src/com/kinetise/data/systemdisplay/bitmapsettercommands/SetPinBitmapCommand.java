package com.kinetise.data.systemdisplay.bitmapsettercommands;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import com.kinetise.data.descriptors.types.AGSizeModeType;
import com.kinetise.data.packagemanager.AppPackage;
import com.kinetise.data.packagemanager.AppPackageManager;
import com.kinetise.data.sourcemanager.AbstractGetSourceCommand;
import com.kinetise.data.systemdisplay.views.AGMapView;
import com.kinetise.helpers.asynccaller.AsyncCaller;
import com.kinetise.helpers.drawing.ScaleHelper;

public class SetPinBitmapCommand extends AbstractGetSourceCommand<Bitmap> {
    private int mWidth;
    private int mHeight;
    private AGMapView mMapView;
    private String mBitmapSource;

    public SetPinBitmapCommand(String baseUrl, String source, int width, int height, AGMapView mapView){

        super(baseUrl, source);
        mWidth = width;
        mHeight = height;
        mMapView = mapView;
        mBitmapSource=source;
    }

    @Override
    public void postGetSource(final Bitmap bitmap) {
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Matrix matrix = new ScaleHelper(AGSizeModeType.LONGEDGE, mWidth, mHeight, bitmap.getWidth(), bitmap.getHeight()).getMatrixForScale();
                Bitmap scaled = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                mMapView.addBitmapToDictionaryAndUpdateIfallAdded(mBitmapSource, scaled);
            }
        });
    }

    @Override
    public void onError() {
        AppPackage appPackage = AppPackageManager.getInstance().getPackage();
        postGetSource(appPackage.getErrorPlaceholder());
        mMapView.hideLoading();
    }

    @Override
    public void cancel() {
        //nothing to do
    }

    @Override
    public Object[] getParams() {
        return new Object[]{mWidth,mHeight};
    }
}
