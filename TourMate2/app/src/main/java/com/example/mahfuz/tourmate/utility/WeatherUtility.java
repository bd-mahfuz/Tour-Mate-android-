package com.example.mahfuz.tourmate.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeatherUtility {

    public static String milliToDateConverter(Long millisecond) {
        millisecond = millisecond*1000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisecond);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String sDate = dateFormat.format(calendar.getTime());
        return sDate;
    }

    public static String milliToDayConverter(Long millisecond) {
        millisecond = millisecond*1000;
        Calendar cal = Calendar.getInstance();

        cal.setTime(new Date(millisecond));

        int dow = cal.get(Calendar.DAY_OF_WEEK);

        switch (dow) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
        }
        return "Unknown";
    }

    public static String getTime(Long millisecond) {
        millisecond = millisecond*1000;
        Calendar cal = Calendar.getInstance();

        cal.setTime(new Date(millisecond));
        DateFormat dateFormat = new SimpleDateFormat("HH:mm a");
        String time = dateFormat.format(cal.getTime());
        return time;
    }
}
