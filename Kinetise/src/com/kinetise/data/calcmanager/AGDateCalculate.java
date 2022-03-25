package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.actions.StringVariableDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.TextCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGDateDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.TextDescriptor;
import com.kinetise.data.systemdisplay.TextMeasurer;
import com.kinetise.helpers.time.DateNamesHolder;
import com.kinetise.helpers.time.DateParser;

import java.util.Calendar;

public class AGDateCalculate extends AGTextCalculate {

    private static AGDateCalculate mInstance;

    @Override
    public void measureWidthForMin(AbstractAGElementDataDesc dataDesc,
                                   double maxWidth, double maxSpaceForMax) {
        AGDateDataDesc dateDataDesc = (AGDateDataDesc) dataDesc;
        TextDescriptor textDescriptor = dateDataDesc.getTextDescriptor();
        AGViewCalcDesc calcDesc = dateDataDesc.getCalcDesc();
        TextCalcDesc textCalcDesc = textDescriptor.getCalcDescriptor();
        String longestText = getLongestTextForCurrentFormat(dataDesc);

        dateDataDesc.getTextDescriptor().setText(new StringVariableDataDesc(longestText));
        super.measureWidthForMin(dataDesc, maxWidth, maxSpaceForMax);
        double measuredWidth = Math.ceil(textDescriptor.getCalcDescriptor().getTextWidth());

        dateDataDesc.getTextDescriptor().setText(new StringVariableDataDesc(dateDataDesc.getCurrentDateString()));
        super.measureWidthForMin(dataDesc, measuredWidth, maxSpaceForMax);
        textCalcDesc.setTextWidth(measuredWidth);
        if (measuredWidth > maxWidth) {
            calcDesc.setWidth(maxWidth
                    + calcDesc.getPaddingLeft()
                    + calcDesc.getPaddingRight());
        } else {
            calcDesc.setWidth(measuredWidth
                    + calcDesc.getPaddingLeft()
                    + calcDesc.getPaddingRight());
        }
    }

    protected String getLongestTextForCurrentFormat(
            AbstractAGElementDataDesc dataDesc) {
        AGDateDataDesc dateDesc = (AGDateDataDesc) dataDesc;
        String format = dateDesc.getFormat();
        int monthIndex = getLongestMonthIndex(dateDesc, format);
        int weekdayIndex = getLongestWeekdayIndex(dateDesc, format);
        Calendar calendar = getCalendarInstanceForLongestDate(format, monthIndex, weekdayIndex);
        return DateParser.getFormattedDateString(calendar.getTime(), dateDesc.getFormat(), dateDesc.getTimeZone(), ((AGDateDataDesc) dataDesc).isLowerCaseAMPMmarker());
    }

    private Calendar getCalendarInstanceForLongestDate(String format, int longestMonthIndex, int longestWeekdayIndex) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2000);
        calendar.set(Calendar.MONTH, longestMonthIndex);
        calendar.set(Calendar.DAY_OF_WEEK, longestWeekdayIndex);
        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);

        boolean formatUsesAmPm = format.contains("a");
        if (formatUsesAmPm) {
            calendar.set(Calendar.HOUR_OF_DAY, 22);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 10);
        }

        boolean formatHasLeadingZeroesForMinutes = format.contains("mm");
        if(formatHasLeadingZeroesForMinutes) {
            calendar.set(Calendar.MINUTE, 0);
        } else {
            calendar.set(Calendar.MINUTE, 20);
        }

        boolean formatHasLeadingZeroesForSeconds = format.contains("ss");
        if(formatHasLeadingZeroesForSeconds){
            calendar.set(Calendar.SECOND, 0);
        } else {
            calendar.set(Calendar.SECOND,20);
        }

        calendar.set(Calendar.MILLISECOND, 20);
        return calendar;
    }

    private int getLongestWeekdayIndex(AGDateDataDesc dateDesc, String format) {
        double maxLen;
        double realStrLen;
        String[] longDayNames = getLongDayNames();
        String[] shortDayNames = getShortDayNames();
        boolean formatUsesFullWeekday = format.contains("EEEE");
        boolean formatUsesShortWeekday = format.contains("EEE");
        int weekdayIndex = 0;
        maxLen = 0;
        for (int i = 0; i < longDayNames.length; i++) {
            realStrLen = measureString(dateDesc, ((formatUsesFullWeekday) ? longDayNames[i] : "") + ((formatUsesShortWeekday) ? shortDayNames[i] : ""));
            if (realStrLen > maxLen) {
                maxLen = realStrLen;
                weekdayIndex = i;
            }
        }
        return weekdayIndex;
    }

    protected String[] getShortDayNames() {
        return DateNamesHolder.getShortDayNames();
    }

    protected String[] getLongDayNames() {
        return DateNamesHolder.getLongDayNames();
    }

    private int getLongestMonthIndex(AGDateDataDesc dateDesc, String format) {
        String[] longMonthNames = getLongMonthNames();
        String[] shortMonthNames = getShortMonthNames();
        boolean formatUsesFullMonth = format.contains("MMMM");
        String format2 = format.replace("MMMM","");
        boolean formatUsesShortMonth = format2.contains("MMM");
        double currentStringLength;
        int longestMonthIndex = 0;
        double longestStringLength = 0;
        if(formatUsesFullMonth || formatUsesShortMonth) {
            for (int i = 0; i < longMonthNames.length; i++) {
                String longMonthName = (formatUsesFullMonth) ? longMonthNames[i] : "";
                String shortMonthName = (formatUsesShortMonth) ? shortMonthNames[i] : "";
                currentStringLength = measureString(dateDesc, longMonthName + shortMonthName);
                if (currentStringLength > longestStringLength) {
                    longestStringLength = currentStringLength;
                    longestMonthIndex = i;
                }
            }
            return longestMonthIndex;
        } else {
            return 9;
        }
    }

    protected String[] getShortMonthNames() {
        return DateNamesHolder.getShortMonthNames();
    }

    protected String[] getLongMonthNames() {
        return DateNamesHolder.getLongMonthNames();
    }

    public static AGDateCalculate getInstance() {
        if (mInstance == null) {
            mInstance = new AGDateCalculate();
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    private double measureString(AGDateDataDesc dataDesc, String measured) {
        TextDescriptor textDescriptor = dataDesc.getTextDescriptor();
        TextMeasurer textMeasurer = new TextMeasurer(textDescriptor);
        textMeasurer.measure(measured, 1000);

        return textDescriptor.getCalcDescriptor().getTextWidth();
    }

}