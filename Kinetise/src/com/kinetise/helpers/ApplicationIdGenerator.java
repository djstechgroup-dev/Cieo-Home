package com.kinetise.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.kinetise.helpers.preferences.SecurePreferencesHelper;

/**
 * Helper for generating deviceID
 */
public class ApplicationIdGenerator {
	public static final String DEVICE_ID_VALUE = "DeviceIdValue";

	/**
	 * Generates a variant 2, version 4 (randomly generated number) UUID as per RFC 4122
	 * or if exsist return previous generated value
	 * @param context of current activity
	 * @return generated applicationID
	 */
	static public String getUUID(Context context)
	{
		SharedPreferences preferencesFile = SecurePreferencesHelper.getApplicationSettings();
		String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		return preferencesFile.getString(DEVICE_ID_VALUE,androidID);
	}
}
