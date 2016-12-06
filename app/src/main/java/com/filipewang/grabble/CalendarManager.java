package com.filipewang.grabble;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class manages anything to do with dates.
 * Its main two functions is getting the correct week day and the current day which
 * is later used as an ID for different actions.
 */
public class CalendarManager {
    Calendar cal;
    private String[] weekDays = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};

    public CalendarManager(){
        cal = Calendar.getInstance();
    }

    // Gets the current day of the week, for the Calendar cal, all in lower case.
    public String getDayOfWeek(){
        int day = cal.get(Calendar.DAY_OF_WEEK);
        day = day - 1;
        return weekDays[day];
    }

    // Gets the current day as an ID in the format of ddMMyyy.
    public String getCurrentDay(){
        DateFormat df = new SimpleDateFormat("ddMMyyy");
        return df.format(cal.getTime());
    }
}
