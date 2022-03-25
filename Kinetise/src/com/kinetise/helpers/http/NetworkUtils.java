package com.kinetise.helpers.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.alterapimanager.AAResponse;
import com.kinetise.data.application.alterapimanager.AGOkHttpConfigurator;
import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.application.alterapimanager.HttpResponseHandler;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.helpers.http.exceptions.NoInternetException;
import com.kinetise.helpers.http.exceptions.TimeoutDownloadException;
import com.kinetise.helpers.http.exceptions.UnknownHostDownloadException;
import com.kinetise.helpers.time.DateParser;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NetworkUtils {

    public static final String HEADER_DATE = "Date";
    public static final String LOWER_HEADER_DATE = "date";

    public static boolean isNetworkAvailable() {
        if (AGApplicationState.getInstance() != null)
            return NetworkUtils.isNetworkAvailable(AGApplicationState.getInstance().getContext());
        else
            return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Date getHeaderDate(Map<String, List<String>> headers) {
        try {
            if (headers.containsKey(LOWER_HEADER_DATE)) {
                return getHeaderDate(headers.get(LOWER_HEADER_DATE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static Date getHeaderDate(List<String> dateHeader) throws Exception {
        if (dateHeader != null && dateHeader.size() == 1) {
            return getHeaderDate(dateHeader.get(0));
        } else
            return null;
    }

    private static Date getHeaderDate(String date) throws Exception {
        return DateParser.parseDate(date, new String[]
                {
                        DateParser.PATTERN_RFC1123,
                        DateParser.PATTERN_ASCTIME,
                        DateParser.PATTERN_RFC850
                });
    }

    public static String createPostBody(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (Map.Entry<String, String> pair : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getKey().trim(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue().trim(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            return "";
        }

        return result.toString();
    }

    public static void sendRequest(String url, Map<String, String> headers, String body, RequestCallback callback) {
        sendRequest(url, headers, body, HttpRequestManager.RequestType.SILENT, callback);
    }

    public static void sendRequest(String url, Map<String, String> headers, String body, HttpRequestManager.RequestType requestType, RequestCallback callback) {
        sendRequest(OkHttpClientManager.getInstance().getClient(),
                body != null ? AGHttpMethodType.POST : AGHttpMethodType.GET,
                url,
                headers,
                body,
                null,
                requestType,
                CacheControlOptions.NO_CACHE,
                callback
        );
    }

    public static void sendRequest(OkHttpClient client,
                                   AGHttpMethodType methodType,
                                   String url,
                                   Map<String, String> headers,
                                   String body,
                                   String httpResponseTransform,
                                   HttpRequestManager.RequestType requestType,
                                   CacheControlOptions cacheOption,
                                   RequestCallback callback) {
        Request.Builder builder;
        HttpRequestManager requestManager = new HttpRequestManager();
        try {
            if (methodType == AGHttpMethodType.GET) {
                builder = AGOkHttpConfigurator.configureOkHttpRequestForGet(url, headers);
            } else {
                builder = AGOkHttpConfigurator.configureOkHttpRequestForPost(url, headers);
                AGOkHttpConfigurator.setPost(builder, methodType, requestType, body);
            }
            switch(cacheOption){
                case NO_CACHE:
                    builder.cacheControl(new CacheControl.Builder()
                            .noCache()
                            .build());
                    break;
                case NO_STORE:
                    builder.cacheControl(new CacheControl.Builder()
                            .noStore()
                            .build());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailed(requestManager, new PopupMessage(true, HttpRequestManager.getErrorMessage(requestType)));
            return;
        }

        try {
            if (requestType != HttpRequestManager.RequestType.IMAGE && !NetworkUtils.isNetworkAvailable())
                throw new NoInternetException();
            requestManager.executeRequest(client, builder);
        } catch (NoInternetException e) {
            callback.onFailed(requestManager, new PopupMessage(true, LanguageManager.getInstance().getString(LanguageManager.ERROR_NO_CONNECTION)));
            return;
        } catch (UnknownHostDownloadException e) {
            callback.onFailed(requestManager, new PopupMessage(true, LanguageManager.getInstance().getString(LanguageManager.ERROR_COULD_NOT_RESOLVE_DOMAIN_NAME) + e.getHostName()));
            return;
        } catch (TimeoutDownloadException e) {
            callback.onFailed(requestManager, new PopupMessage(true, LanguageManager.getInstance().getString(LanguageManager.ERROR_CONNECTION_TIMEOUT)));
            return;
        } catch (Exception e) {
            callback.onFailed(requestManager, new PopupMessage(true, LanguageManager.getInstance().getString(LanguageManager.ERROR_CONNECTION)));
            return;
        }

        int statusCode = requestManager.getStatusCode();

        if (statusCode >= 200 && statusCode < 400) {
            callback.onResponse(requestManager);
        } else {
            AAResponse alterApiResponse = requestManager.getAlterApiResponse(httpResponseTransform);
            AlterApiManager.handleAAResponse(alterApiResponse);

            PopupMessage[] messages = HttpResponseHandler.getAlterApiMessages(alterApiResponse, LanguageManager.getInstance().getString(LanguageManager.POPUP_ERROR_HEADER));

            if ((statusCode == 401 || statusCode == 403) && AGApplicationState.hasLoginScreen()) {
                callback.onLogout();
                showErrorMessages(callback, requestManager, messages, new PopupMessage(true, LanguageManager.getInstance().getString(LanguageManager.ERROR_INVALID_SESSION)));
            } else {
                showErrorMessages(callback, requestManager, messages, HttpResponseHandler.getCustomHttpErrorMessage(statusCode));
            }
        }
    }

    private static void showErrorMessages(RequestCallback callback, HttpRequestManager requestManager, PopupMessage[] messages, PopupMessage customHttpErrorMessage) {
        if (messages.length > 0) {
            callback.onFailed(requestManager, messages);
        } else {
            callback.onFailed(requestManager, customHttpErrorMessage);
        }
    }

    public interface RequestCallback {
        void onFailed(HttpRequestManager requestManager, PopupMessage... messages);

        void onResponse(HttpRequestManager requestManager);

        void onLogout();
    }

}
