package com.holidayjournal.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class DateFormatter {

    private static DateTimeFormatter dtf = DateTimeFormat.forPattern("d MMM yyyy");

    public static long toLong(String dateString) {
        try {
            return dtf.parseMillis(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static DateTime toDate(long millis) {
        return new DateTime(Long.valueOf(millis), DateTimeZone.getDefault());
    }

    public static String toString(long millis) {
        return dtf.print(millis);
    }

}
