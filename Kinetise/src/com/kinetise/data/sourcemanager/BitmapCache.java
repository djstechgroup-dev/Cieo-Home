package com.kinetise.data.sourcemanager;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapCache {

    private static BitmapCache mInstance;

    private LruCache<String, Bitmap> mLruCache;

    private BitmapCache() {
        initLruCache();
    }

    public static BitmapCache getInstance() {
        if (mInstance == null) {
            mInstance = new BitmapCache();
        }
        return mInstance;
    }

    private void initLruCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 3;

        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void clear() {
        mLruCache.evictAll();
    }

    public void removeFromLruCache(String uri, int width, int height) {
        mLruCache.remove(createKey(uri, width, height));
    }

    public void addBitmap(String uri, int width, int height, Bitmap bitmap) {
        mLruCache.put(createKey(uri, width, height), bitmap);
    }

    public Bitmap getBitmap(String uri, int width, int height) {
        return mLruCache.get(createKey(uri, width, height));
    }

    private String createKey(String uri, int width, int height) {
        return String.format("%s%s%d%s%d", uri, "x", width, "x", height);
    }

    public void clearAssetBitmaps() {
        //TODO może trzeba trzymać key set bo nie da się iterować po lru cache
    }
}
