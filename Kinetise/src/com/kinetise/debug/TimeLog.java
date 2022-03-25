package com.kinetise.debug;

import com.kinetise.support.logger.Logger;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Logging helper to log execution time of methods
 */
public class TimeLog {

    private static HashMap<Long, HashMap<String, ArrayList<TimeItem>>> sStartTimeMap = new HashMap<Long, HashMap<String, ArrayList<TimeItem>>>();

    /**
     * Starts time logging
     * @param methodTag
     */
    public synchronized static void startLoggingTime(String methodTag) {
        startLoggingTime(true, methodTag);
    }

    /**
     * Starts time logging
     * @param methodTag
     * @param active if false than log will not print output to console
     */
    public synchronized static void startLoggingTime(boolean active, String methodTag) {
        long currentThreadId = Thread.currentThread().getId();

        if (!sStartTimeMap.containsKey(currentThreadId)) {
            sStartTimeMap.put(currentThreadId, new HashMap<String, ArrayList<TimeItem>>());
        }

        HashMap<String, ArrayList<TimeItem>> threadMap = sStartTimeMap.get(currentThreadId);

        if (!threadMap.containsKey(methodTag)) {
            threadMap.put(methodTag, new ArrayList<TimeItem>());
        }

        ArrayList<TimeItem> startTimesList = threadMap.get(methodTag);

        Long startTime = System.currentTimeMillis();
        startTimesList.add(new TimeItem(startTime, active));
    }

    /**
     * Stops logging time, prints output of all logs that were active for current thread and methodTag
     * you can set active by {@link #startLoggingTime(boolean, String)}
     * @param methodTag
     */
    public synchronized static void stopLoggingTime(String methodTag) {
        long currentThreadId = Thread.currentThread().getId();

        ArrayList<TimeItem> startTimesList = null;
        HashMap<String, ArrayList<TimeItem>> threadMap = null;
        if (sStartTimeMap.containsKey(currentThreadId)) {
            threadMap = sStartTimeMap.get(currentThreadId);
            if (threadMap.containsKey(methodTag)) {
                startTimesList = threadMap.get(methodTag);
            }
        }

        if (startTimesList == null || startTimesList.size() == 0) {
            throw new InvalidParameterException("Cannot stop logging time when start did not happen - " + methodTag);
        }

        int startTimesCount = startTimesList.size();
        int lastIndex = startTimesCount - 1;
        TimeItem timeItem = startTimesList.get(lastIndex);
        Long stopTime = System.currentTimeMillis();

        if (timeItem.getActivity()) {
            Logger.v("TimeLog", methodTag , "[Thread: " + currentThreadId + "][OUT] " + methodTag + " " + (startTimesCount != 1 ? ("(call " + lastIndex +
                    ". time)") : "" + " - time: [" + (stopTime - timeItem.getTimestamp()) + "ms]"));
        }

        if (startTimesCount == 1) {
            threadMap.remove(methodTag);
            if (sStartTimeMap.get(currentThreadId).size() == 0) {
            	threadMap.values().removeAll(Collections.singleton(sStartTimeMap.get(currentThreadId)));
            }
        } else {
            startTimesList.remove(lastIndex);
        }
    }

    /**
     * Helper for storing timestamp of event, and information if log is active for it
     */
    public static class TimeItem {

        private Long mTimestamp;
        private boolean mActive;

        public TimeItem(Long timestamp, boolean active) {
            mTimestamp = timestamp;
            mActive = active;
        }

        public Long getTimestamp() {
            return mTimestamp;
        }

        public boolean getActivity() {
            return mActive;
        }
    }
}
