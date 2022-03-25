package com.kinetise.data.application.alterapimanager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.ApplicationDescriptionDataDesc;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.helpers.DeviceInfo;
import com.kinetise.helpers.RWrapper;
import com.kinetise.helpers.http.HttpRequestManager;
import com.kinetise.helpers.http.exceptions.OtherDownloadException;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.ProtocolException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class AGOkHttpConfigurator {
    public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
    public static final String CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded; charset=utf-8";
    public static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";

    public static final String ACCEPT_ENCODING_HEADER_NAME = "Accept-Encoding";
    public static final String USER_AGENT_HEADER_NAME = "User-Agent";
    public static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    public static final String CONNECTION_HEADER_NAME = "Connection";
    public static final String KINETISE_HEADER_OS = "X-Kinetise-OS";
    public static final String KINETISE_HEADER_OS_VERSION = "X-Kinetise-OS-Version";
    public static final String KINETISE_HEADER_DEVICE = "X-Kinetise-Device";
    public static final String KINETISE_HEADER_APP_NAME = "X-Kinetise-App-Name";
    public static final String KINETISE_HEADER_APP_VERSION = "X-Kinetise-App-Version";
    public static final String KINETISE_HEADER_VERSION = "X-Kinetise-Version";
    public static final String KINETISE_HEADER_LAST_UPDATE_TOKEN = "X-Kinetise-Last-Update-Token";
    public static final String KINETISE_HEADER_API_VERSION = "X-Kinetise-API-Version";

    public static final String CONNECTION_HEADER_VALUE = "close";
    public static final String ACCEPT_ENCODING_HEADER_VALUE = "gzip";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_PRAGMA = "Pragma";
    public static final String HEADER_VARY = "Vary";
    public static final String HEADER_DATE = "Date";
    public static final String KINETISE_OS_HEADER_VALUE = "Android";

    public static final int CONNECTION_TIMEOUT_MILLIS = 30000;
    public static final int READ_TIMEOUT_MILLIS = 10000;

    public static OkHttpClient configureOkHttpClient() throws IOException {
        OkHttpClient client = new OkHttpClient();
        try {
            setUpSSLForConnection(client);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        setOkHttpClientParameters(client);

        setCookieManager(client);

        if (KinetiseApplication.getInstance().useStetho()) {
            client.networkInterceptors().add(new StethoInterceptor());
        }

        client.setConnectTimeout(30000, TimeUnit.SECONDS);
        client.setReadTimeout(30000, TimeUnit.SECONDS);
        client.setWriteTimeout(30000, TimeUnit.SECONDS);

        return client;
    }

    private static void setCookieManager(OkHttpClient client) {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);
    }

    public static Request.Builder configureOkHttpRequestForPost(String url, Map<String, String> headers) throws IOException, OtherDownloadException {
        Request.Builder builder = new Request.Builder();

        boolean isUrlCorrect = setUrl(builder, url);
        if (!isUrlCorrect)
            throw new OtherDownloadException();

        Request request = setCommonDefaultHeaders(headers, builder);

        setDefaultContentTypeHeader(builder, request);
        setDefaultConnectionHeader(builder, request);

        return builder;
    }

    public static Request.Builder configureOkHttpRequestForGet(String url, Map<String, String> headers) throws IOException, OtherDownloadException {
        Request.Builder builder = new Request.Builder();

        boolean isUrlCorrect = setUrl(builder, url);
        if (!isUrlCorrect)
            throw new OtherDownloadException();

        setCommonDefaultHeaders(headers, builder);

        return builder;
    }

    private static boolean setUrl(Request.Builder builder, String url) {
        url = url.replaceAll("\\s","%20");

        Uri uri = Uri.parse(url);

        Uri.Builder uriBuilder =  uri.buildUpon();
        uriBuilder.clearQuery();

        try {
            Set<String> queryNames = uri.getQueryParameterNames();
            for (String queryName : queryNames) {
                List<String> queryParameters = uri.getQueryParameters(queryName);

                for (String queryParameter : queryParameters) {
                    uriBuilder.appendQueryParameter(queryName, queryParameter);
                }
            }

            builder.url(uriBuilder.build().toString());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static Request setCommonDefaultHeaders(Map<String, String> headers, Request.Builder builder) throws ProtocolException {
        setRequestHeaders(headers, builder);

        Request request = builder.build();
        setDefaultUserAgentHeader(builder, request);
        setDefaultConnectionHeader(builder, request);
        setKinetiseHeaders(builder);

        setOkHttpRequestParameters(builder);
        return request;
    }

    public static void setKinetiseHeaders(Request.Builder builder) {
        String versionValue = Build.VERSION.RELEASE;
        String device = DeviceInfo.getDeviceName();

        Context context = AGApplicationState.getInstance().getContext();
        String appName = context.getString(RWrapper.string.app_name);
        String appVersion = "";
        String version = "";
        String apiVersion = "";
        PackageInfo pInfo = null;

        apiVersion = getApiVersion();
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion = getVersionName(pInfo);
            version = getVersionCode(pInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        builder.addHeader(KINETISE_HEADER_OS, KINETISE_OS_HEADER_VALUE);
        builder.addHeader(KINETISE_HEADER_OS_VERSION, versionValue);
        builder.addHeader(KINETISE_HEADER_DEVICE, device);
        builder.addHeader(KINETISE_HEADER_APP_NAME, appName);
        builder.addHeader(KINETISE_HEADER_APP_VERSION, appVersion);
        builder.addHeader(KINETISE_HEADER_VERSION, version);
        builder.addHeader(KINETISE_HEADER_API_VERSION, apiVersion);
    }

    private static String getVersionCode(PackageInfo pInfo) {
        if (pInfo != null)
            return Integer.toString(pInfo.versionCode);
        else
            return "";
    }

    private static String getVersionName(PackageInfo pInfo) {
        if (pInfo != null && pInfo.versionName != null)
            return pInfo.versionName;
        else return "";
    }

    private static String getApiVersion() {
        ApplicationDescriptionDataDesc applicationDescription =  AGApplicationState.getInstance().getApplicationDescription();
        if(applicationDescription!=null)
        return applicationDescription.getApiVersion();
        else return  "";
    }

    private static void setOkHttpClientParameters(OkHttpClient client) throws ProtocolException {
        client.setConnectTimeout(CONNECTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setFollowRedirects(true);
        client.setFollowSslRedirects(true);
    }

    private static void setOkHttpRequestParameters(Request.Builder request) throws ProtocolException {
        request.addHeader(ACCEPT_ENCODING_HEADER_NAME, ACCEPT_ENCODING_HEADER_VALUE);
    }

    private static void setDefaultContentTypeHeader(Request.Builder builder, Request request) {
        String contentTypeValue = request.header(CONTENT_TYPE_HEADER_NAME);
        if (contentTypeValue == null) {
            builder.addHeader(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_URL_ENCODED);
        } else {
            builder.removeHeader(CONTENT_TYPE_HEADER_NAME);
            builder.addHeader(CONTENT_TYPE_HEADER_NAME, contentTypeValue);
        }
    }

    private static void setDefaultUserAgentHeader(Request.Builder builder, Request request) {
        if (request.header(USER_AGENT_HEADER_NAME) == null && AGApplicationState.getInstance() != null) {
            ApplicationDescriptionDataDesc dataDesc = AGApplicationState.getInstance().getApplicationDescription();
            if (dataDesc != null) {
                String userAgent = dataDesc.getDefaultUserAgent();
                if (userAgent == null) {
                    userAgent = DEFAULT_USER_AGENT;
                }
                builder.addHeader("User-Agent", userAgent);
            }
        }
    }

    private static void setDefaultConnectionHeader(Request.Builder builder, Request request) {
        if (request.header(CONNECTION_HEADER_NAME) != null) {
            builder.removeHeader(CONNECTION_HEADER_NAME);
        }

        builder.addHeader(CONNECTION_HEADER_NAME, CONNECTION_HEADER_VALUE);
    }

    private static void setUpSSLForConnection(OkHttpClient client) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        }, null);
        client.setSslSocketFactory(ctx.getSocketFactory());
        client.setHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    private static void setRequestHeaders(Map<String, String> headers, Request.Builder builder) {
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
        }
    }

    public static void setPost(Request.Builder builder, AGHttpMethodType methodType, HttpRequestManager.RequestType requestType, String postBody) {
        if (postBody == null) {
            postBody = "";
        }
        MediaType type = getMediaTypeForRequest(requestType, builder.build().headers());
        RequestBody formBody = RequestBody.create(type, postBody);

        if (methodType == AGHttpMethodType.POST) {
            builder.post(formBody);
        } else if (methodType == AGHttpMethodType.PUT) {
            builder.put(formBody);
        } else if (methodType == AGHttpMethodType.DELETE) {
            builder.delete(formBody);
        } else if (methodType == AGHttpMethodType.PATCH) {
            builder.patch(formBody);
        }
    }

    private static MediaType getMediaTypeForRequest(HttpRequestManager.RequestType requestType, Headers headers) {
        MediaType type;
        if (headers.get(CONTENT_TYPE_HEADER_NAME) != null) {
            type = MediaType.parse(headers.get(CONTENT_TYPE_HEADER_NAME));
        } else {
            switch (requestType) {
                case SILENT:
                case FORM:
                case EMAIL:
                    type = MediaType.parse(AGOkHttpConfigurator.CONTENT_TYPE_JSON);
                    break;
                default:
                    type = MediaType.parse(AGOkHttpConfigurator.CONTENT_TYPE_URL_ENCODED);
            }
        }
        return type;
    }
}
