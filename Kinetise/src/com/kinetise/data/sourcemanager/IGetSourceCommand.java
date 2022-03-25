package com.kinetise.data.sourcemanager;

import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.helpers.http.CacheControlOptions;

/**
 * Base interface for all commands that are used to set data on objects
 */
public interface IGetSourceCommand<T> {

    public enum SourceType {
    	/**
    	 * Source is app assets directory
    	 */
        ASSETS,
        /**
         * Source is Http URI
         */
        HTTP, 
        /**
         * Source is control
         */
        CONTROL, 
        /**
         * Used for sources like SharedPreferences
         */
        STORAGE, 
        /**
         * Source is XML from the internet
         */
        FEEDXML
    }

    /**
     * Called after getting source
     * @param obj result of getAsset command, type is dependent on implementation
     */
    void postGetSource(T obj);

    /**
     * Notifies that error occured
     */
    void onError();

    void onError(int status, PopupMessage... messages);

    CacheControlOptions getCacheOption();

    void setCacheOption(CacheControlOptions cache);

    /**
     * Notifies that Source command were interrupted
     */
    void cancel();

    Object[] getParams();

    boolean isDisplayResult();
}
