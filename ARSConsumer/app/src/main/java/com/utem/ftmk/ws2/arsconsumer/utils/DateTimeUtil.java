package com.utem.ftmk.ws2.arsconsumer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {

    private static final String FORMAT_DEFAULT = "dd MMM yyyy";
    public static final long TWELVE_YEAR = 378683424000L;

    public static String format(long date) {
        return new SimpleDateFormat(FORMAT_DEFAULT, Locale.getDefault()).format(new Date(date));
    }

}
