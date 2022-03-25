package com.kinetise.data.descriptors.types;

import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Base64;

import com.kinetise.data.descriptors.IFormValue;

import java.io.ByteArrayOutputStream;

public class GestureInfo implements IFormValue {

    private Gesture mGesture;
    private int mWidth;
    private int mHeight;
    private Paint mPaint;

    public Gesture getGesture() {
        return mGesture;
    }

    public void setGesture(Gesture gesture) {
        mGesture = gesture;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    public String getPhotoBase64() {
        if (mGesture == null || mGesture.getStrokesCount() == 0)
            return null;
        Bitmap bitmap = getGestureBitmap();
        if (bitmap != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream(); //TODO wydzielić gdzieś!!!!!!!!!! bo już w kilku miejscach się powtarza
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
        } else {
            return null;
        }
    }

    public Bitmap getGestureBitmap() {
        if (mGesture != null) {
            if (mGesture.getStrokesCount() == 0)
                return null;
            Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            c.drawPath(mGesture.toPath(), mPaint);
            return bitmap;
        }
        return null;
    }

    @Override
    public IFormValue copy() {
        GestureInfo copy = new GestureInfo();
        copy.setGesture(mGesture);
        copy.setHeight(mHeight);
        copy.setWidth(mWidth);
        copy.setPaint(mPaint);
        return copy;
    }

    @Override
    public String toString() {
        return getPhotoBase64();
    }
}
