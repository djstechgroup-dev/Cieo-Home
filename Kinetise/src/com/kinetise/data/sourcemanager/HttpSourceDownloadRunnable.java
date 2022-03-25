package com.kinetise.data.sourcemanager;

import android.graphics.Bitmap;

import com.google.gson.JsonObject;
import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.alterapimanager.AGOkHttpConfigurator;
import com.kinetise.data.application.feedmanager.DownloadFeedCommand;
import com.kinetise.data.application.formdatautils.FormFormaterV3;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.datadescriptors.components.ImageDescriptor;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.packagemanager.AppPackageManager;
import com.kinetise.data.systemdisplay.bitmapsettercommands.BitmapSetterCommand;
import com.kinetise.helpers.AndroidBitmapDecoder;
import com.kinetise.helpers.http.CacheControlOptions;
import com.kinetise.helpers.http.HttpRequestManager;
import com.kinetise.helpers.http.HttpRequestManager.RequestType;
import com.kinetise.helpers.http.NetworkUtils;
import com.kinetise.helpers.http.OkHttpClientManager;
import com.kinetise.helpers.http.RedirectMap;
import com.kinetise.helpers.jq.JQBridge;
import com.kinetise.helpers.threading.AGAsyncTask;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpSourceDownloadRunnable extends AGAsyncTask implements NetworkUtils.RequestCallback {
    protected final IGetSourceCommand mCommand;
    protected final AssetsManager.ResultType mExpectedResultType;
    protected final Map<String, String> mHeaders;
    protected String mUri;

    public HttpSourceDownloadRunnable(IGetSourceCommand command, AssetsManager.ResultType expectedResultType, String uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        mCommand = command;
        mExpectedResultType = expectedResultType;
    }

    @Override
    public void run() {
        if (mIsCanceled)
            return;

        sleepIfDelayed();

        if (mIsCanceled)
            return;

        getAndHandleDataFromInternet();
    }

    private void sleepIfDelayed() {
        if (mCommand instanceof DownloadFeedCommand) {
            long delay = ((DownloadFeedCommand) mCommand).getDelay();
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getAndHandleDataFromInternet() {
        OkHttpClient client = OkHttpClientManager.getInstance().getClient();
        String uri = mUri;

        if (mCommand instanceof DownloadFeedCommand && ((DownloadFeedCommand) mCommand).isLongPolling()) {
            uri = AssetsManager.addHttpQueryParams(uri, "longpolling", "true");
            long timestamp = ((DownloadFeedCommand) mCommand).getPreviousResponseDate();
            if (timestamp > 0)
                uri = AssetsManager.addHttpQueryParams(uri, "lastupdatetoken", Long.toString(timestamp));
            client = client.clone();
            client.setReadTimeout(0, TimeUnit.MILLISECONDS);
            client.setConnectTimeout(0, TimeUnit.MILLISECONDS);
        }

        AGHttpMethodType method;
        String bodyTransform = null;
        Map<String, String> bodyParams = null;
        String postBody = null;

        if (mCommand instanceof DownloadFeedCommand) {
            IFeedClient feedClient = ((DownloadFeedCommand) mCommand).getFeedClient();
            method = feedClient.getHttpMethod();
            bodyTransform = feedClient.getRequestBodyTrasform();
            bodyParams = feedClient.getBodyParamsDataDesc().getHttpParamsAsHashMap();
            postBody = getPostBody(bodyTransform, bodyParams);
        } else if (mCommand instanceof BitmapSetterCommand) {
            ImageDescriptor imageDescriptor = ((BitmapSetterCommand) mCommand).getImageDescriptor();
            method = imageDescriptor.getHttpMethod();
            if (method != null && method != AGHttpMethodType.GET) { //TODO czeka na weba - checkboxy/radio/toogle nie mają HttpMethod
                bodyTransform = imageDescriptor.getRequestBodyTrasform();
                bodyParams = imageDescriptor.getBodyParams().getHttpParamsAsHashMap();
                postBody = getPostBody(bodyTransform, bodyParams);
            }
        } else {
            method = AGHttpMethodType.GET;
        }

        NetworkUtils.sendRequest(client, method, uri, mHeaders, postBody, null, mExpectedResultType == AssetsManager.ResultType.IMAGE ? RequestType.IMAGE : RequestType.FEED, mCommand.getCacheOption(), this);
    }

    private void updateRedirectMap(HttpRequestManager requestManager) {
        String redirectedUrl = requestManager.getResponseUrl();
        if (redirectedUrl != null)
            RedirectMap.getInstance().addRedirect(mUri, redirectedUrl);
    }

    private String getPostBody(String bodyTransform, Map<String, String> bodyParams) {
        String postBody = "";
        if (bodyParams != null) {
            postBody = formatBodyParams(bodyParams);
            postBody = JQBridge.runTransform(bodyTransform, postBody, AGApplicationState.getInstance().getContext());
        }
        return postBody;
    }

    private String formatBodyParams(Map<String, String> bodyParams) {
        String resultBody;
        JsonObject jsonObject = FormFormaterV3.formatParams(bodyParams);
        JsonObject resultObject = new JsonObject();
        resultObject.add(FormFormaterV3.PARAMS_SECTION_NAME, jsonObject);
        resultBody = resultObject.toString();
        return resultBody;
    }

    private void passResultToCommand(HttpRequestManager requestManager) {
        Object result;

        if (mIsCanceled)
            return;

        if (mCommand instanceof DownloadFeedCommand) {
            Map<String, List<String>> headers = requestManager.getHeaders();
            long timestamp = 0;
            if (headers != null) {
                List<String> timestampHeaders = headers.get(AGOkHttpConfigurator.KINETISE_HEADER_LAST_UPDATE_TOKEN.toLowerCase(Locale.getDefault()));
                if (timestampHeaders != null && timestampHeaders.size() > 0) {
                    timestamp = Long.parseLong(timestampHeaders.get(0));
                } else {
                    Date date = NetworkUtils.getHeaderDate(headers);
                    timestamp = date.getTime();
                }
            }
            ((DownloadFeedCommand) mCommand).setResponseTimestamp(timestamp);
            DataFeedResponse dataFeedResponse = new DataFeedResponse(requestManager.getContent(), timestamp);
            mCommand.postGetSource(dataFeedResponse);
            return;
        } else if (mExpectedResultType == AssetsManager.ResultType.IMAGE) {
            result = streamAsBitmap(requestManager.getContent());
        } else if (mExpectedResultType == AssetsManager.ResultType.SOUND) {
            String fileName = String.valueOf(System.currentTimeMillis());
            File mFile = new File(KinetiseApplication.getInstance().getFilesDir(), fileName + ".mp3");
            try {
                createSoundFile(mFile, requestManager.getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = mFile;
        } else {
            result = AssetsManager.streamAsString(requestManager.getContent());
        }
        if (mIsCanceled)
            return;

        mCommand.postGetSource(result);
    }

    private void createSoundFile(File f, InputStream is) throws Exception {
        FileOutputStream fOutput = null;
        try {
            fOutput = new FileOutputStream(f);
            byte[] buffer = new byte[1024];
            int length = is.read(buffer);
            while (length != -1) {
                fOutput.write(buffer, 0, length);
                length = is.read(buffer);
            }
        } finally {
            if (fOutput != null) {
                fOutput.flush();
                fOutput.close();
            }
        }
    }

    private Object streamAsBitmap(InputStream stream) {
        Bitmap bitmap = null;
        int[] params = BitmapSetterCommand.getBitmapParams(mCommand.getParams());
        try {
            bitmap = AndroidBitmapDecoder.decodeBitmapFromStream(stream, params[0], params[1]);
        } catch (OutOfMemoryError | IOException error) {
            ExceptionManager.getInstance().handleException(error, false);
        }
        if (bitmap != null) {
            BitmapCache.getInstance().addBitmap(mUri, params[0], params[1], bitmap);
        } else {
            bitmap = AppPackageManager.getInstance().getPackage().getErrorPlaceholder();
        }
        return bitmap;
    }

    @Override
    public void onFailed(HttpRequestManager requestManager, PopupMessage... messages) {
        updateRedirectMap(requestManager);
        int statusCode = requestManager.getStatusCode();
        mCommand.onError(statusCode, messages);
    }

    @Override
    public void onResponse(HttpRequestManager requestManager) {
        updateRedirectMap(requestManager);
        if (mIsCanceled)
            return;
        passResultToCommand(requestManager);
    }

    @Override
    public void onLogout() {
        if (mCommand instanceof DownloadFeedCommand) {
            AGApplicationState.getInstance().logoutUser();
        }
    }
}

