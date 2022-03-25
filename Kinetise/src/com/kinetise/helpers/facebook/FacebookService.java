package com.kinetise.helpers.facebook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.helpers.RWrapper;
import com.kinetise.helpers.http.HttpRequestManager;
import com.kinetise.helpers.http.NetworkUtils;
import com.kinetise.helpers.http.TextResponseRequestCallback;
import com.kinetise.helpers.preferences.SecurePreferencesHelper;
import com.kinetise.helpers.threading.ThreadPool;
import com.kinetise.helpers.threading.UncancelableTask;
import com.kinetise.support.logger.Logger;

import java.util.Arrays;
import java.util.List;

public class FacebookService {

    private static final String SHARED_PREFERENCE_FACEBOOK_TOKEN = "sharedFacebookToken";
    private static final double MIN_TOKEN_UPDATE_DELAY = 5 * 60 * 1000; //5 minutes
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

    private static FacebookService mInstance;

    private Context mContext;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private PendingAction pendingAction = PendingAction.NONE;
    private String mPendingMessage;
    private String mPendingName;
    private String mPendingLink;
    private String mPendingPicture;
    private String mPendingCaption;
    private String mFacebookToken = "";
    private boolean mExternalApplicationEnabled;
    private double mLastTokenUpdate;

    /**
     * Sets current app dictionary
     *
     * @param pDictionary application dictionary
     */

    /**
     * Sets information that External Application is enabled at the moment
     *
     * @param isExternalApplicationEnabled
     */
    public void setExternalApplication(boolean isExternalApplicationEnabled) {
        mExternalApplicationEnabled = isExternalApplicationEnabled;
    }

    private enum PendingAction {
        NONE,
        /**
         * Status updateDate pending action
         */
        POST_STATUS_UPDATE,
        /**
         * with this flag status will be updated after successful login
         */
        POST_STATUS_UPDATE_AFTER_LOGIN
    }

    public Session.StatusCallback getSessionStatusCallback() {
        return statusCallback;
    }

    private FacebookService(Context context) {
        mContext = context;
    }

    private FacebookService() {
    }

    static public FacebookService getInstance() {
        if (mInstance == null) {
            synchronized (FacebookService.class){
                if (mInstance == null) {
                    mInstance = new FacebookService(AGApplicationState.getInstance().getContext());
                }
            }
        }
        return mInstance;
    }

    public static void clearInstance() {
        mInstance = null;
    }

    /**
     * Posts to facebook wall asynchronously
     *
     * @param messsage    to post
     * @param pName       The name of the link attachment.
     * @param pPictureUrl The URL of a picture attached to this post. The picture must be at least 200px by 200px.
     * @param pLink       The link attached to this post
     */
    public void postToFacebookWallAsync(final String messsage, final String pName, final String pPictureUrl, final String pLink, final String caption) {
        postToFacebookWall(messsage, pName, pPictureUrl, pLink, caption);
    }

    /**
     * Calls facebook dialog to post on user's wall, if user is not logged into facebook than it will automatically handle login
     * process before. Check loginToFacebook(Context context); for login instructions
     * for rest of params check {@link #postToFacebookWallAsync(String, String, String, String, String)}
     */
    public void postToFacebookWall(String messsage, String pName, String pPictureUrl, String pLink, String caption) {
        mPendingMessage = messsage;
        mPendingName = pName;
        mPendingLink = pLink;
        mPendingPicture = pPictureUrl;
        mPendingCaption = caption;
        postToWall();
    }

    /**
     * Initiates post to wall action, at this stage all values like, Message, Name, Description, Link, Picture should be set.
     * If user need to login first displays login dialog
     */
    private void postToWall() {
        if (checkFacebookLogin()) {
            performPublish();
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE_AFTER_LOGIN;
            loginToFacebook();
        }

    }

    /**
     * Checks if app has publish permissions
     *
     * @return true if app has permission to publish posts on facebook wall, false otherwise
     */
    private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains("publish_actions");
    }

    /**
     * Initiates publish action, if app has no publish permissions, than ask for them, otherwise call action to post
     */
    private void performPublish() {

        Session session = Session.getActiveSession();
        pendingAction = PendingAction.POST_STATUS_UPDATE;

        if (session != null && AGApplicationState.getInstance().getActivity() != null) {
            if (hasPublishPermission()) {
                // We can do the action right away.
                handlePendingAction();
            } else {
                // We need to get new permissions, then complete the action when we get called back.
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(AGApplicationState.getInstance().getActivity(), PERMISSIONS));
            }
        }
    }

    /**
     * Handles Pending action, depending on type of it.
     */
    @SuppressWarnings("incomplete-switch")
    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case POST_STATUS_UPDATE:
                publishStory();
//                checkAndResetSession(Session.getActiveSession());
                break;
            default:
                break;
        }
    }

    //This method will be used probably in future for better publish result propagation
    @SuppressWarnings("unused")
    private void showPublishResult(String message, FacebookRequestError error) {
        String title;
        String alertMessage;
        if (error == null) {
            title = mContext.getString(RWrapper.string.app_name);
            alertMessage = mContext.getString(RWrapper.string.app_name);
        } else {
            title = mContext.getString(RWrapper.string.app_name);
            alertMessage = error.getErrorMessage();
        }

        if (AGApplicationState.getInstance().getActivity() != null) {
            new AlertDialog.Builder(AGApplicationState.getInstance().getActivity())
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton(RWrapper.string.app_name, null)
                    .show();
        }
    }

    /**
     * Presents user FeedDialog in which he can post to facebook wall.
     */
    private void publishStory() {
        Session session = Session.getActiveSession();

        if (session != null && AGApplicationState.getInstance().getActivity() != null) {
            Bundle postParams = new Bundle();
            postParams.putString("name", mPendingName);
            postParams.putString("description", mPendingMessage);
            postParams.putString("caption", mPendingCaption);
            mPendingMessage = null;
            postParams.putString("link", mPendingLink);
            postParams.putString("picture", mPendingPicture);

            WebDialog feedDialog = (
                    new WebDialog.FeedDialogBuilder(AGApplicationState.getInstance().getActivity(),
                            Session.getActiveSession(),
                            postParams))
                    .setOnCompleteListener(new OnCompleteListener() {

                        @Override
                        public void onComplete(Bundle values,
                                               FacebookException error) {

                            if (error == null) {
                                // When the story is posted, echo the success
                                // and the post Id.
                                final String postId = values.getString("post_id");
                                if (postId != null) {
                                    PopupManager.showInfoPopup(LanguageManager.getInstance().getString(LanguageManager.FACEBOOK_POST_SUCCESS));
                                }
                            } else if (error instanceof FacebookOperationCanceledException) {

                            } else {
                                PopupManager.showErrorPopup(LanguageManager.getInstance().getString(LanguageManager.FACEBOOK_POST_FAILED));
                            }
                        }

                    })
                    .build();
            feedDialog.show();
        }
    }

    /**
     * Checks if user is logged into facebook, remember that this method checks this offline, if you want to check it online
     * which helps avoid problem when user clears his apps settings on facebook than use checkAndResetSession
     *
     * @return true if if session is opened, false otherwise
     */
    private boolean checkFacebookLogin() {
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            return true;
        }
        return false;
    }

    /**
     * Inits session login
     */
    public void loginToFacebook(Session.StatusCallback callback) {
        Session session = Session.getActiveSession();
        if (AGApplicationState.getInstance().getActivity() != null) {
            if (session != null) {
                if (!session.isOpened() && !session.isClosed()) {
                    session.openForRead(new Session.OpenRequest(AGApplicationState.getInstance().getActivity()).setCallback(callback));
                } else {
                    Session.setActiveSession(null);
                    Session.openActiveSession(AGApplicationState.getInstance().getActivity(), true, callback);
                }
            } else {
                Session.openActiveSession(AGApplicationState.getInstance().getActivity(), true, callback);
            }
        }
    }

    private void loginToFacebook() {
        loginToFacebook(statusCallback);
    }

    /**
     * @return true if facebook app is currently enabled, false otherwise
     */
    public boolean isFacebookAppEnabled() {
        return mExternalApplicationEnabled;
    }

    /**
     * Class used to get notifications about changes to facebook Session status
     */
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (pendingAction != PendingAction.NONE &&
                    (exception instanceof FacebookOperationCanceledException ||
                            exception instanceof FacebookAuthorizationException)) {
                pendingAction = PendingAction.NONE;
                Toast.makeText(mContext, LanguageManager.getInstance().getString(LanguageManager.FACEBOOK_INIT), Toast.LENGTH_SHORT).show();
                if (session != null) {
                    session.removeCallback(this);
                    session.closeAndClearTokenInformation();
                }
            } else if (session.isOpened()) {
                if (pendingAction == PendingAction.POST_STATUS_UPDATE_AFTER_LOGIN) {
                    performPublish();
                } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
                    handlePendingAction();
                }
            }
        }
    }

    /**
     * Retrieve facebook token from facebook api
     */
    public void retrieveAccessToken() {
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            final long currentTime = System.currentTimeMillis();
            if (currentTime - mLastTokenUpdate > MIN_TOKEN_UPDATE_DELAY) {
                mLastTokenUpdate = currentTime;
                String url = mContext.getString(RWrapper.string.fb_auth_url);

                final String finalUrl = url + '?' + mContext.getString(RWrapper.string.fb_client_id_key) + '=' + mContext.getString(RWrapper.string.fb_client_id_value) + '&'
                        + mContext.getString(RWrapper.string.fb_client_secret_key) + '=' + mContext.getString(RWrapper.string.fb_client_secret_value) + '&'
                        + "grant_type=client_credentials";


                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        NetworkUtils.sendRequest(finalUrl, null, null, new TextResponseRequestCallback() {
                            @Override
                            public void onSuccess(String response) {
                                httpRequestCompleted(response);
                            }

                            @Override
                            public void onFailed(HttpRequestManager requestManager, PopupMessage... messages) {
                                mLastTokenUpdate = 0;
                            }
                        });
                    }
                };

                ThreadPool.getInstance().executeBackground(new UncancelableTask(runnable));
            }
        } else {
            SharedPreferences preferences = SecurePreferencesHelper.getUserData();
            if (preferences.contains(SHARED_PREFERENCE_FACEBOOK_TOKEN)) {
                mFacebookToken = preferences.getString(SHARED_PREFERENCE_FACEBOOK_TOKEN, null);
            }
        }
    }

    public void httpRequestCompleted(String response) {
        if (response != null) {
            response = response.substring(response.indexOf('=') + 1);
            mFacebookToken = response;
            SharedPreferences preferences = SecurePreferencesHelper.getUserData();
            preferences.edit().putString(SHARED_PREFERENCE_FACEBOOK_TOKEN, mFacebookToken).apply();
            Logger.v(this, "httpRequestCompleted", "FacebookToken is " + response);
        } else {
            mLastTokenUpdate = 0;
        }
    }

    /**
     * Returns current facebook token
     *
     * @return current facebook token
     */
    public String getFacebookToken() {
        return mFacebookToken;
    }

}

