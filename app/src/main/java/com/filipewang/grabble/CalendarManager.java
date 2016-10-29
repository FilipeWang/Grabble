package com.filipewang.grabble;

import java.util.Calendar;

/**
 * Created by Filipe on 29-Oct-16.
 * This class manages anything to do with dates.
 */
public class CalendarManager {
    Calendar cal;
    private String[] weekDays = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};

    public CalendarManager(){
        cal = Calendar.getInstance();
    }

    public String getDayOfWeek(){
        int day = cal.get(Calendar.DAY_OF_WEEK);
        day = day - 1;
        String currWeekDay = weekDays[day];
        return currWeekDay;
    }
}
