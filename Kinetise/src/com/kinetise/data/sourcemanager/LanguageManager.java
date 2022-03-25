package com.kinetise.data.sourcemanager;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.packagemanager.AppPackage;
import com.kinetise.data.packagemanager.AppPackageManager;
import com.kinetise.helpers.RWrapper;
import com.kinetise.helpers.time.DateNamesHolder;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LanguageManager {

    public static final String TEXT_NOT_FOUND_IN_DICTIONARY = "Text not found in dictionary";
    public static final String INVALID_DATE_FORMAT = "INVALID_DATE_FORMAT";
    public static final String ERROR_CONNECTION = "ERROR_CONNECTION";
    public static final String ERROR_NO_CONNECTION = "ERROR_NO_CONNECTION";
    public static final String ERROR_DATA_FROM_CACHE = "ERROR_DATA_FROM_CACHE";
    public static final String ERROR_CONNECTION_TIMEOUT = "ERROR_CONNECTION_TIMEOUT";
    public static final String ERROR_INVALID_SESSION = "ERROR_INVALID_SESSION";
    public static final String ERROR_HTTP = "ERROR_HTTP";
    public static final String ERROR_LOGIN = "ERROR_LOGIN";
    public static final String ERROR_SEND_FORM = "ERROR_SEND_FORM";
    public static final String ERROR_SEND_EMAIL = "ERROR_SEND_EMAIL";
    public static final String ERROR_FORM_INVALID = "ERROR_INVALID_FORM";
    public static final String ERROR_DATA = "ERROR_DATA";
    public static final String ERROR_SCREEN_HIERARCHY = "SCREEN_HIERARCHY_ERROR";
    public static final String ERROR_CODE_SCANNER = "ERROR_CODE_SCANNER";
    public static final String ERROR_GET_PHONE_CONTACT = "ERROR_CODE_SCANNER";
    public static final String ERROR_QRCODE_INVALID_URL = "ERROR_QRCODE_INVALID_URL";
    public static final String ERROR_INVALID_URL = "ERROR_INVALID_URL";
    public static final String ERROR_INVALID_FORM = "FORM_INVALID";
    public static final String CLOSE_POPUP = "CLOSE_POPUP";
    public static final String POPUP_INFO_HEADER = "POPUP_INFO_HEADER";
    public static final String POPUP_ERROR_HEADER = "POPUP_ERROR_HEADER";
    public static final String PULL_TO_REFRESH_TEXT_KEY = "PULL_TO_REFRESH";
    public static final String RELEASE_TEXT_KEY = "RELEASE_TO_REFRESH";
    public static final String FACEBOOK_INIT = "FACEBOOK_INIT";
    public static final String FACEBOOK_POST_SUCCESS = "FACEBOOK_POST_SUCCEED";
    public static final String FACEBOOK_POST_FAILED = "FACEBOOK_POST_FAILED";
    public static final String NO_YOUTUBE_APP = "NO_YOUTUBE_APP";
    public static final String APPAUTH_RETRY_BUTTON = "APPAUTH_RETRY_BUTTON";
    public static final String APPAUTH_CLOSE_BUTTON = "APPAUTH_CLOSE_BUTTON";
    public static final String APPAUTH_ERROR = "APPAUTH_ERROR";
    public static final String NODE_NOT_FOUND = "NODE_NOT_FOUND";
    public static final String MAP_KEY_NOT_FOUND = "MAP_KEY_NOT_FOUND";
    public static final String SEND_MEMORY_ERROR = "SEND_MEMORY_ERROR";
    public static final String DOWNLOADING_FILE = "DOWNLOADING_FILE";
    public static final String OPEN_PHOTO_TITLE = "OPEN_PHOTO_TITLE";
    public static final String OPEN_PHOTO_CAMERA = "OPEN_PHOTO_CAMERA";
    public static final String OPEN_PHOTO_LIBRARY = "OPEN_PHOTO_LIBRARY";
    public static final String OFFLINE_READING_SUCCEED_TITLE = "OFFLINE_READING_SUCCEED_TITLE";
    public static final String OFFLINE_READING_SUCCEED_DESCRIPTION = "OFFLINE_READING_SUCCEED_DESCRIPTION";
    public static final String OFFLINE_READING_FAILED_TITLE = "OFFLINE_READING_FAILED_TITLE";
    public static final String OFFLINE_READING_FAILED_DESCRIPTION = "OFFLINE_READING_FAILED_DESCRIPTION";
    public static final String START_GPS_TRACKING = "START_GPS_TRACKING";
    public static final String SHARE_DIALOG_TITLE = "SHARE_DIALOG_TITLE";
    public static final String END_GPS_TRACKING = "END_GPS_TRACKING";

    private static final String SHARE_PREFERENCES_LANGUAGE = "languagePreferences";
    private static final String SHARED_PREFERENCE_LAST_LANGUAGE = "lastLanguage";
    private static final String SHARED_PREFERENCE_LAST_SYSTEM_LANGUAGE = "lastSysLanguage";
    private static final String LANGUAGE_DIRECTORY = "languages";
    private static final String LANGUAGE_FILENAME = "strings.json";
    public static final String ERROR_COULD_NOT_RESOLVE_DOMAIN_NAME = "ERROR_COULD_NOT_RESOLVE_DOMAIN_NAME";

    private static LanguageManager mInstance;

    protected Map<String, String> mStrings;
    protected String mLastLanguage;
    protected List<String> mLocalizedAssetsNames;

    public static LanguageManager getInstance() {
        if (mInstance == null) {
            synchronized (LanguageManager.class) {
                if (mInstance == null) {
                    Context context = AGApplicationState.getInstance().getContext();
                    mInstance = new LanguageManager(context);
                    if (!mInstance.changeLanguageIfRequired(context)) {
                        mInstance.initLanguage(context, mInstance.mLastLanguage);
                    }
                }
            }
        }
        return mInstance;
    }

    protected LanguageManager() {
    }

    protected LanguageManager(Context context) {
        mLastLanguage = context.getSharedPreferences(SHARE_PREFERENCES_LANGUAGE, Context.MODE_PRIVATE).getString(SHARED_PREFERENCE_LAST_LANGUAGE, null);
    }

    public static void clearInstance() {
        mInstance = null;
    }

    public String getLastLanguage() {
        return mLastLanguage;
    }

    public boolean changeLanguageIfRequired(Context context) {
        String currentSystemLanguage = context.getResources().getConfiguration().locale.getLanguage();
        String lastSystemLanguage = context.getSharedPreferences(SHARE_PREFERENCES_LANGUAGE, Context.MODE_PRIVATE)
                .getString(SHARED_PREFERENCE_LAST_SYSTEM_LANGUAGE, null);

        //if first run or system language changed
        if (lastSystemLanguage == null || !lastSystemLanguage.equals(currentSystemLanguage)) {
            initLanguage(context, currentSystemLanguage);
            context.getSharedPreferences(SHARE_PREFERENCES_LANGUAGE, Context.MODE_PRIVATE)
                    .edit()
                    .putString(SHARED_PREFERENCE_LAST_SYSTEM_LANGUAGE, currentSystemLanguage)
                    .apply();
            return true;
        }
        return false;
    }

    public void initLanguage(Context context, String language) {
        //reinitialize mStrings map with default strings.json
        String defaultJsonFile = String.format("%s%s",
                AppPackage.ASSETS_PREFIX,
                LANGUAGE_FILENAME);
        mStrings = getStringsMap(defaultJsonFile);

        //replace strings with language specified map (language/XX/strings.json)
        mLastLanguage = language;
        String jsonFile = String.format("%s%s%s%s%s%s",
                AppPackage.ASSETS_PREFIX,
                LANGUAGE_DIRECTORY,
                File.separator,
                language,
                File.separator,
                LANGUAGE_FILENAME);
        replaceStrings(getStringsMap(jsonFile));
        context.getSharedPreferences(SHARE_PREFERENCES_LANGUAGE, Context.MODE_PRIVATE)
                .edit()
                .putString(SHARED_PREFERENCE_LAST_LANGUAGE, mLastLanguage)
                .apply();

        readLocalizedAssetsNames(context);

        Locale locale = new Locale(language);

        Locale.setDefault(locale);
        DateNamesHolder.initializeDateNames();
    }

    /**
     * Does initLanguage plus repaints the current screen
     *
     * @param context
     * @param language
     */

    public void changeLanguage(Context context, String language) {
        initLanguage(context, language);
        AGApplicationState.getInstance().getScreenLoader().reloadCurrentScreen();
    }

    private void readLocalizedAssetsNames(Context context) {
        try {
            mLocalizedAssetsNames = Arrays.asList(AppPackageManager.getInstance().getPackage().listAssets(context.getString(RWrapper.string.getDeveloperAssetsPrefix()) + '/' + AppPackage.LOCALIZATIONS_FOLDER_NAME + '/' + getLastLanguage()));
        } catch (IOException e) {
            mLocalizedAssetsNames = new ArrayList<String>();
            e.printStackTrace();
        }
    }

    private Map<String, String> getStringsMap(String asset) {
        Map<String, String> result = new HashMap<String, String>();
        if (AppPackageManager.getInstance().getPackage() != null) {
            InputStream jsonStream = (InputStream) AppPackageManager.getInstance().getPackage().getAsset(asset);
            if (jsonStream != null && jsonStream instanceof InputStream) {
                String defaultJson = AppPackage.streamToString(jsonStream);
                result = new Gson().fromJson(defaultJson, new TypeToken<HashMap<String, String>>() {
                }.getType());
            }
        }
        return result;
    }

    private void replaceStrings(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            replaceString(entry.getKey(), entry.getValue());
        }
    }

    private void replaceString(String key, String value) {
        mStrings.put(key, value);
    }

    public String getString(String key) {
        if (mStrings != null && mStrings.containsKey(key)) {
            return mStrings.get(key);
        } else {
            return TEXT_NOT_FOUND_IN_DICTIONARY;
        }
    }

    public boolean checkIfLocalizedAssetIsAvailable(String assetFilename) {
        String assetName = FilenameUtils.removeExtension(assetFilename);
        for (String localizedAssetsName : mLocalizedAssetsNames) {
            if (localizedAssetsName.startsWith(assetName))
                return true;
        }

        return false;
    }
}
