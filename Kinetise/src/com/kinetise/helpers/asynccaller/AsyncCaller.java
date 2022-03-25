package com.kinetise.helpers.asynccaller;

import android.app.Activity;

import com.kinetise.data.application.AGApplicationState;


/**
 * Class used to post actions on uiThread
 */
public class AsyncCaller {

	public static void runOnUiThread(Runnable runnable) {
		Activity activity = AGApplicationState.getInstance().getActivity();
        if(activity != null) {
			activity.runOnUiThread(runnable);
        }
	}
}
