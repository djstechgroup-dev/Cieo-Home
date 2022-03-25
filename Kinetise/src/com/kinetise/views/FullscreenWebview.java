package com.kinetise.views;

import android.app.Activity;
import android.app.Dialog;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kinetise.helpers.RWrapper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class FullscreenWebview {

    private Dialog mDialog;
    private static FullscreenWebview mInstance;

    private FullscreenWebview() {
    }

    public static FullscreenWebview getInstance() {
        if (mInstance == null) {
            synchronized (FullscreenWebview.class) {
                if (mInstance == null) {
                    mInstance = new FullscreenWebview();
                }
            }
        }
        return mInstance;
    }

    public static void clearInstance() {
        mInstance = null;
    }

    public void showWebView(final WebViewClient webViewClient, String url, final Map<String, String> headerParams, final Activity activity) {
        mDialog = new Dialog(activity, android.R.style.Theme_Holo_Light_NoActionBar);
        mDialog.setContentView(RWrapper.layout.fullscreen_webview);
        mDialog.show();

        WebView webView = (WebView) mDialog.findViewById(RWrapper.id.webView);
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setJavaScriptEnabled(true);

        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (headerParams == null || headerParams.size() == 0)
            webView.loadUrl(url);
        else
            webView.loadUrl(url, headerParams);
    }

    public void closeWebView() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
