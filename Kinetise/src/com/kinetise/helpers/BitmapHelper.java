package com.kinetise.helpers;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.support.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitmapHelper {

    public static final int PHOTO_SIZE = 1024;
    public static final String IMAGE_DIR = "images";
    public static final String DB_ATTACHMENT = "dbattachment";

    public static Bitmap getBitmapWithCorrectRotation(String path) throws IOException {
        InputStream stream = new FileInputStream(path);
        Bitmap bitmap = AndroidBitmapDecoder.decodeBitmapFromStreamForMax(stream, PHOTO_SIZE);
        int orientation = ExifHelper.extractExifOrientationTagFromFile(path);
        bitmap = ExifHelper.rotateBitmapFromExifTag(bitmap, orientation);
        return bitmap;
    }

    public static String saveToInternalStorage(Context context, Bitmap bitmap, String filename) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        File path = new File(directory, filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public static Bitmap getFromInternalStorage(String absolutePath) {
        try {
            File f = new File(absolutePath);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void copyFileToImageDir(String assetDir, String filename) {
        Context context = KinetiseApplication.getInstance();
        AssetManager assetManager = KinetiseApplication.getInstance().getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
            File file = new File(directory, filename);
            if (file.exists() == false) {
                out = new FileOutputStream(file);
                in = assetManager.open(assetDir + "/" + DB_ATTACHMENT + "/" + filename);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            }
        } catch (Exception e) {
            Logger.e("BitmapHelper", "copyFileToImageDir\t" + e);
        }
    }

    public static void copyToLocale(String path) {
        Context context = KinetiseApplication.getInstance();
        String assetDir = context.getString(RWrapper.string.getDeveloperAssetsPrefix());
        AssetManager assetManager = KinetiseApplication.getInstance().getAssets();
        String assets[] = null;
        try {
            assets = assetManager.list(assetDir + "/" + path);
            if (assets.length > 0) {
                for (int i = 0; i < assets.length; i++) {
                    copyFileToImageDir(assetDir, assets[i]);
                }
            }
        } catch (IOException e) {
            Logger.e("BitmapHelper", "copyToLocale\t" + e);
        }
    }

}
