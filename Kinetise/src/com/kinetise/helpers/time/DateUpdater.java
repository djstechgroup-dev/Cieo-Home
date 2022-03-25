package com.kinetise.helpers.time;


import com.kinetise.data.sourcemanager.LanguageManager;

import java.util.Timer;
import java.util.TimerTask;

public class DateUpdater {

    private static final int SECOND = 1000;
    private static final int HOUR = SECOND * 60 * 60;
    private static final int TIMEOUT = 30000;
    private static DateUpdater mInstance;
    private Timer mTimer;
    private long mTimeDifference = 0;

    public static DateUpdater getInstance() {
        if(mInstance == null){
            synchronized (DateUpdater.class){
                if(mInstance == null) {
                    mInstance = new DateUpdater();
                }
            }
        }
        return mInstance;
    }

    private DateUpdater() {
    }

    public static void clearInstance(){
        mInstance = null;
    }

    public void cancelTimeUpdates(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
        }
    }

    public void initTimeUpdates(){
        cancelTimeUpdates();
        mTimer = new Timer();
        mTimer.schedule(new UpdateTask(), 0, HOUR);
    }

    public long getCurrentTime(){
        return System.currentTimeMillis() - mTimeDifference;
    }

    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            SntpClient sntpClient = new SntpClient();
            if (sntpClient.requestTime(LanguageManager.getInstance().getString("TIMESERVER"), TIMEOUT)) {
                mTimeDifference = System.currentTimeMillis() - sntpClient.getNtpTime();
            } else {
                mTimeDifference = 0;
            }
        }
    }
}
