package com.kinetise.data.application.externalapplications;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.kinetise.components.activity.KinetiseActivity;
import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.data.systemdisplay.helpers.IPermissionListener;
import com.kinetise.data.systemdisplay.helpers.IPermissionRequestListener;
import com.kinetise.data.systemdisplay.helpers.PermissionManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class OpenGalleryApp extends AbstractExternalApplication {
    public static final int PHOTO_CAPTURE = 2;
    public static final int PHOTO_GALLERY = 0;
    private final String SELECT_TEXT;
    private final String OPEN_CAMERA;
    private final String OPEN_GALLERY;
    public String FILE_PATH_PATTERN = String.format(Locale.US, "%s/KinetisePhoto_", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
    static String FILE_PATH;
    public static ArrayList<String> SAVED_PHOTOS = new ArrayList<>();
    final boolean isKitKatOrGreater = Build.VERSION.SDK_INT >= 19; /* KITKAT */

    public static String getFilePath() {
        return FILE_PATH;
    }

    public static void setFilePath(String path) {
        FILE_PATH = path;
        SAVED_PHOTOS.add(path);
    }

    private Activity mActivity;

    public OpenGalleryApp() {
        super();

        SELECT_TEXT = LanguageManager.getInstance().getString(LanguageManager.OPEN_PHOTO_TITLE);
        OPEN_CAMERA = LanguageManager.getInstance().getString(LanguageManager.OPEN_PHOTO_CAMERA);
        OPEN_GALLERY = LanguageManager.getInstance().getString(LanguageManager.OPEN_PHOTO_LIBRARY);
    }

    /**
     * Opening this ExternalApplication starts with dialog that
     * allows user to choose where from shold be the photos loaded,
     * i.e. what intent should be started - gallery to choose file or
     * photo app to caputre one. <p>
     * <p/>
     * When starting an intent, we pass a string to it.
     * This string we will later use in
     * {@link KinetiseActivity} in onAcitivityResult method,
     * to find out what kind of intent we launched previously
     * and how to handle link to image that
     * we received as onActiviryResult() method parameter (different intents return
     * different links, gallery returns link to sdcard, photocapture
     * sometimes link to database etc)
     */
    @Override
    public boolean open(Activity activity) {
        super.open(activity);
        mActivity = activity;
        if (PermissionManager.hasGrantedPermission(mActivity.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == false) {
            requestReadExternalStoragePermission((IPermissionListener) mActivity);
            return false;
        } else {
            loadImage();
        }
        return true;
    }

    private void loadImage() {

        try {
            OpenGalleryApp.setFilePath(FILE_PATH_PATTERN + new Random().nextInt() + ".png");

            AlertDialog.Builder getImageFrom = new AlertDialog.Builder(mActivity);
            getImageFrom.setTitle(SELECT_TEXT);

            PackageManager pm = mActivity.getPackageManager();

            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) || Camera.getNumberOfCameras() > 0) {
                getImageFrom.setItems(new String[]{OPEN_CAMERA, OPEN_GALLERY}, new listenerCameraON());
                getImageFrom.show();
            } else {
                if (isKitKatOrGreater) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    mActivity.startActivityForResult(Intent.createChooser(intent, ""), PHOTO_GALLERY);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    mActivity.startActivityForResult(Intent.createChooser(intent, ""), PHOTO_GALLERY);
                }
            }
        } catch (ActivityNotFoundException e) {
            ExceptionManager.getInstance().handleException(e);
        } catch (Exception e) {
            ExceptionManager.getInstance().handleException(e);
        }
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (Exception e) {
            ExceptionManager.getInstance().handleException(e);
        }
    }

    private class listenerCameraON implements android.content.DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {


            switch (which) {
                case 0:
                    tryTakePicture();
                    break;
                case 1:
                    startChooser();
                    break;
            }
            dialog.dismiss();
        }
    }

    private void requestReadExternalStoragePermission(IPermissionListener listener) {
        listener.onPermissionLack(IPermissionListener.WRITE_EXTERNAL_STORAGE_REQUEST_CODE, new IPermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                loadImage();
            }

            @Override
            public void onPermissionDenied() {
            }
        });
    }

    private void tryTakePicture() {
        Context context = mActivity.getApplicationContext();
        if (PermissionManager.hasPermissionInManifest(context, Manifest.permission.CAMERA)) {
            if (PermissionManager.hasGrantedPermission(context, Manifest.permission.CAMERA)) {
                takePicture();
            } else {
                requestCameraPermission((IPermissionListener) mActivity);
            }
        } else {
            takePicture();
        }
    }

    private void startChooser() {
        if (isKitKatOrGreater) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            mActivity.startActivityForResult(Intent.createChooser(intent, ""), PHOTO_GALLERY);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            mActivity.startActivityForResult(Intent.createChooser(intent, ""), PHOTO_GALLERY);
        }
    }


    private void takePicture() {
        mActivity.startActivityForResult(getImageCaptureIntent(), PHOTO_CAPTURE);
    }

    private void requestCameraPermission(IPermissionListener listener) {
        listener.onPermissionLack(IPermissionListener.CAMERA_REQUEST_CODE, new IPermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                takePicture();
            }

            @Override
            public void onPermissionDenied() {
            }
        });
    }

    private Intent getImageCaptureIntent() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FILE_PATH)));
        return cameraIntent;
    }

    public static synchronized void removeSavedPhotos() {
        for (String path : SAVED_PHOTOS) {
            File file = new File(path);
            ContentResolver resolver = KinetiseApplication.getInstance().getContentResolver();
            if (file.exists() && resolver != null) {
                deleteFileFromMediaStore(resolver, file);
            }
        }
        SAVED_PHOTOS.clear();
    }


    // http://stackoverflow.com/questions/10716642/android-deleting-an-image
    private static void deleteFileFromMediaStore(final ContentResolver contentResolver, final File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        final int result = contentResolver.delete(uri,
                MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
        if (result == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                contentResolver.delete(uri,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
            }
        }
    }

}
