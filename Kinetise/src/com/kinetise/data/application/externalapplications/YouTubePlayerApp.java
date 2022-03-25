package com.kinetise.data.application.externalapplications;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.sourcemanager.LanguageManager;


public class YouTubePlayerApp extends AbstractExternalApplication {
    private final String WARNING_NOTE;
    private final String mVideoId;

    public YouTubePlayerApp(String videoId) {
        super();
        
        WARNING_NOTE = LanguageManager.getInstance().getString(LanguageManager.NO_YOUTUBE_APP);
        mVideoId = videoId;
    }

    /**
     * Starts external youtube application, shows error when theres no such app
     */
    @Override
    public boolean open(Activity activity) {
        super.open(activity);

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + mVideoId));
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, WARNING_NOTE, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            ExceptionManager.getInstance().handleException(e);
        }

        return true;
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (Exception e) {
            ExceptionManager.getInstance().handleException(e);
        }
    }
}
