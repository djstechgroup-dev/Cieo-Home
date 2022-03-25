package com.kinetise.data.application.externalapplications;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.kinetise.data.exceptionmanager.ExceptionManager;

public class WebBrowserApp extends AbstractExternalApplication {

    private final Uri mUri;

    public WebBrowserApp(Uri uri) {
        super();
        mUri = uri;
    }

    /**
     * Simply opens webbrowser with given URI
     */
    @Override
    public boolean open(Activity activity) {
        super.open(activity);

        try{
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            intent.setData(mUri);
            activity.startActivity(intent);
        }catch (Exception e){
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

    public Uri getUri(){
        return mUri;
    }
}
