package com.kinetise.data.descriptors.types;

import android.graphics.Bitmap;
import android.util.Base64;

import com.kinetise.data.descriptors.IFormValue;
import com.kinetise.helpers.BitmapHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class PhotoInfo implements IFormValue {

    private String mPath;

    public void setPath(String path) {
        mPath = path;
    }

    public String getPath() {
        if (mPath != null)
            return mPath;
        return "";
    }

    public String getPhotoBase64() {
        if (mPath != null && !mPath.equals("")) {
            Bitmap bitmap;
            try {
                bitmap = BitmapHelper.getBitmapWithCorrectRotation(mPath);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void clear() {
        mPath = null;
    }

    public boolean isPhotoTaken() {
        return (mPath != null && !mPath.equals(""));
    }

    @Override
    public String toString() {
        return getPhotoBase64();
    }

    public PhotoInfo copy() {
        PhotoInfo copy = new PhotoInfo();
        copy.setPath(mPath);
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof PhotoInfo) {
            PhotoInfo compared = (PhotoInfo) obj;
            if (mPath == null && compared.mPath == null)
                return true;
            else if (mPath != null && mPath.equals(compared.mPath))
                return true;
        }
        return super.equals(obj);
    }
}
