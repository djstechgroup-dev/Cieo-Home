package com.kinetise.helpers.gcm;

import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.kinetise.data.application.alterapimanager.AGOkHttpConfigurator;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.helpers.ApplicationIdGenerator;
import com.kinetise.helpers.RWrapper;
import com.kinetise.helpers.http.HttpRequestManager;
import com.kinetise.helpers.http.NetworkUtils;
import com.kinetise.helpers.http.TextResponseRequestCallback;
import com.kinetise.helpers.threading.AGAsyncTask;

import java.io.IOException;
import java.util.HashMap;

public class GCMRegisterRunnable extends AGAsyncTask {
    private Context mContext;
    private GCMManager.GCMRegisteredCallback mCallback;
    private String mOldRegistrationId;

    public GCMRegisterRunnable(Context context, String oldRegistrationId, GCMManager.GCMRegisteredCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        this.mOldRegistrationId = oldRegistrationId;
    }

    @Override
    public void run() {
        //get registration ID
        String token = getRegistrationID();

        //register in back-end server
        if (!TextUtils.isEmpty(token) && (mOldRegistrationId == null || !token.equals(mOldRegistrationId)))
            sendToServer(token);
    }

    private String getRegistrationID() {
        String sender = mContext.getString(RWrapper.string.push_google_project_number);
        String token = null;
        if (!mIsCanceled && !TextUtils.isEmpty(sender)) {
            try {
                token = InstanceID.getInstance(mContext).getToken(sender, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return token;
    }

    private void sendToServer(final String token) {
        if (!TextUtils.isEmpty(token)) {
            if (mIsCanceled) {
                return;
            }

            String regUrl = mContext.getResources().getString(RWrapper.string.push_registration_url);
            String pid = mContext.getResources().getString(RWrapper.string.push_project_id);
            RegisterBody registerBody = new RegisterBody(pid, ApplicationIdGenerator.getUUID(mContext), token, AGOkHttpConfigurator.KINETISE_OS_HEADER_VALUE);
            String body = new Gson().toJson(registerBody, RegisterBody.class);
            HashMap<String,String> headers =new HashMap<>();
            headers.put(AGOkHttpConfigurator.CONTENT_TYPE_HEADER_NAME, AGOkHttpConfigurator.CONTENT_TYPE_JSON);
            NetworkUtils.sendRequest(regUrl, headers, body, new TextResponseRequestCallback() {
                @Override
                public void onSuccess(String response) {
                    mCallback.onGCMRegistered(mContext, token);
                }

                @Override
                public void onFailed(HttpRequestManager requestManager, PopupMessage... messages) {
                    mCallback.onGCMRegisterFailed();
                }
            });
        }
    }

    private class RegisterBody {
        @SerializedName("projectid")
        public String projectid;

        @SerializedName("devicetoken")
        public String devicetoken;

        @SerializedName("registrationtoken")
        public String registrationtoken;

        @SerializedName("deviceos")
        public String deviceos;

        public RegisterBody(String projectID, String deviceID, String token, String deviceOS) {
            projectid = projectID;
            devicetoken = deviceID;
            registrationtoken = token;
            deviceos = deviceOS;
        }
    }
}
