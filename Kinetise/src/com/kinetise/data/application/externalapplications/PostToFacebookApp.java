package com.kinetise.data.application.externalapplications;


import android.app.Activity;
import android.widget.Toast;

import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.helpers.facebook.FacebookService;

public class PostToFacebookApp extends AbstractExternalApplication {
    private final String mAppName;
    private final String mLink;
    private final String mPictureUrl;
    private final String mDescription;
    private final String mCaption;

    public PostToFacebookApp(final String appName, final String caption, final String link, final String picUrl,
                             final String description) {
        super();

        mAppName = appName;
        mCaption = caption;
        mLink = link;
        mPictureUrl = picUrl;
        mDescription = description;
    }

    /**
     * Launches external dialog to login and post to users wall
     */
    @Override
    public boolean open(final Activity activity) {
        try {
            FacebookService.getInstance().setExternalApplication(true);
            FacebookService.getInstance().postToFacebookWallAsync(mDescription,mAppName,mPictureUrl, mLink,mCaption);
        } catch (Throwable e) {
            String error = LanguageManager.getInstance().getString(LanguageManager.FACEBOOK_INIT);
            Toast.makeText(activity.getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            ExceptionManager.getInstance().handleException(new Exception(e), false);
        }
        return true;
    }

    @Override
    public void close() {
        FacebookService.getInstance().setExternalApplication(false);
        //do not close application
        try {
            super.close();
        } catch (Exception e) {
            ExceptionManager.getInstance().handleException(e);
        }
    }
}
