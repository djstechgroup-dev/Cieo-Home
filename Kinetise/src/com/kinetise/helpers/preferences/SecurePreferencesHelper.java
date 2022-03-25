package com.kinetise.helpers.preferences;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import in.co.ophio.secure.core.*;

import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.support.logger.Logger;

import java.util.HashMap;
import java.util.Map;

public class SecurePreferencesHelper {
    public static final String OLD_APP_FILENAME = "APPLICATION";
    public static final String APPLICATION_SETTINGS = "application_settings.xml";
    public static final String USER_DATA = "user_data.xml";
    public static final String DATA_FEEDS = "data_feeds.xml";
    public static final String DATA_FEED_DATABASE = "data_feed_database.xml";
    public static final String DATA_FEED_DATABASE_LOG = "data_feed_database_log.xml";
    public static final String PROPERTY_STORAGE = "properties.xml";
    public static final String VARIABLE_STORAGE = "variables.xml";
    private static String key;

    private static Map<String, String> oldPreferenceFilesMapping = new HashMap<String, String>() {
        {
            put("ApplicationState", APPLICATION_SETTINGS);
            put("analyticsPreferences", APPLICATION_SETTINGS);
            put("DeviceId", APPLICATION_SETTINGS);
            put("VERIFY", APPLICATION_SETTINGS);
            put("APPLICATION", APPLICATION_SETTINGS);
            put("alterApiPreferences",USER_DATA);
            put("basicAuthLoginManagerFile",USER_DATA);
            put("facebookPreferences",USER_DATA);
            put("GCM",USER_DATA);
            put("RedirectMap",USER_DATA);
            put("synchronizerPreferences",USER_DATA);
            put("twitterPreferences",USER_DATA);
            put("dataFeeds", DATA_FEEDS);
            put("propertyStorage", PROPERTY_STORAGE);
            put("LocalValueStorage", VARIABLE_STORAGE);
        }
    };

    public static void init(Application application) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                key = KeyStoreKeyGenerator.get(application,
                        application.getPackageName())
                        .loadOrGenerateKeys();
            }

        } catch(Exception exception){
            key = null;
        }
    }

    public static void migrateOldPreferences(Context context) {
        if(!hasOldPreferences(context))
            return;

        for(Map.Entry<String,String> entry:oldPreferenceFilesMapping.entrySet()){
            SharedPreferences.Editor targetPreferenceEditor = getPrefForFilename(entry.getValue()).edit();
            if(targetPreferenceEditor == null){
                continue;
            }
            Logger.d(SecurePreferencesHelper.class,"migrate","migrating data from "+entry.getKey()+" to "+entry.getValue());
            SharedPreferences sourcePreferences = context.getSharedPreferences(entry.getKey(), 0);
            SharedPreferences.Editor sourcePreferencesEditor = sourcePreferences.edit();
            Map<String, ?> values = sourcePreferences.getAll();

            for(String key:values.keySet()){
                putValue(targetPreferenceEditor, key, values.get(key));
                sourcePreferencesEditor.remove(key);
            }

            sourcePreferencesEditor.commit();
            targetPreferenceEditor.commit();
        }
    }

    private static boolean hasOldPreferences(Context context){
        SharedPreferences preferencesFile = context.getSharedPreferences(OLD_APP_FILENAME, 0);
        return preferencesFile.contains(KinetiseApplication.APP_VERSION);

    }

    private static SharedPreferences getPrefForFilename(String value) {
        switch(value){
            case APPLICATION_SETTINGS:
                return getApplicationSettings();
            case USER_DATA:
                return getUserData();
            case DATA_FEEDS:
                return getDataFeeds();
            case PROPERTY_STORAGE:
                return getProperties();
            case VARIABLE_STORAGE:
                return getVariables();
        }
        return null;
    }

    private static void putValue(SharedPreferences.Editor targetPreferenceEditor, String key, Object value) {
        if(value instanceof String){
            targetPreferenceEditor.putString(key, (String)value);
        } else if(value instanceof Boolean){
            targetPreferenceEditor.putBoolean(key, (boolean)value);
        } else if(value instanceof Float){
            targetPreferenceEditor.putFloat(key, (float)value);
        } else if(value instanceof Integer){
            targetPreferenceEditor.putInt(key, (int)value);
        } else if(value instanceof Long) {
            targetPreferenceEditor.putLong(key, (long) value);
        }
    }

    public static SharedPreferences getApplicationSettings() {
        return getSecurePreference(APPLICATION_SETTINGS);
    }

    public static SharedPreferences getUserData() {
        return getSecurePreference(USER_DATA);
    }

    public static SharedPreferences getDataFeeds() {
        return getPreference(DATA_FEEDS);
    }

    public static SharedPreferences getDataFeedDatabase() {
        return getPreference(DATA_FEED_DATABASE);
    }

    public static SharedPreferences getDataFeedDatabaseLog() {
        return getPreference(DATA_FEED_DATABASE_LOG);
    }

    public static SharedPreferences getProperties() {
        return getSecurePreference(PROPERTY_STORAGE);
    }

    public static SharedPreferences getVariables() {
        return getSecurePreference(VARIABLE_STORAGE);
    }

    private static SharedPreferences getSecurePreference(String fileName) {
        if(key==null){
            return getPreference(fileName);
        } else {
            return new ObscuredPreferencesBuilder()
                    .setApplication(KinetiseApplication.getInstance())
                    .obfuscateValue(true)
                    .obfuscateKey(true)
                    .setSharePrefFileName(fileName)
                    .setSecret(key)
                    .createSharedPrefs();
        }
    }

    private static SharedPreferences getPreference(String fileName) {
        Context context = AGApplicationState.getInstance().getContext();
        return context.getSharedPreferences(fileName, context.MODE_PRIVATE);
    }
}
