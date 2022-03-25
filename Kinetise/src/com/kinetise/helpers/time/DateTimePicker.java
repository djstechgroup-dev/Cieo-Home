package com.kinetise.helpers.time;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.datadescriptors.AGDatePickerDataDesc;
import com.kinetise.data.descriptors.types.AGDatePickerModeType;

import java.util.Calendar;
import java.util.Date;

public class DateTimePicker implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private final IDateTimePickedListener mListener;
    private final Date mMinDate;
    private final Date mMaxDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private AGDatePickerModeType mMode;

    public DateTimePicker(Date dateObject, AGDatePickerModeType datePickerMode, Date minDate, Date maxDate, IDateTimePickedListener listener) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateObject);
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (datePickerMode == AGDatePickerModeType.DATE) {
            mHour = 0;
            mMinute = 0;
        } else {
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
        }

        if (datePickerMode == AGDatePickerModeType.TIME) {
            mMinDate = null;
            mMaxDate = null;
        } else {
            mMinDate = minDate;
            mMaxDate = maxDate;
        }

        mMode = datePickerMode;
        mListener = listener;
    }

    public void show() {
        if (mMode == AGDatePickerModeType.TIME) {
            showTimePicker(mHour, mMinute);
        } else {
            showDatePicker(mYear, mMonth, mDay, mMinDate, mMaxDate);
        }
    }

    private void showDatePicker(int year, int month, int day, Date minDate, Date maxDate) {
        Activity activity = AGApplicationState.getInstance().getActivity();
        DatePickerDialog dialog = new DatePickerDialog(activity, this, year, month, day);
        DatePicker picker = dialog.getDatePicker();
        if (minDate != null)
            picker.setMinDate(minDate.getTime());
        if (maxDate != null)
            picker.setMaxDate(maxDate.getTime());
        dialog.show();
    }

    private void showTimePicker(int hour, int minute) {
        Activity activity = AGApplicationState.getInstance().getActivity();
        TimePickerDialog dialog = new TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity));
        dialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        if (view.isShown()) {
            mHour = hour;
            mMinute = minute;
            onDateTimePicked();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (view.isShown()) {
            mYear = year;
            mMonth = month;
            mDay = day;
            if (mMode == AGDatePickerModeType.DATETIME) {
                showTimePicker(mHour, mMinute);
            } else {
                onDateTimePicked();
            }
        }
    }

    private void onDateTimePicked() {
        if (mListener != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(mYear, mMonth, mDay, mHour, mMinute, 0);
            Date date = new Date(cal.getTimeInMillis());
            date = AGDatePickerDataDesc.getDateInRange(date, mMinDate, mMaxDate);
            mListener.onDateTimePicked(date);
        }
    }

}
