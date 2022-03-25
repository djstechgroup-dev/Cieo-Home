package com.kinetise.data.application;

import android.app.Activity;
import android.content.Context;

import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.application.screenhistory.ApplicationState;
import com.kinetise.data.application.screenhistory.ScreenHistoryManager;
import com.kinetise.data.application.screenloader.ScreenLoader;
import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.ApplicationDescriptionDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGErrorDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGLoadingDataDesc;
import com.kinetise.data.descriptors.datadescriptors.OverlayDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;

public interface IAGApplication {

    ScreenLoader getScreenLoader();

    SystemDisplay getSystemDisplay();

    AlterApiManager getAlterApiManager();

    ScreenHistoryManager getHistoryManager();

    ApplicationState getApplicationState();

    void setApplicationState(ApplicationState appState);

    AGScreenDataDesc getCurrentScreenDesc();

    ApplicationDescriptionDataDesc getApplicationDescription();

    AGErrorDataDesc getErrorDataDesc(int width, int height);

    AGLoadingDataDesc getLoadingDataDesc(int width, int height);

    AGScreenDataDesc getScreenDesc(String screenId);

    OverlayDataDesc getOverlayDataDesc(String overlayId);

    Activity getActivity();

    void setContext(Context context);

    Context getContext();

}
