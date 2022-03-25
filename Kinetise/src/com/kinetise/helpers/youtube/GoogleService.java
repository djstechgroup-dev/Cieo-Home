package com.kinetise.helpers.youtube;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Intent;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kinetise.R;
import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.helpers.LoginCallback;
import com.kinetise.helpers.RWrapper;
import com.kinetise.helpers.threading.AGAsyncTask;
import com.kinetise.helpers.threading.ThreadPool;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.kinetise.helpers.asynccaller.AsyncCaller.runOnUiThread;

public class GoogleService {

    public static final int REQUEST_GOOGLE_LOGIN = 44;
    public static final int REQUEST_GOOGLE_ERROR = 45;
    public static final String SCOPE_STRING = "oauth2:profile email https://www.googleapis.com/auth/spreadsheets";

    private static GoogleService mInstance;
    private LoginCallback mLoginCallback;
    private String mToken;

    public static GoogleService getInstance() {
        if (mInstance == null) {
            mInstance = new GoogleService();
        }
        return mInstance;
    }

    public void login(LoginCallback callback) {
        mLoginCallback = callback;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(AGApplicationState.getInstance().getContext().getString(RWrapper.string.google_sign_in_client_id))
                .build();
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(KinetiseApplication.getInstance())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        AGApplicationState.getInstance().getActivity().startActivityForResult(signInIntent, REQUEST_GOOGLE_LOGIN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GOOGLE_LOGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else if (requestCode == REQUEST_GOOGLE_ERROR) {
            if (resultCode == RESULT_OK) {
                getAccessToken(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
            }
        }
    }

    private void getAccessToken(final String email) {
        AGAsyncTask task = new AGAsyncTask() {
            @Override
            public void run() {
                try {
                    Account account = new Account(email, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                    mToken = GoogleAuthUtil.getToken(AGApplicationState.getInstance().getContext(), account, SCOPE_STRING);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mLoginCallback != null)
                                mLoginCallback.onLoginSuccess(mToken);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mLoginCallback != null)
                                mLoginCallback.onFailed();
                        }
                    });
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                    handleException(e);
                }
            }
        };
        ThreadPool.getInstance().execute(task);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            getAccessToken(acct.getEmail());
        } else {
            if (mLoginCallback != null)
                mLoginCallback.onFailed();
        }
    }

    public void handleException(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    int statusCode = ((GooglePlayServicesAvailabilityException) e).getConnectionStatusCode();
                    Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(AGApplicationState.getInstance().getActivity(), statusCode, REQUEST_GOOGLE_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    AGApplicationState.getInstance().getActivity().startActivityForResult(intent, REQUEST_GOOGLE_ERROR);
                } else {
                    if (mLoginCallback != null)
                        mLoginCallback.onFailed();
                }
            }
        });
    }

    public String getGoogleToken() {
        return mToken;
    }

}
