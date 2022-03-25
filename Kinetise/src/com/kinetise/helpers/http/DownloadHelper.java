package com.kinetise.helpers.http;

import android.os.AsyncTask;

import java.io.*;
import java.net.URL;
import java.security.InvalidParameterException;

/**
 * Created by Kuba Komorowski on 2014-10-21.
 */
public class DownloadHelper {

    public static boolean downloadAndSaveSynchronously(String assetUrl, String fileName) {
        try {
            URL u = new URL(assetUrl);
            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            FileOutputStream fos = new FileOutputStream(new File(fileName));
            while ((length = dis.read(buffer))>0) {
                fos.write(buffer, 0, length);

            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void downloadAndSaveAsynchronously(String assetUrl, String fileName, DownloadFinishedCallback callback){
        DownloadFilesTask task = new DownloadFilesTask(callback);
        task.execute(assetUrl,fileName);
    }

    private static class DownloadFilesTask extends AsyncTask<String, Integer, Boolean> {
        DownloadFinishedCallback mCallback;

        public DownloadFilesTask(DownloadFinishedCallback callback){
            mCallback = callback;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if(params.length !=2){
                throw new InvalidParameterException("DownloadFilesTask execute should take 2 parameters - url and result filename.");
            }
            return downloadAndSaveSynchronously(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(Boolean result){
            mCallback.onDownloadCompleted(result);
        }
    }


    public interface DownloadFinishedCallback{
        void onDownloadCompleted(boolean isDownloaded);
    }

}
