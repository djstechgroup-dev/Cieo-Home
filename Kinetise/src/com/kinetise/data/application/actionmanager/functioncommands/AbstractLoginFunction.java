package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.application.alterapimanager.AGOkHttpConfigurator;
import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.MultiActionDataDesc;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.helpers.LoginCallback;
import com.kinetise.helpers.http.NetworkUtils;
import com.kinetise.helpers.jq.JQBridge;
import com.kinetise.helpers.threading.AGAsyncTask;
import com.kinetise.helpers.threading.ThreadPool;
import com.kinetise.helpers.threading.UncancelableTask;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractLoginFunction extends AbstractAlterAPIFunction implements LoginCallback {

    protected static final String ACCESS_TOKEN_PARAM_NAME = "access_token";

    protected MultiActionDataDesc mActionDataDesc;
    protected String mAlterApiUrl;
    protected HttpParamsDataDesc mAlterApiHttpParams;
    protected HttpParamsDataDesc mAlterApiHeaderParams;
    protected HttpParamsDataDesc mAlterApiBodyParams;
    protected AGHttpMethodType mHttpMethod;
    protected String mContentType;
    protected String mRequestTransform;
    protected String mResponseTransform;

    public AbstractLoginFunction(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
        mAlterApiHttpParams = new HttpParamsDataDesc();
        mAlterApiHeaderParams = new HttpParamsDataDesc();
        mAlterApiBodyParams = new HttpParamsDataDesc();
    }

    @Override
    public void onLoginSuccess(String accessToken) {
        if (mAlterApiUrl != null && !mAlterApiUrl.equals("")) {
            loginToAlterApi(accessToken);
        } else {
            AlterApiManager.setAlterApiSessionId(accessToken);
            onSuccess();
            finishAction();
        }
    }

    @Override
    public void onFailed() {
    }

    protected void loginToAlterApi(String accessToken) {
        SystemDisplay display = AGApplicationState.getInstance().getSystemDisplay();
        final AlterApiManager alterApiManager = AGApplicationState.getInstance().getAlterApiManager();

        display.blockScreenWithLoadingDialog(true);

        final String postBody = formatBody(accessToken);

        if (mContentType!=null)
            setContentType(mAlterApiHeaderParams);

        Runnable r = new Runnable() {

            @Override
            public void run() {
                alterApiManager.login( //TODO jak już wszystkie loginy będą uspójnione to można wyrzucić tą logikę wstecznej kompatybilności (sprawdzenia nulli)
                        mHttpMethod != null ? mHttpMethod : AGHttpMethodType.POST,
                        mAlterApiUrl,
                        postBody,
                        mAlterApiHttpParams,
                        mAlterApiHeaderParams,
                        mResponseTransform,
                        AbstractLoginFunction.this);
            }
        };

        AGAsyncTask task = new UncancelableTask(r);
        ThreadPool.getInstance().execute(task);
    }


    protected String formatBody(String accessToken) {
        if (mContentType != null && mContentType.contains(AGOkHttpConfigurator.CONTENT_TYPE_JSON)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(ACCESS_TOKEN_PARAM_NAME, accessToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return JQBridge.runTransform(mRequestTransform, jsonObject.toString(), AGApplicationState.getInstance().getContext());
        } else {
            mAlterApiBodyParams.addHttpParam(ACCESS_TOKEN_PARAM_NAME, accessToken);
            return NetworkUtils.createPostBody(mAlterApiBodyParams.getHttpParamsAsHashMap());
        }
    }

    private void setContentType(HttpParamsDataDesc headerParams) {
        headerParams.addHttpParam(AGOkHttpConfigurator.CONTENT_TYPE_HEADER_NAME, mContentType);
    }

    @Override
    public void onSuccess(PopupMessage... messages) {
        if (mActionDataDesc != null)
            ExecuteActionManager.executeMultiAction(mActionDataDesc);
        super.onSuccess(messages);
    }
}
