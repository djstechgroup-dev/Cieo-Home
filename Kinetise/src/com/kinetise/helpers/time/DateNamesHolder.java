package com.kinetise.helpers.time;

import com.kinetise.data.sourcemanager.LanguageManager;

import java.text.DateFormatSymbols;

public class DateNamesHolder {
    public static String INVALID_TIME_FORMAT_VALUE;

    private static String[] mShortDayNames;
    private static String[] mLongDayNames;
    private static String[] mShortMonthNames;
    private static String[] mLongMonthNames;
    private static DateFormatSymbols mDateFormatSymbols;

    public static void init(){
        mDateFormatSymbols = new DateFormatSymbols();
    }

    public static void initializeDateNames() {
        init();
        String[] pShortDayNames = new String[7];
        String[] pLongDayNames = new String[7];
        String[] pShortMonthNames = new String[12];
        String[] pLongMonthNames = new String[12];

        for (int i = 1; i <= 12; i++) {
            pShortMonthNames[i - 1] = LanguageManager.getInstance().getString("mmm" + i);
            pLongMonthNames[i - 1] = LanguageManager.getInstance().getString("mmmm" + i);
        }
        setShortMonthNames(pShortMonthNames);
        setLongMonthNames(pLongMonthNames);

        for (int i = 1; i <= 7; i++) {
            pShortDayNames[i - 1] = LanguageManager.getInstance().getString("ddd" + i);
            pLongDayNames[i - 1] = LanguageManager.getInstance().getString("dddd" + i);
        }
        setShortDayNames(pShortDayNames);
        setLongDayNames(pLongDayNames);
        INVALID_TIME_FORMAT_VALUE = LanguageManager.getInstance().getString(LanguageManager.INVALID_DATE_FORMAT);
    }

    public static String[] getShortDayNames() {
        return mShortDayNames.clone();
    }

    public static void setShortDayNames(String[] shortDayNames) {
        String[] shortWeekdays = new String[8];
        System.arraycopy(shortDayNames, 0, shortWeekdays, 1, shortWeekdays.length - 1);
        mDateFormatSymbols.setShortWeekdays(shortWeekdays);
        mShortDayNames = shortDayNames;
    }

    public static String[] getLongDayNames() {
        return mLongDayNames.clone();
    }

    public static void setLongDayNames(String[] longDayNames) {
        String[] weekdays = new String[8];
        System.arraycopy(longDayNames, 0, weekdays, 1, weekdays.length - 1);
        mDateFormatSymbols.setWeekdays(weekdays);
        mLongDayNames = longDayNames;
    }

    public static String[] getShortMonthNames() {
        return mShortMonthNames.clone();
    }

    public static void setShortMonthNames(String[] shortMonthNames) {
        String[] shortMonths = new String[12];
        System.arraycopy(shortMonthNames, 0, shortMonths, 0, shortMonths.length);
        mDateFormatSymbols.setShortMonths(shortMonths);
        mShortMonthNames = shortMonthNames;
    }

    public static String[] getLongMonthNames() {
        return mLongMonthNames;
    }

    public static void setLongMonthNames(String[] longMonthNames) {
        String[] months = new String[12];
        System.arraycopy(longMonthNames, 0, months, 0, months.length);
        mDateFormatSymbols.setMonths(months);
        mLongMonthNames = longMonthNames;
    }

    public static DateFormatSymbols getFormatSymbols(boolean isLowerCaseAMPMmarker) {
        DateFormatSymbols result = mDateFormatSymbols;
        if(isLowerCaseAMPMmarker)
            result.setAmPmStrings(new String[]{"am","pm"});
        return result;
    }
}
