package com.xfdingustc.snipe.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTime {

    private static final DateFormat mDateFormat;
    private static final DateFormat mTimeFormat;
    private static final SimpleDateFormat mDayFormat;

    static {
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        mDayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
    }

    private static int mTimezone = TimeZone.getDefault().getRawOffset();
    private static Date mDate = new Date();

    public static int getTimezone() {
        return mTimezone;
    }

    public static String getCurrentDate(long timeMillis) {
        mDate.setTime(timeMillis);
        return mDateFormat.format(mDate);
    }

    public static String getCurrentTime(long timeMillis) {
        mDate.setTime(timeMillis);
        return mTimeFormat.format(mDate);
    }

    public static String getDateString(int date, long startTimeMs) {
        long time = startTimeMs + (long) date * 1000;
        mDate.setTime(time - mTimezone);
        return mDateFormat.format(mDate);
    }


    public static String getDateString(long date) {
        mDate.setTime(date);
        return mDateFormat.format(mDate);
    }

    public static String getTimeString(int date, long startTimeMs) {
        long time = startTimeMs + (long) date * 1000;
        mDate.setTime(time - mTimezone);
        return mTimeFormat.format(mDate);
    }

    public static String toString(long date, long startTimeMs) {
        long time = startTimeMs + date;
        mDate.setTime(time - mTimezone);
        return mDateFormat.format(mDate) + " " + mTimeFormat.format(mDate);
    }

    public static Date getTimeDate(long date, long timeMs) {
        long time = timeMs + date;
        mDate.setTime(time - mTimezone);
        return mDate;
    }

    public static String getDayName(int date, long startTimeMs) {
        long time = startTimeMs + (long) date * 1000;
        mDate.setTime(time - mTimezone);
        return mDayFormat.format(mDate);
    }

    public static String secondsToString(int seconds) {
        String text;
        if (seconds < 3600)
            text = String.format(Locale.ENGLISH, "%02d:%02d", (seconds % 3600) / 60, seconds % 60);
        else
            text = String.format(Locale.ENGLISH, "%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60,
                (seconds % 60));
        return text;
    }

    public static String toFileName(int date, long startTimeMs) {
        long time = startTimeMs + (long) date * 1000;
        mDate.setTime(time - mTimezone);
        DateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return format.format(mDate);
    }

    public static String toFileName(long date) {
        mDate.setTime(date - mTimezone);
        DateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return format.format(mDate);
    }
}
