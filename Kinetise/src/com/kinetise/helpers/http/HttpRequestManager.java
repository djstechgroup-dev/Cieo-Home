package com.kinetise.helpers.http;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.alterapimanager.AAExtractor;
import com.kinetise.data.application.alterapimanager.AAJsonExtractor;
import com.kinetise.data.application.alterapimanager.AAResponse;
import com.kinetise.data.application.alterapimanager.AAXmlExtractor;
import com.kinetise.data.sourcemanager.InterruptedDownloadException;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.helpers.http.exceptions.DownloadException;
import com.kinetise.helpers.http.exceptions.OtherDownloadException;
import com.kinetise.helpers.http.exceptions.TimeoutDownloadException;
import com.kinetise.helpers.http.exceptions.UnknownHostDownloadException;
import com.kinetise.helpers.jq.JQBridge;
import com.kinetise.support.logger.Logger;
import com.squareup.okhttp.*;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class HttpRequestManager {
    public static final boolean LOGGING_ENABLED = false;
    public static final boolean LOG_MESSAGE_CONTENT = false;

    private int mStatusCode;
    private boolean mCacheResponse;
    private BufferedInputStream mContent;
    private boolean mCanceled = false;
    private Map<String, List<String>> mHeaders;
    private int cacheHitsBeforeLastExecution;
    private int cacheHitsAfterLastExecution;
    private String mResponseUrl;

    public static HttpResponseResultCode httpStatusToResultCode(int statusCode) {
        if (statusCode >= 200 && statusCode < 400)
            return HttpResponseResultCode.OK;
        if (statusCode == 400)
            return HttpResponseResultCode.BAD_REQUEST;
        if (statusCode == 403 || statusCode == 401)
            return HttpResponseResultCode.INVALID_SESSION;
        if (statusCode == 504)
            return HttpResponseResultCode.NO_CACHE;
        else
            return HttpResponseResultCode.OTHER;
    }

    public static String getErrorMessage(RequestType mRequestType) {
        String errorMessage;
        switch (mRequestType) {
            case FORM:
                errorMessage = LanguageManager.getInstance().getString(LanguageManager.ERROR_SEND_FORM);
                break;
            case EMAIL:
                errorMessage = LanguageManager.getInstance().getString(LanguageManager.ERROR_SEND_EMAIL);
                break;
            case LOGIN:
                errorMessage = LanguageManager.getInstance().getString(LanguageManager.ERROR_LOGIN);
                break;
            default:
                errorMessage = LanguageManager.getInstance().getString(LanguageManager.ERROR_DATA);
                break;
        }
        return errorMessage;
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    public void cancel() {
        mCanceled = true;
    }

    public void executeGetNoContent(OkHttpClient client, Request.Builder builder) throws DownloadException {
        executeRequestWithoutReadingContent(client, builder);
    }

    public void executeRequest(OkHttpClient client, Request.Builder builder) throws DownloadException {
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            mStatusCode = response.code();

            mCacheResponse = (response.cacheResponse() != null);

            if (httpStatusToResultCode(mStatusCode) == HttpResponseResultCode.NO_CACHE) {
                mContent = null;
            } else {
                mHeaders = parseHeaders(response.headers());
                InputStream is;

                is = decompressStream(response.body().byteStream());

                mContent = new BufferedInputStream(is);
            }

            mResponseUrl = response.request().urlString();

            updateCacheHitCounter();
            if (LOGGING_ENABLED)
                LogLastResponse();
        } catch (InterruptedIOException e) {
            e.printStackTrace();
            throw new InterruptedDownloadException();
        } catch (SocketException e) {
            e.printStackTrace();
            throw new OtherDownloadException();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new UnknownHostDownloadException(request.urlString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new TimeoutDownloadException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new OtherDownloadException();
        }
    }

    private void executeRequestWithoutReadingContent(OkHttpClient client, Request.Builder builder) throws DownloadException {
        try {
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            mStatusCode = response.code();

            mCacheResponse = (response.cacheResponse() != null);

            mContent = null;
            if (httpStatusToResultCode(mStatusCode) != HttpResponseResultCode.NO_CACHE) {
                mHeaders = parseHeaders(response.headers());
            }

            mResponseUrl = response.request().urlString();

            if (LOGGING_ENABLED)
                LogLastResponse();
        } catch (InterruptedIOException e) {
            e.printStackTrace();
            throw new InterruptedDownloadException();
        } catch (SocketException e) {
            e.printStackTrace();
            throw new OtherDownloadException();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new OtherDownloadException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new TimeoutDownloadException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new OtherDownloadException();
        }
    }

    public InputStream decompressStream(InputStream input) throws IOException {
        List<String> contentEncoding = (mHeaders.containsKey("Content-Encoding")) ? mHeaders.get("Content-Encoding") : mHeaders.get("content-encoding");
        if (contentEncoding != null && contentEncoding.contains("gzip"))
            return new GZIPInputStream(input);
        else
            return input;
    }


    public AAResponse getAlterApiResponse() {
        return getAlterApiResponse(null);
    }

    public AAResponse getAlterApiResponse(String responseTransform) {
        String response;
        try {
            response = IOUtils.toString(mContent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(mContent);
        }
        try {
            response = JQBridge.runTransform(responseTransform, response, AGApplicationState.getInstance().getContext());
        } catch (Exception e){

        }

        AAExtractor extractor;
        extractor = new AAJsonExtractor();
        AAResponse aaResponse = extractor.parse(response);
        if (aaResponse != null)
            return extractor.parse(response);
        extractor = new AAXmlExtractor();
        return extractor.parse(response);

    }

    private void updateCacheHitCounter() {
        cacheHitsBeforeLastExecution = cacheHitsAfterLastExecution;
        cacheHitsAfterLastExecution = OkHttpClientManager.getInstance().getClient().getCache().getHitCount();
    }

    private boolean wasLastRequestCached() {
        return cacheHitsAfterLastExecution > cacheHitsBeforeLastExecution;
    }

    private void LogLastResponse() {
        Logger.d(this, "executeRequest", String.format("Request was from cache %s", (wasLastRequestCached()) ? "true" : "false"));
        Logger.d(this, "executeRequest", String.format("Request status code: %s", mStatusCode));
        if (LOG_MESSAGE_CONTENT && mContent != null) {
            Logger.d(this, "executeRequest", "First 100 characters of the request:");
            Logger.d(this, "executeRequest", getFirst100CharactersFromStream(mContent));
        }
    }

    public String getFirst100CharactersFromStream(BufferedInputStream stream) {
        byte[] buffer = new byte[100];
        try {
            stream.mark(101);
            stream.read(buffer, 0, 100);
            stream.reset();
        } catch (Exception e) {
            return "";
        }
        return new String(buffer);
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public String getResponseUrl() {
        return mResponseUrl;
    }

    public InputStream getContent() {
        return mContent;
    }

    public Map<String, List<String>> getHeaders() {
        return mHeaders;
    }

    public boolean isCacheResponse() {
        return mCacheResponse;
    }

    private Map<String, List<String>> parseHeaders(Headers headers) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (String header : headers.names()) {
            map.put(header.toLowerCase(Locale.US), headers.values(header));
        }
        return map;
    }

    public enum HttpResponseResultCode {
        OK, BAD_REQUEST, INVALID_SESSION, NO_CACHE, OTHER
    }


    public enum RequestType {
        FORM, EMAIL, LOGIN, LOGOUT, FEED, IMAGE, OTHER, BASIC_AUTH_LOGIN, SILENT
    }

}

