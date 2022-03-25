package com.kinetise.data.application.actionmanager.nativeshare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.commands.NativeShareGetBitmapCommand;
import com.kinetise.data.sourcemanager.AssetsManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShareData {
    public static final String FILE_NAME_PREFIX = "share_image_";
    public static final String FILE_EXTENSION = ".jpg";
    public static List<File> mDownloadedImages = new ArrayList<>();
    private ArrayList<String> mTexts;
    private ArrayList<SharedImageData> mImages;

    public ShareData() {
        mTexts = new ArrayList<>();
        mImages = new ArrayList<>();
    }

    public void setIntentData(Intent intent) {
        setIntentAction(intent);
        setIntentExtras(intent);
        setIntentMime(intent);

    }

    public boolean allDataDownloaded() {
        for (SharedImageData imageData : mImages) {
            if (imageData.getStatus() == SharedImageData.DownloadStatus.WAITING)
                return false;
        }
        return true;
    }

    public void setIntentExtras(Intent intent) {
        if (hasText()) {
            if (mTexts.size() > 1) {
                intent.putStringArrayListExtra(Intent.EXTRA_TEXT, mTexts);
            } else {
                intent.putExtra(Intent.EXTRA_TEXT, mTexts.get(0));
            }
        }
        if (hasImage()) {
            if (getImageCount() > 1) {
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, getImageUris());
            } else {
                intent.putExtra(Intent.EXTRA_STREAM, getImageUris().get(0));
            }
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    public void putText(String text) {
        mTexts.add(text);
    }

    public void putImageSource(String imageSrc) {
        mImages.add(new SharedImageData(imageSrc));
    }

    public void setIntentAction(Intent intent) {
        if (getImageCount() + mTexts.size() > 0) {
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        }
        intent.setAction(Intent.ACTION_SEND);
    }

    public void setIntentMime(Intent intent) {
        if (hasText()) {
            if (hasImage()) {
                intent.setType("*/*");
            } else {
                intent.setType("text/plain");
            }
        } else {
            intent.setType("image/png");
        }
    }

    public boolean hasText() {
        return mTexts.size() > 0;
    }

    public boolean hasImage() {
        for (SharedImageData imageData : mImages) {
            if (imageData.getStatus() == SharedImageData.DownloadStatus.DOWNLOADED)
                return true;
        }
        return false;
    }

    public ArrayList<Uri> getImageUris() {
        ArrayList<Uri> imageUris = new ArrayList<>();
        for (SharedImageData imageData : mImages) {
            if (imageData.getStatus() == SharedImageData.DownloadStatus.DOWNLOADED)
                imageUris.add(imageData.getImageUri());
        }
        return imageUris;
    }

    public int getImageCount() {
        return getImageUris().size();
    }

    public void downloadImages(OnAllDownloadsFinishedCallback callback, String baseUrl) {
        for (SharedImageData imageData : mImages) {
            startImageDownload(imageData, baseUrl, callback);
        }
    }

    private void startImageDownload(final SharedImageData imageData, String baseUrl, final OnAllDownloadsFinishedCallback callback) {
        NativeShareGetBitmapCommand bitmapCommand = new NativeShareGetBitmapCommand(baseUrl, imageData.getAsImageDescriptor(), new NativeShareGetBitmapCommand.NativeShareCommandCallback() {
            @Override
            public void onDownloadError() {
                imageData.setStatus(SharedImageData.DownloadStatus.DOWNLOAD_ERROR);
                synchronized (this) {
                    if (allDataDownloaded()) {
                        callback.onDownloadsFinished();
                    }
                }
            }

            @Override
            public void onDownloadSuccess(Bitmap b) {
                imageData.setStatus(SharedImageData.DownloadStatus.DOWNLOADED);
                imageData.setImageUri(getLocalBitmapUri(b));
                synchronized (this) {
                    if (allDataDownloaded()) {
                        callback.onDownloadsFinished();
                    }
                }
            }
        });
        AssetsManager.getInstance().getAsset(bitmapCommand, AssetsManager.ResultType.IMAGE);
    }

    public Uri getLocalBitmapUri(Bitmap bitmap) {
        Uri bmpUri = null;

        try {
            Activity activity = AGApplicationState.getInstance().getActivity();
            String fileName = FILE_NAME_PREFIX + System.currentTimeMillis() + FILE_EXTENSION;
            File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
            FileOutputStream out = new FileOutputStream(file);
            synchronized (mDownloadedImages) {
                mDownloadedImages.add(file);
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public interface OnAllDownloadsFinishedCallback {
        void onDownloadsFinished();
    }

    public static void clearShareData() {
        synchronized (mDownloadedImages) {
            Iterator<File> iterator = mDownloadedImages.iterator();
            while (iterator.hasNext()) {
                try {
                    iterator.next().delete();
                    iterator.remove();
                } catch (Exception e) {
                }
            }
        }
    }

}