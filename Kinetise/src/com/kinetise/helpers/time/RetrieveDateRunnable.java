package com.kinetise.helpers.time;

import com.kinetise.helpers.threading.AGAsyncTask;

/**
 * We use this runnable to get date from server. If date is unavailable
 * we set globalServerTime to -2 to indicate this fact
 */
public class RetrieveDateRunnable extends AGAsyncTask {
    private String mTimeServerHostname;

    public RetrieveDateRunnable(String timeServerHostname) {
        mTimeServerHostname = timeServerHostname;
    }

    @Override
    public void run() {
        if(mIsCanceled)
            return;
        SntpClient client = new SntpClient();

        if (client.requestTime(mTimeServerHostname, 3000)) {
            long globalTime = client.getNtpTime();
            ServerTimeManager.setGlobalServerTime(globalTime);
        } else {
            ServerTimeManager.setGlobalServerTime(-2);
        }
    }

}
