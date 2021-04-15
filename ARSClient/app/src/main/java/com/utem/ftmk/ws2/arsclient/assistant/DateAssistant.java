package com.utem.ftmk.ws2.arsclient.assistant;

import android.content.res.Resources;

import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.model.plan.Plan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateAssistant {

    private static final int DAY = 0;
    private static final int MONTH = 1;
    private static final int YEAR = 2;

    public static final int HOUR_IN_MILLIS = 60 * 60 * 1000;
    public static final int DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;

    public static String format(long date, int type) {
        switch (type) {
            case MONTH:
                return formatMonth(date);
            case YEAR:
                return formatYear(date);
            default:
                return formatDefault(date);

        }
    }

    public static String formatDefault(long date) {
        return new SimpleDateFormat(App.getContext().getString(R.string.date_format_default),
                Locale.getDefault()).format(new Date(date));
    }

    public static String formatMonth(long date) {
        return new SimpleDateFormat(App.getContext().getString(R.string.date_format_month),
                Locale.getDefault()).format(new Date(date));
    }

    public static String formatYear(long date) {
        return new SimpleDateFormat(App.getContext().getString(R.string.date_format_year),
                Locale.getDefault()).format(new Date(date));
    }

    public static Date parsePaymentDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat(
                App.getContext().getString(R.string.parse_format_payment_date),
                Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date parsedDate = new Date();
        try {
            parsedDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    public static long addDate(long date, int dateToPlus, int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int field = Calendar.DAY_OF_MONTH;
        switch (type) {
            case DAY:
                field = Calendar.DAY_OF_MONTH;
                break;
            case MONTH:
                field = Calendar.MONTH;
                break;
            case YEAR:
                field = Calendar.YEAR;
                break;
        }
        calendar.add(field, dateToPlus);
        return calendar.getTimeInMillis();
    }

    public static long addDays(long date, int daysToPlus) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.add(Calendar.DAY_OF_MONTH, daysToPlus);
        return calendar.getTimeInMillis();
    }

    public static long addDate(long date, int yearsToPlus, int monthsToPlus, int daysToPlus) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.add(Calendar.YEAR, yearsToPlus);
        calendar.add(Calendar.MONTH, monthsToPlus);
        calendar.add(Calendar.DAY_OF_MONTH, daysToPlus);
        return calendar.getTimeInMillis();
    }

    public static Calendar toCalendar(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return calendar;
    }

    public static String planDurationToString(Plan plan) {
        Resources res = App.getContext().getResources();
        int year = plan.getDurationYear();
        int month = plan.getDurationMonth();
        int day = plan.getDurationDay();
        String duration = "";
        duration += year == 0 ? "" : res.getQuantityString(R.plurals.number_of_year, year, year) + " ";
        duration += month == 0 ? "" : res.getQuantityString(R.plurals.number_of_month, month, month) + " ";
        duration += day == 0 ? "" : res.getQuantityString(R.plurals.number_of_day, day, day);
        return duration.trim();
    }

    public static String planLeftDurationToString(long duration) {
        Resources res = App.getContext().getResources();
        String durationString = "";

        int dayLeft = (int) (duration / DAY_IN_MILLIS);
        durationString += dayLeft == 0 ? "" : res.getQuantityString(R.plurals.number_of_day, dayLeft, dayLeft) + " ";

        duration %= DAY_IN_MILLIS;
        int hourLeft = (int) (duration / HOUR_IN_MILLIS);
        durationString += hourLeft == 0 ? "" : res.getQuantityString(R.plurals.number_of_hour, hourLeft, hourLeft);

        return durationString.trim();
    }
}
