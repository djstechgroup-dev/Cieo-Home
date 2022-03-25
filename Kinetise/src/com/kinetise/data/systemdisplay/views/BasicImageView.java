package com.kinetise.data.systemdisplay.views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

import com.kinetise.data.descriptors.types.AGSizeModeType;
import com.kinetise.helpers.drawing.ScaleHelper;

public class BasicImageView extends View {
    private final Paint mImagePaint = new Paint();
    private final Matrix mImageMatrix = new Matrix();
    private Bitmap mImageBitmap;
    private BitmapShader mImageShader;
    private RectF mImageRect;
    private AGSizeModeType mSizeMode;


    public BasicImageView(Context context) {
        super(context);
        mImagePaint.setAntiAlias(true);
        mImagePaint.setFilterBitmap(true);
        mImagePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    public void setSizeMode(AGSizeModeType sizeMode){
        mSizeMode = sizeMode;
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            mImageBitmap = bitmap;
            mImageShader = new BitmapShader(mImageBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mImageMatrix.reset();
            setMatrix();
            mImagePaint.setShader(mImageShader);
        } else {
            mImageBitmap = null;
            mImagePaint.setShader(null);
        }
        invalidate();
    }

    protected void setMatrix() {
        if (mImageBitmap != null) {
            Matrix matrixForScale = new ScaleHelper(mSizeMode, getWidth(), getHeight(), mImageBitmap.getWidth(), mImageBitmap.getHeight()).getMatrixForScale();
            mImageMatrix.set(matrixForScale);
            mImageRect = new RectF(0, 0, mImageBitmap.getWidth(), mImageBitmap.getHeight());
            mImageMatrix.mapRect(mImageRect);
            mImageShader.setLocalMatrix(mImageMatrix);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setMatrix();
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
        if (mImageBitmap != null) {
            canvas.clipRect(mImageRect);
            canvas.drawRect(mImageRect, mImagePaint);
        }
    }
}
