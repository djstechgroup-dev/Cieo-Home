package com.kinetise.helpers.http;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import com.kinetise.data.application.alterapimanager.AGOkHttpConfigurator;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.helpers.threading.AGAsyncTask;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Task to start system-level download of a file.
 * It queries for file headers to better evaluate mime-type and better handle file opening.
 */

public class DownloadFileRunnable extends AGAsyncTask {

    private final DownloadManager.Request mRequest;
    private final Context mContext;
    private String mUrl;

    public DownloadFileRunnable(String url, DownloadManager.Request request, Context context) {
        this.mUrl = url;
        this.mRequest = request;
        this.mContext = context;
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    @Override
    public void run() {
        if (mIsCanceled) {
            return;
        }

        String mimeType = null;
        String contentDisposition = null;

        HttpRequestManager requestManager = new HttpRequestManager();
        try {
            Request.Builder builder = AGOkHttpConfigurator.configureOkHttpRequestForGet(mUrl, new HashMap<String, String>());

            requestManager.executeGetNoContent(OkHttpClientManager.getInstance().getClient(), builder);
            int statusCode = requestManager.getStatusCode();

            if (statusCode >= 200 && statusCode < 300) {
                Map<String, List<String>> headers = requestManager.getHeaders();
                List<String> contentTypeHeader = headers.get("content-type");
                if (contentTypeHeader != null && contentTypeHeader.size() > 0) {
                    mimeType = contentTypeHeader.get(0);
                }

                if (mimeType != null) {
                    final int semicolonIndex = mimeType.indexOf(';');
                    if (semicolonIndex != -1) {
                        mimeType = mimeType.substring(0, semicolonIndex);
                    }
                }

                List<String> contentDispositionHeader = headers.get("content-disposition");
                if (contentDispositionHeader != null && contentDispositionHeader.size() > 0) {
                    contentDisposition = contentDispositionHeader.get(0);
                }
            } else {
                // there was error with reading header of the file, so don't try actual download, but show error toast
                PopupManager.showToast(LanguageManager.getInstance().getString(LanguageManager.ERROR_CONNECTION));
                return;
            }


        } catch (Exception e) {
            // there was error with reading header of the file, so don't try actual download, but show error toast
            e.printStackTrace();
            PopupManager.showToast(LanguageManager.getInstance().getString(LanguageManager.ERROR_CONNECTION));
            return;
        }


        // try to find out real mime type
        // we check 1) actual mime type from Content-Type header 2) mime type based on extension visibile in URL 3) mime type based on extension visibile in Content-Disposition filename
        boolean mimeTypeKnown = false;

        if (mimeType != null) {
            if (mimeType.equalsIgnoreCase("text/plain") ||
                    mimeType.equalsIgnoreCase("application/octet-stream")) {
                String newMimeType =
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                                MimeTypeMap.getFileExtensionFromUrl(mUrl));
                if (newMimeType != null) {
                    mimeType = newMimeType;
                    mRequest.setMimeType(newMimeType);
                    mimeTypeKnown = true;
                }
            } else {
                mimeTypeKnown = true;
            }
        }

        String filename = URLUtil.guessFileName(mUrl, contentDisposition, mimeType);

        if (!mimeTypeKnown) {
            int lastDotIdx = filename.lastIndexOf('.');
            if (lastDotIdx != -1 && lastDotIdx < (filename.length() - 1)) {
                String contentDispositionFilenameExtension = filename.substring(lastDotIdx + 1);
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(contentDispositionFilenameExtension);
                if (mimeType != null) {
                    mimeTypeKnown = true;
                }
            }
        }

        if (mimeTypeKnown)
            mRequest.setMimeType(mimeType);
        mRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);


        // Start the actual download
        DownloadManager manager = (DownloadManager) mContext.getSystemService(
                Context.DOWNLOAD_SERVICE);
        manager.enqueue(mRequest);
    }

}
