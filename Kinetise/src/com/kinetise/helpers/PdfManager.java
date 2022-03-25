package com.kinetise.helpers;

import android.content.Context;
import com.kinetise.helpers.http.DownloadHelper;
import com.kinetise.support.logger.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kuba Komorowski on 2014-10-22.
 */
public class PdfManager implements DownloadHelper.DownloadFinishedCallback {
    private static final String TEMP_PDF_DIRECTORY_NAME = "pdfTemp";
    private static PdfManager mInstance;
    private String mFilepath;
    private Map<String, String> mFiles;

    private PdfCallback mCallback;

    private PdfManager(){
        mFiles = new HashMap<String,String>();
    }

    public static PdfManager getInstance(){
        if(mInstance == null){
            synchronized (PdfManager.class){
                if(mInstance == null){
                    mInstance = new PdfManager();
                }
            }
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    public void getPdf(String url, PdfCallback callback, Context context){
        File pdfDirectory = context.getDir(TEMP_PDF_DIRECTORY_NAME,Context.MODE_PRIVATE);

        if(mFiles.containsKey(url)){
            String filename = mFiles.get(url);
            Logger.v(this,"getPdf","Pdf from url: " + url + " is already saved with name " + filename);
            mFilepath = pdfDirectory.getAbsolutePath() + File.separator + filename;
            callback.pdfDownloaded("file:///android_asset/pdfviewer/index.html?url=" + mFilepath);
            mFilepath = null;
        }
        else{
            Logger.v(this,"getPdf","Downloading pdf from url : " + url);
            String filename = Integer.toString(mFiles.size());
            Logger.v(this,"getPdf","Pdf is being downloaded with filename " + filename);
            mFilepath = pdfDirectory.getAbsolutePath() + File.separator + filename;
            mFiles.put(url, filename);
            mCallback = callback;
            DownloadHelper.downloadAndSaveAsynchronously(url,mFilepath,this);
        }
    }

    public void cleanTempFiles(Context context) {
        mFiles = null;
        File pdfDirectory = context.getDir(TEMP_PDF_DIRECTORY_NAME, Context.MODE_PRIVATE);
        if (pdfDirectory.isDirectory()) {
            String[] children = pdfDirectory.list();
            for (int i = 0; i < children.length; i++) {
                new File(pdfDirectory, children[i]).delete();
            }
        }
    }

    @Override
    public void onDownloadCompleted(boolean isDownloaded) {
        if(isDownloaded){
            Logger.v(this,"getPdf","Pdf is being downloaded with filename");
            mCallback.pdfDownloaded("file:///android_asset/pdfviewer/index.html?url=" + mFilepath);
        } else {
            Logger.w(this, "onDownloadCompleted" , String.format("Could not download file : %s" , mFilepath));
            mCallback.pdfDownloaded(mFilepath);
        }
        mFilepath = null;
        mCallback = null;
    }

    public interface PdfCallback{
        public void pdfDownloaded(String filepath);
    }
}
