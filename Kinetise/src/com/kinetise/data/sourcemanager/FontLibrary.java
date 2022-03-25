package com.kinetise.data.sourcemanager;

import android.content.res.Resources;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * @author: Marcin Narowski
 * Date: 29.07.13
 * Time: 13:01
 */
public class FontLibrary {
    private HashMap<String, Typeface> mTypefaceHashMap;
    private static FontLibrary mInstance;

    private FontLibrary() {
        mTypefaceHashMap = new HashMap<String, Typeface>();
    }

    public static synchronized FontLibrary getInstance() {
        if (mInstance == null) {
            synchronized (FontLibrary.class){
                if (mInstance == null) {
                    mInstance = new FontLibrary();
                }
            }
        }

        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    public synchronized void addTypeface(String key, Typeface typeface) {
        mTypefaceHashMap.put(key, typeface);
    }

    public synchronized Typeface getTypeface(String key) throws Resources.NotFoundException {
        if (mTypefaceHashMap.containsKey(key)) {
            return mTypefaceHashMap.get(key);
        } else {
            throw new Resources.NotFoundException("Typeface " + key + " not found");
        }
    }

    public synchronized void clear() {
        mTypefaceHashMap.clear();
    }


}
