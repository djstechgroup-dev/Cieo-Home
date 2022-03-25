package com.kinetise.data.packagemanager;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;

import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.sourcemanager.BitmapCache;
import com.kinetise.data.sourcemanager.FontLibrary;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.helpers.AndroidBitmapDecoder;
import com.kinetise.helpers.RWrapper;
import com.kinetise.support.logger.Logger;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

public class AppPackage {
    public final static String ASSETS_PREFIX = "assets://";
    public final static String PROJECT_FILE = "project.xml";
    private final static String XML_EXTENSION = "xml";
    private final static String TTF_EXTENSION = "ttf";
    private final static String JSON_EXTENSION = "json";
    private static final String UTF8 = "UTF-8";
    private static final int MAX_ASSET_BITMAP_SIZE = 2048;
    private static final int FILE_COPY_BUFFER_SIZE = 9182;
    private static final int MIN_ASSET_BITMAP_SIZE = 32;
    public static final String LOCALIZATIONS_FOLDER_NAME = "languages";

    private final String mApplicationAssetsDir;

    private Context mContext;
    private Bitmap mLoadingBitmap;
    private Bitmap mErrorBitmap;
    private Bitmap mScannedQRCodeBitmap;

    public AppPackage(Context context) {
        mContext = context;
        mApplicationAssetsDir = getAssetsDir();
        mLoadingBitmap = loadPlaceholder(getLoadingPlaceholderString());
        mErrorBitmap = loadPlaceholder(getErrorPlaceholderPath());
        mScannedQRCodeBitmap = null; // will be lazy-loaded when needed for the first time
    }

    private String getApplicationMainFile() {
        return getStringFromXmlFileAsset(PROJECT_FILE);
    }

    private String getStringFromXmlFileAsset(String xmlFileName) {
        Logger.v(this, "getStringFromXmlFileAsset", "Xml file name is " + xmlFileName);
        return getStringFromFile(xmlFileName);
    }

    public String getStringFromFile(String fileName) {
        Context context = mContext;
        InputStream stream;
        try {
            stream = context.getAssets().open(mApplicationAssetsDir + "/" + fileName);
            return streamToString(stream);

        } catch (IOException e) {
            ExceptionManager.getInstance().handleException(e, false);
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
        return "";
    }

    public void copyFileFromAssetsToExternalStorage(String filename) {
        File file = getFileFromExternalStorage(filename);
        try {
            InputStream is = getFileFromAssetsAsStream(filename);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[FILE_COPY_BUFFER_SIZE];
            int bytesRead = 0;
            while ((bytesRead = is.read(data, 0, data.length)) != (-1)) {
                os.write(data, 0, bytesRead);
            }
            is.close();
            os.close();
        } catch (IOException e) {
        }
    }

    public File getFileFromExternalStorage(String filename) {
        return new File(mContext.getExternalFilesDir("openedFiles"), filename);
    }

    public void deleteFileFromExternalStorage(String filename) {
        File file = getFileFromExternalStorage(filename);
        file.delete();
    }

    public boolean fileExistsInExternalStorage(String filename) {
        File file = getFileFromExternalStorage(filename);
        return file.exists();
    }

    public static String streamToString(InputStream stream) {
        BufferedReader reader = null;
        try {
            return IOUtils.toString(stream, UTF8);
        } catch (IOException e) {
            ExceptionManager.getInstance().handleException(e, false);
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return "";
    }

    public InputStream getFileFromAssetsAsStream(String xmlFileName) {
        InputStream stream = null;
        xmlFileName = mApplicationAssetsDir + '/' + xmlFileName;
        try {
            stream = mContext.getAssets().open(xmlFileName);
        } catch (IOException e) {
            ExceptionManager.getInstance().handleException(e, false);
            e.printStackTrace();
        }
        return stream;
    }

    public synchronized String getErrorPlaceholderPath() {
        return mContext.getString(RWrapper.string.getErrorPlaceholder());
    }

    public synchronized Bitmap getErrorPlaceholder() {
        return mErrorBitmap;
    }

    public synchronized Bitmap getLoadingPlaceholder() {
        return mLoadingBitmap;
    }

    public synchronized String getScannedQRCodePlaceholderPath() {
        return mContext.getString(RWrapper.string.getScannedQRCodePlaceholder());
    }

    public synchronized Bitmap getScannedQRCodePlaceholder() {
        if (mScannedQRCodeBitmap == null) {
            mScannedQRCodeBitmap = loadPlaceholder(getScannedQRCodePlaceholderPath());
        }
        return mScannedQRCodeBitmap;
    }

    public synchronized Bitmap loadPlaceholder(String path) {
        Bitmap result = null;
        InputStream stream = null;
        try {
            stream = mContext.getAssets().open(path);
            result = AndroidBitmapDecoder.decodeBitmapFromStream(stream, 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }

    public synchronized String getLoadingPlaceholderString() {
        return mContext.getString(RWrapper.string.getLoadingPlaceholder());
    }

    private Bitmap getBitmapFromAssets(String name, Object[] params) {
        Bitmap result = null;
        InputStream stream = null;
        try {
            /*Fixme: LRU CACHE zapamiętuje grafiki wg ich nazw, nie na podstawie faktycznej ścieżki do pliku. W efekcie czego może zwracać grafikę o złym
            rozmiarze, a także gdy dodamy możliwość tłumacznenia grafik przez umieszczanie przetłumaczonych wersji w odpowiednich katalogach to LRU cache
            moze zwracać błędną grafikę ( nie przetłumaczoną)*/

            result = BitmapCache.getInstance().getBitmap(name, 0, 0); //TODO rozmiary dla plików z assetów
            if (result == null) {
                stream = getBitmapWithSizeFromAssets(name, params);
                result = AndroidBitmapDecoder.decodeBitmapFromStream(stream, 0, 0);
                if (result != null) {
                    BitmapCache.getInstance().addBitmap(name, 0, 0, result);
                }
            }
        } catch (IOException e) {
            result = getErrorPlaceholder();
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            result = getErrorPlaceholder();
            e.printStackTrace();
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }

    private InputStream getBitmapWithSizeFromAssets(String assetFilename, Object[] params) throws IOException {
        if (params == null || isSystemImage(assetFilename))
            return mContext.getAssets().open(assetFilename);
        InputStream result;
        int bitmapsBiggerSize = Math.max((Integer) params[0], (Integer) params[1]);
        if (bitmapsBiggerSize == 0) {
            Logger.w("AppPackage", "getBitmapWithSizeFromAssets", "Requested bitmap with size 0! Fell back to maximum size");
            bitmapsBiggerSize = 2048;
        }
        int assetsBitmapSize = getNearestPowerOf2(bitmapsBiggerSize, MAX_ASSET_BITMAP_SIZE);
        while (assetsBitmapSize >= MIN_ASSET_BITMAP_SIZE) {
            try {
                result = tryOpenBitmapFile(assetFilename, assetsBitmapSize);
                if (result != null)
                    return result;
            } catch (IOException e) {
                Logger.d(this, e.getMessage());
            }
            assetsBitmapSize = assetsBitmapSize >> 1;
        }
        throw new IOException("File not found");
    }

    public static int getNearestPowerOf2(int size, int maxSize) {
        int n = MIN_ASSET_BITMAP_SIZE;
        if (maxSize < n) {
            return n;
        }
        while (n < size) {
            n = (n << 1);
        }

        if (n > maxSize) {
            n = maxSize;
        }
        return n;
    }

    private InputStream tryOpenBitmapFile(String filename, int size) throws IOException {
        String languageName = LanguageManager.getInstance().getLastLanguage();

        if (LanguageManager.getInstance().checkIfLocalizedAssetIsAvailable(filename)) {
            filename = LOCALIZATIONS_FOLDER_NAME + '/' + languageName + '/' + filename;
        }

        String file = addSizeToBitmapPath(filename, size);
        return mContext.getAssets().open(mApplicationAssetsDir + "/" + file);
    }

    public static String addSizeToBitmapPath(String path, int size) {
        String extension = FilenameUtils.getExtension(path);
        String filename = FilenameUtils.removeExtension(path);
        return filename + "_" + size + "." + extension;
    }

    private boolean isSystemImage(String pName) {
        return (getErrorPlaceholderPath().equals(pName) || getLoadingPlaceholderString().equals(pName) || getCurtainPlaceholderString().equals(pName));

    }

    private String getAssetsDir() {
        String res = mContext.getString(RWrapper.string.getDeveloperAssetsPrefix());
        if (res == null) {
            return "";
        }
        return res;
    }

    public String getLocalizedAssetsDir() {
        String res = mContext.getString(RWrapper.string.getDeveloperAssetsPrefix()) + '/' + LanguageManager.getInstance().getLastLanguage();
        if (res == null) {
            return "";
        }
        return res;
    }

    public synchronized String getApplicationXml() {
        return getApplicationMainFile();
    }

    public Object getLocalizedAsset(String asset, Object[] params) {
        Object result;
        if (asset.startsWith(ASSETS_PREFIX)) {
            String assetFilename = removeAssetsPrefixFromAssetName(asset);

            result = getAssetObject(assetFilename, params);

            return result;
        } else {
            throw new InvalidParameterException(asset + " is not a valid asset name.");
        }
    }

    public synchronized Object getAsset(String asset) {
        return getAsset(asset, null);
    }

    public synchronized Object getAsset(String asset, Object[] params) {
        String assetName = removeAssetsPrefixFromAssetName(asset);
        return getAssetObject(assetName, params);
    }

    private Object getAssetObject(String assetName, Object[] params) {
        if (assetName.endsWith(XML_EXTENSION) || assetName.endsWith(JSON_EXTENSION)) {
            return getFileFromAssetsAsStream(assetName);
        } else if (assetName.endsWith(TTF_EXTENSION)) {
            return getTypeFace(assetName);
        } else {
            return getBitmapFromAssets(assetName, params);
        }
    }

    private String removeAssetsPrefixFromAssetName(String asset) {
        int startIndex = ASSETS_PREFIX.length();
        return asset.substring(startIndex);
    }

    public AssetFileDescriptor getAssetFileDescriptor(String fileName) throws IOException {
        fileName = fileName.replace("assets://", "");
        fileName = mApplicationAssetsDir + '/' + fileName;
        return mContext.getAssets().openFd(fileName);
    }

    private Typeface getTypeFace(String assetName) {
        FontLibrary fontLibrary = FontLibrary.getInstance();
        Typeface typeface;

        try {
            typeface = fontLibrary.getTypeface(assetName);
        } catch (Resources.NotFoundException e) {
            typeface = Typeface.createFromAsset(mContext.getAssets(), assetName);
            fontLibrary.addTypeface(assetName, typeface);
        } catch (Exception e) {
            ExceptionManager.getInstance().handleException(e, false);
            return null;
        }

        return typeface;
    }

    public synchronized String getCurtainPlaceholderString() {
        return mContext.getString(RWrapper.string.getCurtainPlaceholder());
    }

    public String getString(int stringId) {
        return mContext.getString(stringId);
    }

    public String[] listAssets(String path) throws IOException {
        return mContext.getAssets().list(path);
    }

}
