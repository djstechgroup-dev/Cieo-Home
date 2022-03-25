package com.kinetise.data.systemdisplay.views.text;

import java.util.HashMap;

/**
 * @author: Marcin Narowski
 * Date: 07.10.13
 * Time: 08:50
 */
public class FontSizeHelper {

    public boolean isAlreadyAdded(String font) {
        return mMap.containsKey(font);
    }

    private static class FontSizeHelperData {
        private final double mA;
        private final double mB;

        public FontSizeHelperData(double a, double b) {
            mA = a;
            mB = b;
        }

        public double getSize(double fontSize) {
            return mA * fontSize + mB;
        }
    }

    private static FontSizeHelper mInstance;
    private HashMap<String, FontSizeHelperData> mMap = new HashMap<String, FontSizeHelperData>();

    public static FontSizeHelper getInstance() {
        if (mInstance == null) {
            synchronized (FontSizeHelper.class){
                if (mInstance == null) {
                    mInstance = new FontSizeHelper();
                }
            }
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    private FontSizeHelper() {
    }

    public void addFontData(String fontName, double da, double db) {
        mMap.put(fontName, new FontSizeHelperData(da, db));
    }


    public double getSizeFor(String fontName, double fontSize) {
        if (mMap.containsKey(fontName)) {
            FontSizeHelperData fontFunction = mMap.get(fontName);
            return fontFunction.getSize(fontSize);
        }

        return 0;
    }
}
