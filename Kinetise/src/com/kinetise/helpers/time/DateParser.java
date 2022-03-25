package com.kinetise.helpers.time;

import java.lang.ref.SoftReference;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DateParser {
    public static final String RFC3339_regexp = "\\d{4}-\\d{2}-\\d{2}t\\d{2}:\\d{2}:\\d{2}.*";
    public static final String RFC822_pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
    public static final String RFC3339_basic_pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String RFC3339_sec_fraction_pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String RFC3339_zero_timezone = "+0000";

    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
    public static final String PATTERN_RFC850 = "EEEE, dd-MMM-yy HH:mm:ss zzz";
    public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

    private static final int RFC3339_no_sec_fraction_digits = 3;

    private static HashMap<String, SimpleDateFormat> dateFormatHashMap = new HashMap<String, SimpleDateFormat>();

    public static Date tryParseDate(String dateString) throws java.text.ParseException, IndexOutOfBoundsException {
        Date date;

        if (dateString == null) {
            dateString = "";
        } else {
            dateString = dateString.replace("Z", RFC3339_zero_timezone);
        }

        if (dateString.toLowerCase(Locale.US).matches(RFC3339_regexp)) {
            try {
                String dateFormat = RFC3339_basic_pattern; // RFC3339
                boolean setLenient = false;
                date = parseDate(dateString, dateFormat, setLenient);
            } catch (java.text.ParseException pe) {
                // we need to check how many "second fractions" are used to add trailing zeros to input string
                // +2 in calculation below is for ' characters in dateFormat
                int numberOfSecFractionDigits = dateString.length() - RFC3339_basic_pattern.length() + 2 - RFC3339_zero_timezone.length();
                if (numberOfSecFractionDigits < RFC3339_no_sec_fraction_digits) {
                    StringBuilder sb = new StringBuilder();
                    // append part before second fraction part
                    sb.append(dateString.substring(0, dateString.indexOf(".") + 1 + numberOfSecFractionDigits));
                    // append trailing zeros
                    for (int i = 0; i < RFC3339_no_sec_fraction_digits - numberOfSecFractionDigits; i++) {
                        sb.append("0");
                    }
                    // append timezone (everything after second fractions)
                    sb.append(dateString.substring(dateString.length() - RFC3339_zero_timezone.length()));
                    dateString = sb.toString();
                }
                String dateFormat = RFC3339_sec_fraction_pattern;
                boolean setLenient = true;
                date = parseDate(dateString, dateFormat, setLenient);
            }
            return date;

        } else {
            String dateFormat = RFC822_pattern;

            date = parseDate(dateString, dateFormat, false);

            return date;
        }
    }

    public static Date parseDate(String dateString, String dateFormat, boolean setLenient) throws ParseException {
        // check if have SimpleDateFormat for this dateFormat already created
        if (!dateFormatHashMap.containsKey(dateFormat)) {
            dateFormatHashMap.put(dateFormat, new SimpleDateFormat(dateFormat, Locale.US));
        }

        SimpleDateFormat simpleDateFormat = dateFormatHashMap.get(dateFormat);

        simpleDateFormat.setLenient(setLenient);
        return simpleDateFormat.parse(dateString);
    }

    public static String getDateAsRFC3339(Date date) {
        return getFormattedDateString(date, RFC3339_basic_pattern, TimeZone.getDefault(), false);
    }

    public static String getFormattedDateString(Date date, String format, TimeZone timeZone, boolean isLowerCaseAMPMmarker) {
        Locale defaultLocale = Locale.getDefault();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, defaultLocale);

        if (timeZone != null) {
            simpleDateFormat.setTimeZone(timeZone);
        }

        DateFormatSymbols dateFormatSymbols = getDateFormatSymbols(isLowerCaseAMPMmarker);
        simpleDateFormat.setDateFormatSymbols(dateFormatSymbols);

        return simpleDateFormat.format(date);
    }

    public static DateFormatSymbols getDateFormatSymbols(boolean isLowerCaseAMPMmarker) {
        return DateNamesHolder.getFormatSymbols(isLowerCaseAMPMmarker);
    }

    //region removed from API 23 org.apache.http.DateUtils methods

    private static final String[] DEFAULT_PATTERNS = new String[]{
            PATTERN_RFC1036,
            PATTERN_RFC1123,
            PATTERN_ASCTIME
    };

    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;

    public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(GMT);
        calendar.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
    }


    public static Date parseDate(final String dateValue, String[] dateFormats) throws Exception {
        return parseDate(dateValue, dateFormats, null);
    }

    public static Date parseDate(
            String dateValue,
            String[] dateFormats,
            Date startDate
    ) throws Exception {

        if (dateValue == null) {
            throw new IllegalArgumentException("dateValue is null");
        }
        if (dateFormats == null) {
            dateFormats = DEFAULT_PATTERNS;
        }
        if (startDate == null) {
            startDate = DEFAULT_TWO_DIGIT_YEAR_START;
        }
        // trim single quotes around date if present
        // see issue #5279
        if (dateValue.length() > 1
                && dateValue.startsWith("'")
                && dateValue.endsWith("'")
                ) {
            dateValue = dateValue.substring(1, dateValue.length() - 1);
        }

        for (String dateFormat : dateFormats) {
            SimpleDateFormat dateParser = DateFormatHolder.formatFor(dateFormat);
            dateParser.set2DigitYearStart(startDate);

            try {
                return dateParser.parse(dateValue);
            } catch (ParseException pe) {
                // ignore this exception, we will try the next format
            }
        }

        // we were unable to parse the date
        throw new Exception("Unable to parse the date " + dateValue);
    }

    final static class DateFormatHolder {

        private static final ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>>
                THREADLOCAL_FORMATS = new ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>>() {

            @Override
            protected SoftReference<Map<String, SimpleDateFormat>> initialValue() {
                return new SoftReference<Map<String, SimpleDateFormat>>(
                        new HashMap<String, SimpleDateFormat>());
            }

        };

        public static SimpleDateFormat formatFor(String pattern) {
            SoftReference<Map<String, SimpleDateFormat>> ref = THREADLOCAL_FORMATS.get();
            Map<String, SimpleDateFormat> formats = ref.get();
            if (formats == null) {
                formats = new HashMap<String, SimpleDateFormat>();
                THREADLOCAL_FORMATS.set(
                        new SoftReference<Map<String, SimpleDateFormat>>(formats));
            }

            SimpleDateFormat format = formats.get(pattern);
            if (format == null) {
                format = new SimpleDateFormat(pattern, Locale.US);
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                formats.put(pattern, format);
            }

            return format;
        }

    }

    //endregion
}
