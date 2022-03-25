package com.kinetise.data.systemdisplay.helpers;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import com.kinetise.data.systemdisplay.views.AGPhotoView;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class PhotoSetter {

    private static AGPhotoView mPhotoButton;

    public static AGPhotoView getClickedPhotoButtonView() {
        return mPhotoButton;
    }

    public static void setClickedPhotoButtonView(AGPhotoView photoView) {
        mPhotoButton = photoView;
    }

    private static String getPhotoFilepath(Uri uri, ContentResolver resolver){
        final String imageFilePath;
        Cursor cursor = resolver.query(uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
        cursor.moveToFirst();

        imageFilePath = cursor.getString(0);
        cursor.close();

        return imageFilePath;
    }

    //http://stackoverflow.com/questions/19805966/download-image-from-new-google-plus-photos-application
    public static Uri writeToTempImageAndGetPathUri( ContentResolver resolver, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(resolver, inImage, null, null);
        return Uri.parse(path);
    }

    public static void setPhotoBackground(Uri uri, ContentResolver contentResolver) {
        String imageFilePath;
        if (uri.getPath().contains(".")) {
            imageFilePath = uri.getPath();
        } else {
            imageFilePath = getPhotoFilepath(uri, contentResolver);
            if (imageFilePath == null) {
                Uri tmp = downloadAndReturnUri(uri, contentResolver);
                imageFilePath = getPhotoFilepath(tmp, contentResolver);
            }
        }
        if (imageFilePath != null && getClickedPhotoButtonView() != null) {
            setPhotoButtonView(imageFilePath);
        }
    }

    private static Uri downloadAndReturnUri(Uri remoteSource, ContentResolver contentResolver) {
        InputStream is;
        Bitmap bitmap;
        try {
            is = contentResolver.openInputStream(remoteSource);
            bitmap = BitmapFactory.decodeStream(is);
            return writeToTempImageAndGetPathUri(contentResolver, bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setPhotoButtonView(String imageFilePath) {
        AGPhotoView photoView = getClickedPhotoButtonView();
        if (photoView!=null) {
            getClickedPhotoButtonView().setPhotoImage(imageFilePath);
            setClickedPhotoButtonView(null);
        }
    }
}
