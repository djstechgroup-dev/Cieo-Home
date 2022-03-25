package com.kinetise.data.systemdisplay.views.text;

import android.content.Context;
import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.packagemanager.AppPackageManager;
import com.kinetise.data.systemdisplay.helpers.AGTypefaceLocation;
import com.kinetise.helpers.calcmanagerhelper.PrecisionFixHelper;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


public class CharWidthLoaderHelper {

    private static final Character REPLACE_CHAR = '?';
    private static final String UTF8 = "UTF-8";
    private static FontSizeHelper sFontSizeHelper;
    public String mFont;
    private static Map<String, Map<Character, Double>> sMaps = new HashMap<String, Map<Character, Double>>();


    public CharWidthLoaderHelper(String font) {
        setFont(font);
    }

    public static void init(Context context) {
        sFontSizeHelper = FontSizeHelper.getInstance();
        CharWidthLoaderHelper.loadAllFontsData(context);
    }

    public static void clear(){
        sFontSizeHelper = null;
    }

    private void setFont(String font) {
        mFont = font;
    }

    public double getCharWidth(Character searched, Double fontSize) {
        if (!sMaps.get(mFont).containsKey(searched))
            searched = REPLACE_CHAR;

        return sMaps.get(mFont).get(searched) * fontSize;
    }

    private static void putInMapIfMissing(String fontName, Context context) {

        if (!sMaps.containsKey(fontName)) {
            sMaps.put(fontName, new HashMap<Character, Double>());
            String dataFile = fontName + Constants.FONT_DATA_SUFIX;
            BufferedReader reader =null;
            try {
                Map<Character, Double> characterMap = sMaps.get(fontName);

                InputStream stream = context.getAssets().open(AGTypefaceLocation.FONT_FOLDER + "/" + dataFile);
                reader = new BufferedReader(new InputStreamReader(stream, Charset.defaultCharset()));
                String line;
                while ((line = reader.readLine()) != null) {
                    String lineWithoutColon = line.substring(0, line.length() - 1);
                    String[] values = lineWithoutColon.split(":");
                    Character value = StringEscapeUtils.unescapeJava(values[0]).charAt(0);
                    characterMap.put(value, PrecisionFixHelper.toPrecision(Double.parseDouble(values[1]), 3));
                }
                stream.close();
            } catch (IOException e) {
                ExceptionManager.getInstance().handleException(e, false);
            }
            finally {
                if(reader!=null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private static void loadAllFontsData(Context context) {

        try {
            String[] filenames = AppPackageManager.getInstance().getPackage().listAssets(AGTypefaceLocation.FONT_FOLDER);
            for (String fileName : filenames) {
                if (fileName.endsWith(Constants.FONT_EXT)) {
                    String fontName = fileName.replace(Constants.FONT_EXT, "");
                    if (!isFontAdded(fontName)) {
                        putInMapIfMissing(fontName, context);
                        addFontSizeFunction(fontName, context);
                    }
                }
            }
        } catch (IOException e) {
            ExceptionManager.getInstance().handleException(e, true);
        }
        finally {
        }
    }

    private static void addFontSizeFunction(String fontName, Context context) {
        BufferedReader bufferedReader = null;
        try {
            InputStream stream = context.getAssets().open(getFontDataFilePath(fontName));
            InputStreamReader reader = new InputStreamReader(stream,UTF8);
            bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.trim().equals(""))
                    continue;
                String lineWithoutColon = line.substring(0,line.length()-1);
                String[] values = lineWithoutColon.split(Constants.SPLITTER);
                sFontSizeHelper.addFontData(fontName, Double.parseDouble(values[0]), Double.parseDouble(values[1]));
            }
        } catch (IOException e) {
            ExceptionManager.getInstance().handleException(e, false);
        }
        finally {
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static String getFontDataFilePath(String fontName){
        return AGTypefaceLocation.FONT_FOLDER + Constants.DIR_SEPARATOR + fontName + Constants.HEIGHT + Constants.EXT_TXT;
    }

    private static boolean isFontAdded(String fontname) {
        return sFontSizeHelper.isAlreadyAdded(fontname);
    }

    public double getLineHeight(String fontName, double fontSize) {
        return sFontSizeHelper.getSizeFor(fontName, fontSize);
    }


}
