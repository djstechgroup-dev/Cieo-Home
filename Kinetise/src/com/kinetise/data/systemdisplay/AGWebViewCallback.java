package com.kinetise.data.systemdisplay;

import android.webkit.CookieSyncManager;
import com.kinetise.data.systemdisplay.views.AGWebBrowserView;

/**
 * @author: Marcin Narowski
 * Date: 14.10.13
 * Time: 14:34
 */
public class AGWebViewCallback {
    private AGWebBrowserView mView;
    private IPlatformView mPlatformView;

    public AGWebViewCallback(AGWebBrowserView pAGWebBrowserView) {
        mView = pAGWebBrowserView;
    }

    public void pause() {
        mView.onCallbackPause();
        mView.pauseTimers();
        CookieSyncManager.getInstance().stopSync();
    }

    public void resume() {
        mView.onCallbackResume();
        mView.resumeTimers();
        CookieSyncManager.getInstance().startSync();
    }

    public void attach(IPlatformView pPlatformView) {
        mPlatformView = pPlatformView;
        mPlatformView.addWebViewCallback(this);
    }

    public void unattach() {
        mPlatformView.removeWebViewCallback(this);
        mPlatformView = null;
        mView = null;
    }

}
