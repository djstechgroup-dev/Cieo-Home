package com.kinetise.support.logger;

import android.util.Log;

import java.util.HashMap;

/**
 * Internal class for easier logging app events
 */
public class Logger {
    private static boolean debuggable = false;

    public static void setDebuggable(boolean debuggable) {
        Logger.debuggable = debuggable;
    }

    private static HashMap<String, Long> sProfileStartTSMap = new HashMap<String, Long>();
    /**
     * Logs debug
     * @param tag tag of log
     * @param msg logged message
     */
    private static void d(String tag, String msg){
        if(debuggable)
            Log.d(tag, msg);
    }

    public static void d(Object callingClass, String callingMethod, String message){
        String tag = (callingClass instanceof String) ? (String) callingClass : callingClass.getClass().toString();
        String msg = callingMethod + "   " + message;
        d(tag,msg);
    }

    public static void d(Object callingClass, String callingMethod){
        d(callingClass,callingMethod,null);
    }

    /**
     * Logs error
     * @param tag tag of log
     * @param msg logged message
     */
    private static void e(String tag, String msg){
        if(debuggable)
            Log.e(tag, msg);
    }

    public static void e(Object callingClass, String callingMethod, String message){
        String tag = (callingClass instanceof String) ? (String) callingClass : callingClass.getClass().toString();
        String msg = callingMethod + "   " + message;
        e(tag,msg);
    }

    public static void e(Object callingClass, String callingMethod){
        d(callingClass,callingMethod,null);
    }

    /**
     * Logs as info
     * @param tag tag of log
     * @param msg logged message
     */
    private static void i(String tag, String msg){
        if(debuggable)
            Log.i(tag, msg);
    }

    public static void i(Object callingClass, String callingMethod, String message){
        String tag = (callingClass instanceof String) ? (String) callingClass : callingClass.getClass().toString();
        String msg;
        if(message != null)
            msg = callingMethod + "   " + message;
        else
            msg = callingMethod;
        i(tag,msg);
    }

    public static void i(Object callingClass, String callingMethod){
        i(callingClass,callingMethod,null);
    }

    /**
     * Logs as verbose
     * @param tag tag of log
     * @param msg logged message
     */
    private static void v(String tag, String msg){
        if(debuggable)
            Log.v(tag, msg);
    }

    public static void v(Object callingClass, String callingMethod, String message){
        String tag = (callingClass instanceof String) ? (String) callingClass : callingClass.getClass().toString() + " " + callingClass.hashCode();
        String msg;
        if(message != null)
            msg = callingMethod + "   " + message;
        else
            msg = callingMethod;
        v(tag,msg);
    }

    public static void v(Object callingClass, String callingMethod){
        v(callingClass,callingMethod,null);
    }

    /**
     * Logs warning
     * @param tag tag of log
     * @param msg logged message
     */
    private static void w(String tag, String msg){
        if(debuggable)
            Log.w(tag, msg);
    }

    public static void w(Object callingClass, String callingMethod, String message){
        String tag = (callingClass instanceof String) ? (String) callingClass : callingClass.getClass().toString();
        String msg;
        if(message != null)
            msg = callingMethod + "   " + message;
        else
            msg = callingMethod;
        w(tag,msg);
    }

    public static void w(Object callingClass, String callingMethod){
        w(callingClass,callingMethod,null);
    }

    public static void profileStart(String profileTarget) {
        if (debuggable) {
            sProfileStartTSMap.put(profileTarget, new Long(System.currentTimeMillis()));
        }
    }

    public static void profileEnd(String logTag, String profileTarget) {
        if (debuggable) {
            if (sProfileStartTSMap.containsKey(profileTarget)) {
                long startTs = sProfileStartTSMap.get(profileTarget);
                Logger.d(logTag, "PROFILING (" + profileTarget + "): " + (System.currentTimeMillis() - startTs) + " ms");
                sProfileStartTSMap.remove(profileTarget);
            } else {
                Logger.d(logTag, "PROFILING (" + profileTarget + "): profileStart() NEVER CALLED");
            }
        }
    }

    public static void profileEnd(String profileTarget) {
        profileEnd(profileTarget,profileTarget);
    }

}
