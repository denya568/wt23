package ru.wt23.worldtrick23.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyTime {
    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss/dd.MM.yyyy");
        return dateFormat.format(new Date());
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }

    public static String getTimeFromMills(long mills) {
        String time = "";
        Calendar calendar = getCalendar();
        Date date = new Date();
        date.setTime(mills);
        calendar.setTime(date);
        String hours = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minutes = String.valueOf(calendar.get(Calendar.MINUTE));

        if (hours.length() != 2) {
            hours = "0" + hours;
        }
        if (minutes.length() != 2) {
            minutes = "0" + minutes;
        }
        time = hours + ":" + minutes;
        return time;
    }

    public static String getDateFromMills(long mills) {
        String sDate;
        Calendar calendar = getCalendar();
        Date date = new Date();
        date.setTime(mills);
        calendar.setTime(date);
        String day = String.valueOf(calendar.get(Calendar.DATE));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String year = String.valueOf(calendar.get(Calendar.YEAR));

        if (day.length() != 2) {
            day = "0" + day;
        }
        if (month.length() != 2) {
            month = "0" + month;
        }

        sDate = day + "." + month + "." + year;
        return sDate;
    }
}