package com.kinetise.data.application.sdk;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.kinetise.components.activity.ScanCodeActivity;
import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.components.services.LocationService;
import com.kinetise.data.VariableStorage;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.application.alterapimanager.AGOkHttpConfigurator;
import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.application.alterapimanager.IRequestCallback;
import com.kinetise.data.application.externalapplications.OpenGalleryApp;
import com.kinetise.data.application.externalapplications.PostToFacebookApp;
import com.kinetise.data.application.externalapplications.WebBrowserApp;
import com.kinetise.data.application.externalapplications.YouTubePlayerApp;
import com.kinetise.data.application.feedmanager.FeedManager;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.application.loginmanager.BasicAuthLoginManager;
import com.kinetise.data.application.overalymanager.OverlayManager;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.application.screenhistory.ApplicationState;
import com.kinetise.data.application.screenloader.ScreenLoader;
import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.MultiActionDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGDataFeedDataDesc;
import com.kinetise.data.descriptors.datadescriptors.OverlayDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.AGItemTemplateDataDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.FindDescendantByIdVisitor;
import com.kinetise.data.descriptors.types.AGScreenTransition;
import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.packagemanager.AppPackage;
import com.kinetise.data.packagemanager.AppPackageManager;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.data.sourcemanager.BitmapCache;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.helpers.IPermissionListener;
import com.kinetise.data.systemdisplay.helpers.IPermissionRequestListener;
import com.kinetise.data.systemdisplay.helpers.PermissionManager;
import com.kinetise.helpers.ApplicationIdGenerator;
import com.kinetise.helpers.SalesForceHelper;
import com.kinetise.helpers.TwitterService;
import com.kinetise.helpers.encoding.Base64;
import com.kinetise.helpers.encoding.MD5encoder;
import com.kinetise.helpers.encoding.SHAencoder;
import com.kinetise.helpers.facebook.FacebookService;
import com.kinetise.helpers.http.DownloadFileRunnable;
import com.kinetise.helpers.locationhelper.LocationHelper;
import com.kinetise.helpers.math.ExpresionEvaluator;
import com.kinetise.helpers.regexp.RegexpHelper;
import com.kinetise.helpers.threading.ThreadPool;
import com.kinetise.helpers.time.DateParser;
import com.kinetise.helpers.youtube.GoogleService;
import com.kinetise.helpers.youtube.YoutubeHandler;
import com.kinetise.support.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;


public class ActionManager {
    public final static String BASE64 = "base64";
    public final static String MD5 = "md5";
    public final static String SHA = "sha1";
    public static final String UTF_8 = "UTF-8";
    private final static String URL = "url";
    public static final String VIDEO_INTENT_TYPE = "video/mp4";

    //getCurrentTime
    private static final String RFC3339_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String TIMEZONE = "UTC";

    private static ActionManager actionManager;

    private ActionManager() {
    }

    public static ActionManager getInstance() {
        if (actionManager == null) {
            actionManager = new ActionManager();
        }
        return actionManager;
    }

    /**
     * Changes the screen that is presented to the user. Can have one of many different transittions.
     * @param mNextScreenId Id of the screen you want to switch to
     * @param transition Enum with transition to use when switching between screens
     */
    public void goToScreen(String mNextScreenId, AGScreenTransition transition) {
        synchronized (ExecuteActionManager.FUNCTION_SYNCHRONIZER) {
            AGApplicationState applicationState = AGApplicationState.getInstance();
            ScreenLoader loader = applicationState.getScreenLoader();
            if (loader != null)
                loader.loadNextScreen(mNextScreenId, transition);
        }
    }

    /**
     * Changes current screen to the screen presented 'steps' screen changes before.
     * @param steps number of steps to go back. Value of 1 will go back to previous screen.
     * @param transition Enum with transition to use when switching between screens
     */
    public void backBySteps(int steps, AGScreenTransition transition) {
        AGApplicationState applicationState = AGApplicationState.getInstance();
        ApplicationState state = applicationState.getHistoryManager().getScreenBySteps(steps);
        applicationState.getScreenLoader().loadApplicationState(state, transition);
    }

    /**
     * Changes current screen back to the screen with given screenId rewinding backStack to the first occurance of the screen with given Id.
     * If screen with given ID was not on the backstack, goes back to applications main screen.
     * @param screenId id of the screen to go back to
     * @param transition Enum with transition to use when switching between screens
     */
    public void backToScreen(String screenId, AGScreenTransition transition) {
        AGApplicationState applicationState = AGApplicationState.getInstance();
        ApplicationState state = applicationState.getHistoryManager().getScreenByID(screenId);
        if (state != null)
            applicationState.getScreenLoader().loadApplicationState(state, transition);
    }

    /**
     * Executes pull to refresh action, refreshing all feeds on screen and overlay
     */
    public void refresh() {
        FeedManager.executePullToRefreshForScreen(AGApplicationState.getInstance().getCurrentScreenDesc());
        if (OverlayManager.getInstance().isOverlayShown())
            FeedManager.executePullToRefreshForOverlay(OverlayManager.getInstance().getCurrentOverlayViewDataDesc());
    }

    /**
     * Reloads currently open screen, recalculating all dynamic values, layouting and redrawing all views and redownloading all feeds
     */
    public void reload() {
        AGApplicationState applicationState = AGApplicationState.getInstance();
        if (OverlayManager.getInstance().isOverlayShown()) {
            OverlayManager.getInstance().getCurrentOverlayViewDataDesc().resolveVariables();
        }

        applicationState.getCurrentScreenDesc().resolveVariables();
        applicationState.getSystemDisplay().recalculateAndLayoutScreen();

        refresh();
    }

    /**
     * switches screen to screen which id matches 'nextScreenId' setting data feed as screens context
     * @param itemIndex index of the item in data feed that will be a context for the screen
     * @param nextScreenId id of the screen to switch to
     * @param context this is the DataDescriptor of the control that holds the feed, has to implement IFeedClient
     * @param transition Enum with transition to use when switching between screens
     */
    public void goToScreenWithContext(int itemIndex, String nextScreenId, AbstractAGViewDataDesc context, AGScreenTransition transition) {
        synchronized (ExecuteActionManager.FUNCTION_SYNCHRONIZER) {
            try {
                if (context instanceof IFeedClient) {
                    IFeedClient feedClient = (IFeedClient) context;
                    if (itemIndex == -1)
                        itemIndex = feedClient.getActiveItemIndex();
                    feedClient.setActiveItemIndex(itemIndex);
                    DataFeedItem feedItem = feedClient.getFeedDescriptor().getItem(itemIndex);
                    String alterApiContext = feedItem.getAlterApiContext();
                    String guid = feedItem.getGUID();
                    callLoadNextScreen(nextScreenId, context.copy(), alterApiContext, guid, transition);
                } else {
                    callLoadNextScreen(nextScreenId, null, null, null, transition);
                }
            } catch (Exception e) {
                ExceptionManager.getInstance().handleException(e);
            }
        }
    }

    static private void callLoadNextScreen(String nextScreenId, AbstractAGViewDataDesc context, String alterApiContext, String guid, AGScreenTransition transition) {
        ScreenLoader loader = AGApplicationState.getInstance().getScreenLoader();
        loader.loadNextScreen(nextScreenId, transition, context, alterApiContext, guid, true);
    }

    /**
     * Returns the descriptor of control with given id inside root descriptor.
     * If root is null it will search whole screen
     * @param root a root of the hierarchy to search
     * @param controlId id of the control to search for
     * @return returns descriptor with matching id, or null if no such descriptor was found
     */
    public Object getControl(@Nullable AbstractAGElementDataDesc root, String controlId) {
        AGScreenDataDesc screenDataDesc;
        screenDataDesc = AGApplicationState.getInstance().getCurrentScreenDesc();
        if (root == null && screenDataDesc != null) {
            IFeedClient feedClient = screenDataDesc.getFeedClient(controlId);
            if (feedClient != null)
                return feedClient;
        }

        FindDescendantByIdVisitor visitor = new FindDescendantByIdVisitor(controlId);
        if (root == null) {
            if (screenDataDesc != null) {
                AbstractAGElementDataDesc searchParent = screenDataDesc;
                searchParent.accept(visitor);
                if (visitor.getFoundDataDesc() == null && OverlayManager.getInstance().isOverlayShown()) {
                    searchParent = OverlayManager.getInstance().getCurrentOverlayViewDataDesc();
                    searchParent.accept(visitor);
                }
                if (visitor.getFoundDataDesc() == null && PopupManager.getCurrentPopupView() != null) {
                    AbstractAGElementDataDesc popupDescriptor = PopupManager.getCurrentPopupView().getDescriptor();
                    popupDescriptor.accept(visitor);
                }
            }
        } else {
             root.accept(visitor);
        }

        return visitor.getFoundDataDesc();
    }

    /**
     * Closes any popup that is currently open
     */
    public void closePopup() {
        PopupManager.closeMapPopup();
    }

    public void showToast(String text) {PopupManager.showToast(text);}

    /**
     * Changes context of the screen to next item in feed, for screens that are detail screens.
     * Reloads the screen with new context.
     * @param transition Enum with transition to use when switching between screens
     */
    public void nextElement(AGScreenTransition transition) {
        ApplicationState applicationState = AGApplicationState.getInstance().getApplicationState();
        AbstractAGDataFeedDataDesc context = (AbstractAGDataFeedDataDesc) applicationState.getContext();
        DataFeed dataFeed = context.getFeedDescriptor();
        int activeIndex = context.getActiveItemIndex();
        int nextIndex = activeIndex + 1;
        int itemsCount = dataFeed.getItemsCount();

        for (; canShowNextElement(context, itemsCount, nextIndex); ++nextIndex) {
            DataFeedItem item = dataFeed.getItem(nextIndex);
            AGItemTemplateDataDesc matchingTempleteDataDesc = context.getMatchingTemplete(item);

            if (matchingTempleteDataDesc != null) {
                String detailScreenId = matchingTempleteDataDesc.getDetailScreenId();

                if (detailScreenId != null && !detailScreenId.equals("")) {
                    context.setActiveItemIndex(nextIndex);
                    ScreenLoader loader = AGApplicationState.getInstance().getScreenLoader();
                    String alterApiContext = item.getAlterApiContext();
                    String guid = item.getGUID();
                    loader.loadNextScreen(detailScreenId, transition, context, alterApiContext, guid, false);
                    return;
                }
            }
        }
    }


    private boolean canShowNextElement(AbstractAGDataFeedDataDesc context, int itemsCount, int nextIndex) {
        return (nextIndex < itemsCount) && ((nextIndex <= context.getLastItemIndex()) || (context.getLoadMoreTemplate() != null));
    }

    /**
     * Changes context of the screen to previous item in feed, for screens that are detail screens.
     * Reloads the screen with new context.
     * @param transition Enum with transition to use when switching between screens
     */
    public void previousElement(AGScreenTransition transition) {
        ApplicationState appState = AGApplicationState.getInstance().getApplicationState();
        AbstractAGDataFeedDataDesc context = (AbstractAGDataFeedDataDesc) appState
                .getContext();
        DataFeed dataFeed = context.getFeedDescriptor();

        int previousIndex = context.getActiveItemIndex() - 1;


        for (; previousIndex >= 0; --previousIndex) {
            DataFeedItem item = dataFeed.getItem(previousIndex);
            String detailScreenId = context.getMatchingTemplete(item).getDetailScreenId();

            if (detailScreenId != null && !detailScreenId.equals("")) {
                context.setActiveItemIndex(previousIndex);
                ScreenLoader loader = AGApplicationState.getInstance().getScreenLoader();
                String alterApiContext = item.getAlterApiContext();
                String guid = item.getGUID();
                loader.loadNextScreen(detailScreenId, transition, context, alterApiContext, guid, false);
                break;
            }
        }
    }

    /**
     * Opens youtube application on a video with id 'videoId'
     * @param videoId id of a video to open Youtube at
     */
    public void showInYoutubePlayer(String videoId) {
        SystemDisplay display = AGApplicationState.getInstance().getSystemDisplay();
        YouTubePlayerApp youtubeApplication = new YouTubePlayerApp(videoId);
        display.openExternalApplication(youtubeApplication);
    }

    /**
     * load previously oppened screen
     * @param transition Enum with transition to use when switching between screens
     */
    public void goToPreviousScreen(AGScreenTransition transition) {
        AGApplicationState.getInstance().getScreenLoader().loadPreviousScreen(transition);
    }

    /**
     * Returns the control that is context for current detail screen
     * @return returns context control or null if screen has no context
     */
    public AbstractAGElementDataDesc getContext() {
        ApplicationState appState = AGApplicationState.getInstance().getApplicationState();
        AbstractAGElementDataDesc state = appState.getContext();
        return state;
    }

    public String getActiveItemField(Object desc, String fieldId) {
        if (desc != null && !(desc instanceof IFeedClient)) {
            Logger.e(this, "execute", String.format("Descriptor %s should implement IFeedClient interface.", desc.toString()));
            throw new IllegalArgumentException("Descriptor should implement IFeedClient interface");
        } else if (desc == null) {
            return LanguageManager.getInstance().getString(LanguageManager.NODE_NOT_FOUND);
        }

        int feedItemIndex = ((IFeedClient) desc).getActiveItemIndex();
        DataFeed feedDesc = ((IFeedClient) desc).getFeedDescriptor();

        if (feedItemIndex < feedDesc.getItemsCount()) {
            DataFeedItem itemDescriptor = feedDesc.getItem(feedItemIndex);
            Object value;

            if (itemDescriptor != null && itemDescriptor.getByKey(fieldId) != null) {
                value = itemDescriptor.getByKey(fieldId);
            } else {
                value = LanguageManager.getInstance().getString(LanguageManager.NODE_NOT_FOUND);
            }

            return value.toString();
        } else {
            throw new IllegalArgumentException(
                    "There is less feed elements than active item index");
        }
    }

    public void postToFacebook(String appName, String caption, String link, String picture, String description) {
        PostToFacebookApp fbApp = new PostToFacebookApp(appName, caption, link, picture, description);
        SystemDisplay display = AGApplicationState.getInstance().getSystemDisplay();
        display.openExternalApplication(fbApp);
    }

    public void openGallery() {
        OpenGalleryApp openGalleryApp = new OpenGalleryApp();
        SystemDisplay display = AGApplicationState.getInstance().getSystemDisplay();
        display.openExternalApplication(openGalleryApp);
    }


    public String getGpsAccuracy() {
        Location location = LocationHelper.getInstance().getLastKnownLocation();

        String result;
        if (location != null) {
            result = Double.toString(location.getAccuracy());
        } else {
            result = "";
        }
        return result;
    }

    public String getGpsLatitude() {
        Location location = LocationHelper.getInstance().getLastKnownLocation();
        String result;
        if (location != null) {
            result = Double.toString(location.getLatitude());
        } else {
            result = "";
        }
        return result;
    }

    public String getGpsLongitude() {
        Location location = LocationHelper.getInstance().getLastKnownLocation();
        if (location != null) {
            return Double.toString(location.getLongitude());
        } else {
            return "";
        }
    }

    public String regex(String text, String patternName) {
        return RegexpHelper.parseValue(patternName, text);
    }

    public void call(String telephoneNumber) {
        String uri = "tel:" + telephoneNumber.trim();
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse(uri));
        AGApplicationState.getInstance().getActivity().startActivity(callIntent);
    }

    public void openEmail(String subject, String emailBody, String emailAddress) {
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Uri.encode(emailAddress)));
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
        Context context = AGApplicationState.getInstance().getActivity();
        try {
            context.startActivity(Intent.createChooser(sendIntent, "Send email"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void openSms(String phoneNumber, String message) {
        Uri smsUri = Uri.parse("sms:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
        intent.putExtra("sms_body", message);

        Context context = AGApplicationState.getInstance().getActivity();
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "There are no message clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    public String encode(String encodingType, String stringToEncode) {
        String result = "";
        try {
            if (encodingType.equals(BASE64)) {
                result = new String(Base64.encodeBase64(stringToEncode.getBytes()), UTF_8);
            } else if (encodingType.equals(MD5)) {
                result = MD5encoder.encode(stringToEncode);
            } else if (encodingType.equals(SHA)) {
                result = SHAencoder.encode(stringToEncode);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String decode(String encodingType, String stringToDecode) {
        String result;
        try {
            if (encodingType.equals(URL)) {
                result = URLDecoder.decode(stringToDecode, "UTF-8");
            } else if (encodingType.equals(BASE64)) {
                byte[] data = android.util.Base64.decode(stringToDecode, android.util.Base64.DEFAULT);
                result = new String(data, "UTF-8");
            } else {
                throw new IllegalArgumentException("Unsupported encoding type in function decode");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            result = "";
        }

        return result;
    }

    public void addCalendarEvent(String title, String description, String location, String startDateString, String endDateString, String isAllDayText) {
        Date startDate;
        try {
            startDate = DateParser.tryParseDate(startDateString);
        } catch (ParseException e) {
            e.printStackTrace();
            startDate = Calendar.getInstance().getTime();
        }

        Date endDate;
        try {
            endDate = DateParser.tryParseDate(endDateString);
        } catch (ParseException e) {
            e.printStackTrace();
            endDate = Calendar.getInstance().getTime();
        }

        boolean isAllDay = (isAllDayText.equals("yes")) ? true : false;

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, title);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, description);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate.getTime());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate.getTime());
        intent.putExtra(CalendarContract.Events.ALL_DAY, isAllDay);
        Activity currentActivity = AGApplicationState.getInstance().getActivity();
        currentActivity.startActivity(intent);
    }

    public void openMapCurrentLocation(String startLat, String startLng) {
        String uri = String.format(Locale.US, "http://maps.google.com/maps?q=%s,%s", startLat, startLng);
        Intent navigateIntent = new Intent(Intent.ACTION_VIEW);
        navigateIntent.setData(Uri.parse(uri));
        AGApplicationState.getInstance().getActivity().startActivity(navigateIntent);
    }

    public void openMap(String startLat, String startLng, String endLat, String endLng) {
        String uri = String.format(Locale.US, "http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s", startLat, startLng, endLat, endLng);
        Intent navigateIntent = new Intent(Intent.ACTION_VIEW);
        navigateIntent.setData(Uri.parse(uri));
        AGApplicationState.getInstance().getActivity().startActivity(navigateIntent);
    }

    public void showInVideoPlayer(String videoUrl) {
        YoutubeHandler youtubeHandler = new YoutubeHandler(videoUrl);
        if (youtubeHandler.isYoutubeVideo()) {
            openUriInAvailableApp(youtubeHandler.getURI());
        } else {
            tryOpenVideoUrl(Uri.parse(videoUrl));
        }
    }

    private void tryOpenVideoUrl(Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, VIDEO_INTENT_TYPE);
            AGApplicationState.getInstance().getActivity().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            openUriInAvailableApp(uri);
        }
    }

    private void openUriInAvailableApp(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        AGApplicationState.getInstance().getActivity().startActivity(intent);
    }

    public void showOverlay(String overlayId) {
        OverlayDataDesc overlayDataDesc = AGApplicationState.getInstance().getOverlayDataDesc(overlayId);
        OverlayManager.getInstance().showOverlay(overlayDataDesc, AGApplicationState.getInstance().getSystemDisplay());
    }

    public void hideOverlay() {
        OverlayManager.getInstance().hideCurrentOverlay(AGApplicationState.getInstance().getSystemDisplay());
    }

    public void increaseTextMultiplier(float delta) {
        AGApplicationState.getInstance().increaseFontSizeMultiplier(delta);
        AGApplicationState.getInstance().getSystemDisplay().recalculateAndLayoutScreen();
    }

    public String localizeText(String key) {
        return LanguageManager.getInstance().getString(key);
    }

    public void decreaseTextMultiplier(float delta) {
        AGApplicationState.getInstance().decreaseFontSizeMultiplier(delta);
        AGApplicationState.getInstance().getSystemDisplay().recalculateAndLayoutScreen();
    }

    public String getLocalization() {
        return LanguageManager.getInstance().getLastLanguage();
    }

    public void scanQRCode() {
        Activity activity = AGApplicationState.getInstance().getActivity();
        if (activity != null) {
            if (PermissionManager.hasGrantedPermission(activity.getApplicationContext(), Manifest.permission.CAMERA) == false) {
                requestCameraPermission((IPermissionListener) activity);
            } else {
                startScanQRCode(activity);
            }
        }
    }

    private Intent getScanQRCodeIntent() {
        Intent intent;
        intent = new Intent(AGApplicationState.getInstance().getActivity(), ScanCodeActivity.class);
        intent.putExtra(ScanCodeActivity.REQUEST_CODE, ScanCodeActivity.REQUEST_SCAN_CODE_FUNCTION);
        return intent;
    }

    private void startScanQRCode(Activity activity) {
        activity.startActivityForResult(getScanQRCodeIntent(), ScanCodeActivity.REQUEST_SCAN_CODE_FUNCTION);
    }

    private void requestCameraPermission(IPermissionListener listener) {
        listener.onPermissionLack(IPermissionListener.CAMERA_REQUEST_CODE, new IPermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                Activity activity = AGApplicationState.getInstance().getActivity();
                startScanQRCode(activity);
            }

            @Override
            public void onPermissionDenied() {
            }
        });
    }

    public void setLocalization(String languageName) {
        LanguageManager.getInstance().changeLanguage(AGApplicationState.getInstance().getContext(), languageName);
        BitmapCache.getInstance().clearAssetBitmaps();
    }

    /**
     * opens native alert window
     *
     * @param mTitle             alerts title
     * @param mOkButtonLabel     affirmative button label
     * @param mMessage           alert message
     * @param okAction           code to run when ok button is clicked
     * @param mCancelButtonLabel dismiss button
     * @param cancelAction       code to run when dismiss button is clicked
     */
    public void showAlert(String mTitle, String mOkButtonLabel, String mMessage, Runnable okAction, String mCancelButtonLabel, Runnable cancelAction) {
        PopupManager.showAlert(mMessage, mTitle, mOkButtonLabel, okAction, mCancelButtonLabel, cancelAction);
    }

    public String setLocalValue(String key, String value) {
        VariableStorage.getInstance().addValue(key, value);
        return value;
    }

    /**
     * returns application variable
     *
     * @param key variable name
     * @return returns value found, or "" if variable doesn't exists
     */
    @NonNull
    public String getLocalValue(String key) {
        String value = VariableStorage.getInstance().getValue(key);
        if (value == null)
            value = "";
        return value;
    }

    public String getEvaluate(String expression) {
        return ExpresionEvaluator.evaluate(expression);
    }

    /**
     * returns user logged status
     *
     * @return
     */
    @NonNull
    public boolean isLoggedIn() {
        return AGApplicationState.getInstance().isUserLoggedIn();

    }

    @Nullable
    public void openFile(String url) {

        Activity activity = AGApplicationState.getInstance().getActivity();
        if (activity != null) {
            if (!PermissionManager.hasGrantedPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermission((IPermissionListener) activity, url);
            } else
                executeFunction(url);
        }
    }

    private void requestPermission(IPermissionListener listener, String url) {
        listener.onPermissionLack(IPermissionListener.WRITE_EXTERNAL_STORAGE_REQUEST_CODE, new IPermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                openFile(url);
            }

            @Override
            public void onPermissionDenied() {

            }
        });
    }

    private void executeFunction(String url) {
        if (url.startsWith("assets://")) {
            String fileName = getFileNameFromKinetiseURI(url);
            AppPackage appPackage = AppPackageManager.getInstance().getPackage();
            if (!appPackage.fileExistsInExternalStorage(fileName))
                appPackage.copyFileFromAssetsToExternalStorage(fileName);
            createAndExecuteViewIntent(appPackage.getFileFromExternalStorage(fileName));
        } else {
            createAndExecuteDownloadIntent(url);
        }
    }

    private String getFileNameFromKinetiseURI(String kinetiseURI) {
        return kinetiseURI.replace("assets://", "");
    }

    private void createAndExecuteViewIntent(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), getMimeType(file));
        AGApplicationState.getInstance().getActivity().startActivity(intent);
    }

    private String getMimeType(File file) {
        return URLConnection.guessContentTypeFromName(file.getName());
    }

    private void createAndExecuteDownloadIntent(String url) {
        DownloadManager.Request request = null;
        try {
            request = new DownloadManager.Request(Uri.parse(url));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            PopupManager.showErrorPopup(LanguageManager.getInstance().getString(LanguageManager.ERROR_INVALID_URL));
        }

        if (request != null) {
            PopupManager.showToast(LanguageManager.getInstance().getString(LanguageManager.DOWNLOADING_FILE));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(true);
            Context context = AGApplicationState.getInstance().getActivity().getApplicationContext();

            DownloadFileRunnable downloadFileRunnable = new DownloadFileRunnable(url, request, context);
            ThreadPool.getInstance().executeBackground(downloadFileRunnable);
        }
    }

    public String getCurrentTime() {
        TimeZone timeZoneUTC = TimeZone.getTimeZone(TIMEZONE);
        Calendar calendar = new GregorianCalendar(timeZoneUTC, Locale.US);
        Date now = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(RFC3339_FORMAT, Locale.US);
        dateFormat.setTimeZone(timeZoneUTC);
        return dateFormat.format(now);
    }

    public void startGPSTracking(String mUrl, HashMap<String, String> mHttpParams, HashMap<String, String> mHeaderParams, long mMinTime, int mMinDistance) {
        mHeaderParams.put(AGOkHttpConfigurator.CONTENT_TYPE_HEADER_NAME, AGOkHttpConfigurator.CONTENT_TYPE_JSON);
        Activity activity = AGApplicationState.getInstance().getActivity();
        if (activity != null) {
            String url = AssetsManager.addHttpQueryParams(mUrl, mHttpParams);
            Intent intent = new Intent(AGApplicationState.getInstance().getActivity(), LocationService.class);
            intent.putExtra(LocationService.URL, url);
            intent.putExtra(LocationService.HEADER_PARAMS, mHeaderParams);
            intent.putExtra(LocationService.MIN_TIME, mMinTime);
            intent.putExtra(LocationService.MIN_DISTANCE, mMinDistance);
            intent.setAction(LocationService.ACTION_START_LOCATION_TRACKING);
            if (PermissionManager.hasGrantedPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == false) {
                requestLocationPermission((IPermissionListener) activity, intent);
                return;
            }
            if (activity != null)
                activity.startService(intent);
        }
        return;
    }


    private void requestLocationPermission(IPermissionListener listener, Intent intent) {
        listener.onPermissionLack(IPermissionListener.ACCESS_FINE_LOCATION_REQUEST_CODE, new IPermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                Activity activity = AGApplicationState.getInstance().getActivity();
                if (activity != null)
                    activity.startService(intent);
            }

            @Override
            public void onPermissionDenied() {

            }
        });
    }

    /**
     * stops application from sending gps location changes
     */
    public void endGPSTracking() {
        Activity activity = AGApplicationState.getInstance().getActivity();
        if (activity != null) {
            Intent intent = new Intent(AGApplicationState.getInstance().getActivity(), LocationService.class);
            intent.setAction(LocationService.ACTION_STOP_LOCATION_TRACKING);
            activity.startService(intent);
        }
    }

    public String getSalesforceAccessToken() {
        return SalesForceHelper.getToken();
    }

    public int getPageSize(Object desc) {
        if (!(desc instanceof IFeedClient)) {
            return -1;
        }
        return ((IFeedClient) desc).getNumberItemsPerPage();
    }

    public void playSound(final String soundSource, final float volume, final boolean loop) {
        AudioManager manager = (AudioManager) KinetiseApplication.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        try {
            AssetFileDescriptor assetFileDescriptor = AppPackageManager.getInstance().getPackage().getAssetFileDescriptor(soundSource);
            KinetiseMediaPlayer kinetiseMediaplayer = KinetiseMediaPlayer.getInstance();
            MediaPlayer mediaPlayer = kinetiseMediaplayer.getMediaPlayer();
            if (mediaPlayer == null) return;
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            assetFileDescriptor.close();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    manager.requestAudioFocus(kinetiseMediaplayer, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                }
            });
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.setLooping(loop);
            mediaPlayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopAllSounds() {
        KinetiseMediaPlayer.getInstance().release();
    }

    /**
     * calculates distance between two points in given units
     *
     * @param fromLat latitude of first point
     * @param fromLng longtitude of first point
     * @param toLat   latitude of second point
     * @param toLng   longtitude of second point
     * @param unit    unit - 'KM' - kilometers, 'MI' - miles, 'NMI' - nautic miles
     * @return returns calculated distance
     */
    public double calculateGeoDistance(double fromLat, double fromLng, double toLat, double toLng, String unit) {
        Double FAILED_CALCULATION_RESPONSE = -1d;
        Double NMI_FACTOR = 1851.852d;
        Double MI_FACTOR = 1609.344d;
        Double M_Factor = 1000d;
        try {
            LatLng p1 = new LatLng(fromLat, fromLng);
            LatLng p2 = new LatLng(toLat, toLng);

            double distanceInMeters = SphericalUtil.computeDistanceBetween(p1, p2);
            double factor;
            switch (unit) {
                case "KM":
                    factor = M_Factor;
                    break;
                case "MI":
                    factor = MI_FACTOR;
                    break;
                case "NMI":
                    factor = NMI_FACTOR;
                    break;
                default:
                    return FAILED_CALCULATION_RESPONSE;
            }
            return distanceInMeters / factor;

        } catch (Exception e) {
            return FAILED_CALCULATION_RESPONSE;
        }
    }

    public UUID getGuid() {
        return UUID.randomUUID();
    }

    /**
     * @return name of currently opened screen
     */
    public String getScreenName() {
        return AGApplicationState.getInstance().getCurrentScreenDesc().getAnalitycsTag();
    }

    public void update(Object desc) {
        if (!(desc instanceof AbstractAGViewDataDesc))
            throw new InvalidParameterException("Expected View Control");
        ((AbstractAGViewDataDesc) desc).onUpdate();
    }

    /**
     * Opens given url in native web browser application
     *
     * @param url url to open
     */
    public void showInWebBrowser(String url) {
        if (url != null && !url.equals("")) {
            Uri uri = Uri.parse(url);
            if (uri.getHost() != null) {
                WebBrowserApp webBrowserApp = new WebBrowserApp(uri);
                AGApplicationState.getInstance().getSystemDisplay().openExternalApplication(webBrowserApp);
            }
        }
    }

    public String getSessionId() {
        return AlterApiManager.getAlterApiSesionID();
    }

    public String getAlterApiContext() {
        ApplicationState state = AGApplicationState.getInstance().getApplicationState();
        String result;
        if (state != null && state.getAlterApiContext() != null) {
            result = state.getAlterApiContext();
        } else {
            result = "";
        }
        return result;
    }

    public String getDeviceToken() {
        final AlterApiManager mAlterApiManager = AGApplicationState.getInstance().getAlterApiManager();
        String result;
        Activity activity = AGApplicationState.getInstance().getActivity();
        if (mAlterApiManager != null && ApplicationIdGenerator.getUUID(activity) != null) {
            result = ApplicationIdGenerator.getUUID(activity);
        } else {
            result = "";
        }
        return result;
    }

    public void hideOverlayAndRefresh() {
        OverlayManager.getInstance().hideCurrentOverlay(AGApplicationState.getInstance().getSystemDisplay());
        FeedManager.executePullToRefreshForScreen(AGApplicationState.getInstance().getCurrentScreenDesc());
        if (OverlayManager.getInstance().isOverlayShown())
            FeedManager.executePullToRefreshForOverlay(OverlayManager.getInstance().getCurrentOverlayViewDataDesc());
    }

    public String getFacebookAccessToken() {
        return FacebookService.getInstance().getFacebookToken();
    }

    public String getGoogleUserAccessToken() {
        return GoogleService.getInstance().getGoogleToken();
    }

    /**
     * accesses appliactions translated strings defined in the strings.json file in appliactions assets
     *
     * @param key key to look for
     * @return returns a string that correspondes to given key
     */
    public String translate(String key) {
        String result = LanguageManager.getInstance().getString(key);
        if (result == null) {
            result = "";
        }
        return result;
    }

    public String getTwitterToken() {
        return TwitterService.getInstance().getAccesToken();
    }


    public void delay(String actionString, long delay, AbstractAGElementDataDesc contextDataDesc) {
        try {
            actionString = AGXmlActionParser.unescape(actionString);
            final MultiActionDataDesc actions = AGXmlActionParser.createMultiAction(actionString, contextDataDesc);
            ExecuteActionManager.executeMultiActionDelayed(actions, delay);
        } catch (Exception e) {
            ExceptionManager.getInstance().handleException(e);
        }
    }


    public Object getItem(ActionDataDesc actionDesc) {
        AbstractAGViewDataDesc itemDesc = (AbstractAGViewDataDesc) actionDesc.getContextDataDesc();
        while (itemDesc != null && !(itemDesc.getParentContainer() instanceof AbstractAGDataFeedDataDesc)) {
            itemDesc = itemDesc.getParentContainer();
        }
        return itemDesc;
    }

    /**
     * returns basicAuthToken created after calling basicAuthLogin
     *
     * @return basicAuthToken in form of Basic + token value
     */
    public String getBasicAuthBase64() {
        BasicAuthLoginManager basicAuthLoginManager = BasicAuthLoginManager.getInstance();
        String tokenValue = basicAuthLoginManager.getAuthenticationToken();
        if (tokenValue == null)
            tokenValue = basicAuthLoginManager.getEmptyToken();
        return "Basic " + tokenValue;
    }

    /**
     * removes basicAuthToken and switches screen to screen with id from postLogoutScreenId parameter
     *
     * @param postLogoutScreenId id of the screen to move to after loging out. When null, it will move to applications login screen.
     */
    public void basicAuthLogout(String postLogoutScreenId) {
        AGApplicationState.getInstance().logoutUser(postLogoutScreenId);
    }

    /**
     * Makes a basicAuth login call
     *
     * @param url        url to call when logging in
     * @param action     callback to call when login is successful
     * @param httpParams additional parameters to add to the call query
     */
    public void basicAuthLogin(String url, String usernameString, String passwordString, IonSuccessAction action, HttpParamsDataDesc httpParams) {
        SystemDisplay display = AGApplicationState.getInstance().getSystemDisplay();
        display.blockScreenWithLoadingDialog(true);

        String urlWithParameters = AssetsManager.addHttpQueryParams(url, httpParams);

        BasicAuthLoginManager.getInstance().login(urlWithParameters, usernameString, passwordString, new IRequestCallback() {
            @Override
            public void onError(PopupMessage... messages) {
            }

            @Override
            public void onSuccess(PopupMessage... messages) {
                if (action != null) {
                    action.onSuccess(messages);
                }
            }
        });
    }


    public static class KinetiseMediaPlayer implements AudioManager.OnAudioFocusChangeListener {
        private static KinetiseMediaPlayer mKinetiseApplication;
        private MediaPlayer mediaPlayer = new MediaPlayer();

        public static KinetiseMediaPlayer getInstance() {
            if (mKinetiseApplication == null) {
                mKinetiseApplication = new KinetiseMediaPlayer();
            }

            return mKinetiseApplication;
        }

        public KinetiseMediaPlayer() {
            prepare();
        }

        public MediaPlayer getMediaPlayer() {
            prepare();
            return mediaPlayer;
        }


        private void prepare() {
            if (mediaPlayer != null)
                release();
            mediaPlayer = new MediaPlayer();
        }

        public void release() {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                release();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                release();
            }
        }
    }

    public interface IonSuccessAction {
        void onSuccess(PopupMessage... messages);
    }
}
