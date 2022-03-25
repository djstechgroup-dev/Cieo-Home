package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.helpers.time.DateNamesHolder;
import com.kinetise.helpers.time.DateParser;
import com.kinetise.helpers.time.DateSourceType;
import com.kinetise.helpers.time.ServerTimeManager;
import com.kinetise.support.logger.Logger;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AGDateDataDesc extends AGTextDataDesc {

    private DateSourceType mDataSource;
    private String mFormat;
    private boolean mTicking;
    private boolean mIsDefaultTimezone;

    private Date mDateObject;

    private TimeZone mTimeZone;
    private boolean isLowerCaseAMPMmarker;
    private boolean isInvalidDateStringFormat;

    public AGDateDataDesc(String id) {
        super(id);
    }

    public String getCurrentDateString() {
        if (isInvalidDateStringFormat)
            return DateNamesHolder.INVALID_TIME_FORMAT_VALUE;
        if (mDataSource == DateSourceType.INTERNET) {
            //i data wciaz sie nie pobrala
            long globalServerTime = ServerTimeManager.getGlobalServerTime();
            if (globalServerTime == -1L) {
                return "";
            } else if (globalServerTime == -2L) {// jezeli sie nie udalo pobrac
                Date dateObject = getDateObject();
                return DateParser.getFormattedDateString(dateObject, mFormat, mTimeZone, isLowerCaseAMPMmarker); // zwracamy dzisiejsza date
            } else { // jezeli udalo sie pobrac ustawiamy ja tylko raz! potem bedziemy dodawac do niej sekunde co sekunde
                if (!ServerTimeManager.isDateFromServerInitialized()) {
                    mDateObject = new Date(globalServerTime);
                }
                ServerTimeManager.setDateFromServerInitialized();
                Date dateObject = getDateObject();
                return DateParser.getFormattedDateString(dateObject, mFormat, mTimeZone, isLowerCaseAMPMmarker);
            }
        } else {
            Date dateObject = getDateObject();
            return DateParser.getFormattedDateString(dateObject, mFormat, mTimeZone, isLowerCaseAMPMmarker);
        }
    }

    public void setDateVariable(VariableDataDesc variable) {
        getTextDescriptor().setText(variable);
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        parseAndSetDateObject(getTextDescriptor().getText().getStringValue());
    }

    /**
     * Parses data from node and sets it as mDateObject and text descriptor string
     */
    public void parseAndSetDateObject(String dateString) {
        Date date = new Date();

        if (mDataSource == DateSourceType.NODE) {
            setDateObjectFromNode(dateString, date);
        } else if (mDataSource == DateSourceType.LOCAL) {
            setDateObjectAndUpdateText(date);
        } else if (mDataSource == DateSourceType.INTERNET) {
            setDateObjectFromInternet();
        }
    }

    private void setDateObjectFromInternet() {
        if (ServerTimeManager.getGlobalServerTime() >= 0) {
            setDateObjectAndUpdateText(new Date(ServerTimeManager.getGlobalServerTime()));
        } else {
            setDateObjectAndUpdateText(new Date(0));
        }

        isInvalidDateStringFormat = false;
    }

    private void setDateObjectFromNode(String dateString, Date date) {
        if (mDateObject != null)
            return;
        mTimeZone = getTimeZone(dateString);

        try {
            date = DateParser.tryParseDate(dateString);
            isInvalidDateStringFormat = false;
        } catch (IndexOutOfBoundsException e) {
            Logger.w(this, "parseAndSetDateObject", dateString + " - date cannot be parsed.");
            isInvalidDateStringFormat = true;
        } catch (ParseException e) {
            Logger.w(this, "parseAndSetDateObject", dateString + " - date cannot be parsed.");
            isInvalidDateStringFormat = true;
        }
        mDateObject = (Date) date.clone();
    }

    /**
     * @param dateString
     * @return proper timezone depending on timezone and datesrc parameters from xml
     */
    private TimeZone getTimeZone(String dateString) {
        TimeZone timezone = null;

        if (mIsDefaultTimezone || mDataSource == DateSourceType.LOCAL) {
            timezone = TimeZone.getDefault();
        } else if (mDataSource == DateSourceType.INTERNET) {
            timezone = TimeZone.getTimeZone("GMT");
        } else if (mDataSource == DateSourceType.NODE) {
            timezone = getTimezoneFromString(dateString);
        }


        return timezone;
    }

    private TimeZone getTimezoneFromString(String dateString) {
        TimeZone timezone;
        String offset;

        if (dateString.toLowerCase(Locale.US).matches(DateParser.RFC3339_regexp)) {
            if (dateString.contains("Z")) {
                timezone = TimeZone.getDefault();
            } else {
                offset = dateString.substring(dateString.length() - 6);
                timezone = TimeZone.getTimeZone("GMT" + offset);
            }
        } else {
            offset = dateString.substring(26);
            if (offset.contains("-") || offset.contains("+")) {
                offset = offset.substring(0, 3) + ":" + offset.substring(3);
                timezone = TimeZone.getTimeZone("GMT" + offset);
            } else {
                timezone = TimeZone.getTimeZone(offset);
            }
        }

        return timezone;
    }

    public void updateCurrentDateText() {
        if (isInvalidDateStringFormat) {
            getTextDescriptor().getText().setValue(DateNamesHolder.INVALID_TIME_FORMAT_VALUE);
        } else {
            String currentDate = getCurrentDateString();
            getTextDescriptor().getText().setValue(currentDate);
        }
    }

    public DateSourceType getDataSource() {
        return mDataSource;
    }

    public void setDataSource(DateSourceType dataSource) {
        mDataSource = dataSource;
    }

    public void setLowerCaseAMPMmarker(boolean isLowerCaseAMPMmarker) {
        this.isLowerCaseAMPMmarker = isLowerCaseAMPMmarker;
    }

    public String getFormat() {
        return mFormat;
    }

    public void setFormat(String format) {
        mFormat = format;
    }

    public void setIsDefaultTimezone(boolean isDefaultTimezone) {
        mIsDefaultTimezone = isDefaultTimezone;
    }

    public boolean isTicking() {
        return mTicking;
    }

    public void setTicking(boolean ticking) {
        mTicking = ticking;
    }

    public boolean isInvalidDateStringFormat() {
        return isInvalidDateStringFormat;
    }

    @Override
    public AGDateDataDesc createInstance() {
        return new AGDateDataDesc(getId());
    }

    @Override
    public AGDateDataDesc copy() {
        AGDateDataDesc copied = (AGDateDataDesc) super.copy();
        copied.mDataSource = this.mDataSource;
        copied.setFormat(String.valueOf(this.mFormat));
        if (this.mDateObject != null) {
            copied.mDateObject = (Date) this.mDateObject.clone();
        }
        copied.mTicking = this.mTicking;
        copied.mIsDefaultTimezone = mIsDefaultTimezone;

        return copied;
    }

    public Date getDateObject() {
        return (Date) mDateObject.clone();
    }

    public void setDateObjectAndUpdateText(Date dateObject) {
        mDateObject = (Date) dateObject.clone();
        updateCurrentDateText();
    }

    public boolean isDefaultTimezone() {
        return mIsDefaultTimezone;
    }

    public TimeZone getTimeZone() {
        return mTimeZone;
    }

    public boolean isLowerCaseAMPMmarker() {
        return isLowerCaseAMPMmarker;
    }

    @Override
    public String toString() {
        VariableDataDesc text = getTextDescriptor().getText();
        String result = "";
        result += "Date: " + String.valueOf(mDateObject) + "; Text: ";
        result += (text == null ? "null" : String.valueOf(text.getStringValue()));
        result += "; " + super.toString();
        return result;
    }
}
