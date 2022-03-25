package com.kinetise.helpers.time;

import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.helpers.threading.AGAsyncTask;
import com.kinetise.helpers.threading.ThreadPool;

public class ServerTimeManager {
    private static long sGlobalServerTime = -1;
    private static boolean sIsDateFromServerInitialized = false;

    public static void initializeServerDate() {
        AGAsyncTask retrieveDateTask = new RetrieveDateRunnable(LanguageManager.getInstance().getString("TIMESERVER"));
        ThreadPool.getInstance().executeBackground(retrieveDateTask);
    }

    public static void setGlobalServerTime(long timeMilis) {
        sGlobalServerTime = timeMilis;
    }

    public static long getGlobalServerTime() {
        return sGlobalServerTime;
    }

    public static boolean isDateFromServerInitialized(){
        return sIsDateFromServerInitialized;
    }

    public static void setDateFromServerInitialized(){
        sIsDateFromServerInitialized = true;
    }
}
