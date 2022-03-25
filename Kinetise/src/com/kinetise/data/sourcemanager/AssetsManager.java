package com.kinetise.data.sourcemanager;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.feedmanager.DownloadFeedCommand;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedDBManager;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.HttpParamsElementDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.packagemanager.AppPackage;
import com.kinetise.data.packagemanager.AppPackageManager;
import com.kinetise.data.systemdisplay.bitmapsettercommands.BitmapSetterCommand;
import com.kinetise.helpers.AndroidBitmapDecoder;
import com.kinetise.helpers.BitmapHelper;
import com.kinetise.helpers.ExifHelper;
import com.kinetise.helpers.threading.ThreadPool;
import com.kinetise.support.logger.Logger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AssetsManager {

    public static final String PREFIX_ASSETS = "assets://";
    public static final String PREFIX_HTTP = "http://";
    public static final String PREFIX_CONTROL = "control://";
    public static final String PREFIX_HTTPS = "https://";
    public static final String PREFIX_LOCAL = "local://";
    public static final String PREFIX_SDCARD = "sdcard://";

    /**
     * Method is used to append http params to request Webservice url,
     *
     * @param url web address
     */
    public static String addHttpQueryParams(String url, HttpParamsDataDesc pHttpParamsDataDesc) {
        if (pHttpParamsDataDesc == null || pHttpParamsDataDesc.getHttpParamsElementDataDescs().isEmpty()) {
            return url;
        }

        for (HttpParamsElementDataDesc httpParamsElementDataDesc : pHttpParamsDataDesc.getHttpParamsElementDataDescs()) {
            VariableDataDesc paramValue = httpParamsElementDataDesc.getParamValue();
            paramValue.resolveVariable();
            url = addHttpQueryParams(url, httpParamsElementDataDesc.getParamName(), paramValue.getStringValue());
        }

        return url;
    }

    /**
     * Method is used to append http params to request Webservice url,
     *
     * @param url web address
     */
    public static String addHttpQueryParams(String url, Map<String, String> pHttpParamsDataDesc) {
        for (Map.Entry<String, String> entry : pHttpParamsDataDesc.entrySet()) {
            url = addHttpQueryParams(url, entry.getKey(), entry.getValue());
        }
        return url;
    }

    public static String addHttpQueryParams(String url, String key, String value) {
        url = String.format(Locale.US, "%s%s=%s", addQuestionMarkOrAnd(url), key, value);
        return url;
    }

    /**
     * Method is used to define if next parameter should be added after '?' or '&' characters.
     * Character '?' should be used when we add first param to url, character '&' is used to add next parameters to url.
     *
     * @param url Alter Api WebService address
     */
    private static String addQuestionMarkOrAnd(String url) {
        if (url.indexOf('?') == -1) {
            url += '?';
        } else {
            url += '&';
        }

        return url;
    }

    public enum ResultType {
        IMAGE, JSON, STORAGE, FONT, FEEDXML, AGELEMENTDATADESC, SOUND
    }

    private static AssetsManager mInstance;

    public static AssetsManager getInstance() {
        if (mInstance == null) {
            synchronized (AssetsManager.class) {
                if (mInstance == null) {
                    mInstance = new AssetsManager();
                }
            }
        }
        return mInstance;
    }

    private AssetsManager() {
    }

    public static void clearInstance() {
        mInstance = null;
    }

    public void destroy() {
        FontLibrary.getInstance().clear();
    }

    public void getAsset(AbstractGetSourceCommand command, ResultType expectedResultType) {
        Map<String, String> headers = null;
        getAsset(command, expectedResultType, headers, null);
    }

    public void getAsset(AbstractGetSourceCommand command, ResultType expectedResultType, HttpParamsDataDesc headers, HttpParamsDataDesc queryParams, HttpParamsDataDesc localDBParams) {
        HashMap<String, String> headersMap;
        if (headers != null) {
            headersMap = headers.getHttpParamsAsHashMap();
        } else
            headersMap = new HashMap<>();

        String commandUri = command.resolveURI();

        if (queryParams != null && (commandUri.startsWith(PREFIX_HTTP) || commandUri.startsWith(PREFIX_HTTPS) || commandUri.startsWith(PREFIX_LOCAL))) {
            commandUri = addHttpQueryParams(commandUri, queryParams);
        }
        command.setUri(commandUri);
        getAsset(command, expectedResultType, headersMap, localDBParams);
    }


    /**
     * Used by many application components, this method is responsible for downloading
     * required data from web or reading from local files.
     *
     * @param command            callback that is mainly used for calling [pre/post]GetSource before/after
     *                           resolving the resource (helpful for showing/hiding loadings etc)
     * @param expectedResultType type of data we expect to be resolved during method execution
     */
    private void getAsset(AbstractGetSourceCommand command, ResultType expectedResultType, Map<String, String> headers, HttpParamsDataDesc localDBParams) {
        String uri = command.getUri();
        try {
            Logger.v(this, "getAsset", "AssetsManager" + uri);
            if (uri.startsWith(PREFIX_ASSETS)) {
                getFromAssets(uri, command);
            } else if (uri.startsWith(PREFIX_HTTP) || uri.startsWith(PREFIX_HTTPS)) {
                command.setLoadingIfNecessary();
                getFromHttp(uri, headers, expectedResultType, command);
            } else if (uri.startsWith(PREFIX_LOCAL)) {
                if (expectedResultType == ResultType.FEEDXML) {
                    getFromLocalDB(uri, command, localDBParams);
                } else {
                    getLocalImage(uri, command);
                }
            } else if (uri.startsWith(PREFIX_CONTROL)) {
                getFromControl(command);
            } else if (uri.startsWith(PREFIX_SDCARD)) {
                getFromSDCard(uri, command);
            } else {
                handleSourceError(command, expectedResultType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleSourceError(command, expectedResultType);
        }
    }

    private void getLocalImage(String uri, AbstractGetSourceCommand command) {
        uri = uri.replace(PREFIX_LOCAL, "/");
        Bitmap b = BitmapHelper.getFromInternalStorage(uri);
        command.postGetSource(b);
    }

    private void handleSourceError(AbstractGetSourceCommand command, ResultType expectedResultType) {
        String assetUri = command.getUri();
        Logger.e(this, "handleSourceError", "Error occurred in getAsset method, asset uri was: " + assetUri);
        command.onError();
        AppPackage appPackage = AppPackageManager.getInstance().getPackage();
        if (expectedResultType == ResultType.IMAGE && appPackage != null) {
            command.postGetSource(appPackage.getErrorPlaceholder());
        }
    }

    private void getFromAssets(String uri, AbstractGetSourceCommand command) {
        AppPackage pack = AppPackageManager.getInstance().getPackage();
        Object asset = pack.getLocalizedAsset(uri, command.getParams());
        if (command instanceof DownloadFeedCommand) {
            DataFeedResponse response = new DataFeedResponse((InputStream) asset, 0);
            command.postGetSource(response);
        } else {
            command.postGetSource(asset);
        }
    }

    private void getFromControl(IGetSourceCommand command) {
        AbstractAGElementDataDesc context = AGApplicationState.getInstance().getApplicationState().getContext();
        command.postGetSource(context);
    }

    private void getFromSDCard(String uri, IGetSourceCommand command) {
        uri = uri.replace(PREFIX_SDCARD, "");
        getBitmapFromURI(uri, command);
    }

    private void getBitmapFromURI(String uri, IGetSourceCommand command) {
        Bitmap bitmap;
        try {
            bitmap = AndroidBitmapDecoder.decodeBitmapFromFilePath(uri, 800, 600);

            int orientation = ExifHelper.extractExifOrientationTagFromFile(uri);
            bitmap = ExifHelper.rotateBitmapFromExifTag(bitmap, orientation);

        } catch (IOException e) {
            e.printStackTrace();
            bitmap = AppPackageManager.getInstance().getPackage().getErrorPlaceholder();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = AppPackageManager.getInstance().getPackage().getErrorPlaceholder();
        }


        command.postGetSource(bitmap);
    }

    public static String streamAsString(InputStream stream) {
        String result;
        if (stream != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    stream));
            try {
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();

            } catch (IOException e) {
                result = "";
                e.printStackTrace();
            }
        } else {
            result = "";
        }
        return result;
    }

    private void getFromLocalDB(String uri, AbstractGetSourceCommand command, HttpParamsDataDesc localDBParams) {
        DataFeedItem item = createDataFeedItemFromLocalDBParams(localDBParams);
        DataFeed dataFeed = DataFeedDBManager.get(uri, item);
        command.postGetSource(dataFeed);
    }

    private DataFeedItem createDataFeedItemFromLocalDBParams(HttpParamsDataDesc localDBParams) {
        DataFeedItem dataFeedItem = new DataFeedItem("item");
        for (Map.Entry<String, String> entry : localDBParams.getHttpParamsAsHashMap().entrySet()) {
            dataFeedItem.put(entry.getKey(), entry.getValue());
        }
        return dataFeedItem;
    }

    private void getFromHttp(String uri, Map<String, String> headers, ResultType expectedResultType, AbstractGetSourceCommand command) {
        if (expectedResultType == ResultType.IMAGE) {
            int[] params = BitmapSetterCommand.getBitmapParams(command.getParams());
            ImageLoader.getInstance().displayImage(uri, new NonViewAware(new ImageSize(params[0], params[1]), ViewScaleType.CROP), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    command.postGetSource(AppPackageManager.getInstance().getPackage().getErrorPlaceholder());
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Log.d("ImageSize", "Image: " + bitmap.getWidth() + ", " + bitmap.getHeight());
                    command.postGetSource(bitmap);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                    command.postGetSource(AppPackageManager.getInstance().getPackage().getErrorPlaceholder());
                }
            });
/*

            int[] params = BitmapSetterCommand.getBitmapParams(command.getParams());
            Bitmap bitmap = BitmapCache.getInstance().getBitmap(command.getUri(), params[0], params[1]);
            if (bitmap != null) {
                command.postGetSource(bitmap);
                return;
            }*/
        } else {

//        if (expectedResultType == ResultType.SOUND) {
//        //todo dorobić cache dla sound ??
//        }

            final HttpSourceDownloadRunnable runnable = new HttpSourceDownloadRunnable(command, expectedResultType, uri, headers);
            command.setAssociatedTask(runnable);

            if (command instanceof DownloadFeedCommand && ((DownloadFeedCommand) command).getDelay() > 0) {
                ThreadPool.getInstance().executeFeedLongTask(runnable);
            } else if (command instanceof DownloadFeedCommand && ((DownloadFeedCommand) command).isLongPolling()) {
                ThreadPool.getInstance().executeFeedLongTask(runnable);
            } else {
                ThreadPool.getInstance().execute(runnable);
            }
        }
    }

    private static boolean isAbsoluteURI(String uri) {
        return uri.contains("://");
    }

    public static String resolveURI(String uri, String baseUri) {
        if (isAbsoluteURI(uri))
            return decodeUri(uri);
        URL baseUrl;
        try {
            baseUrl = new URL(baseUri);
        } catch (Exception ex) {
            return decodeUri(uri);
        }
        return decodeUri(baseUrl.getProtocol() + "://" + baseUrl.getHost() + uri);
    }

    public static String decodeUri(String uri) {
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return uri;
    }

}






