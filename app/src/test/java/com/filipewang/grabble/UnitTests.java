package com.filipewang.grabble;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by Filipe on 29-Nov-16.
 * This class contains unit tests for 3 of the required marking criteria:
 * - Calculate the correct value of a given word
 * - Check whether a word is valid or not
 * - Loading the right map for a given day
 */

public class UnitTests {

    // Testing different words in the dictionary against their calculated values
    @Test
    public void checkWordValue_AARONIC() throws Exception{assertEquals(42,WordScreen.getValue("AARONIC"));}

    @Test
    public void checkWordValue_ABILITY() throws Exception{assertEquals(62,WordScreen.getValue("ABILITY"));}

    @Test
    public void checkWordValue_BOLTAGE() throws Exception{assertEquals(59,WordScreen.getValue("BOLTAGE"));}

    @Test
    public void checkWordValue_FRANCES() throws Exception{assertEquals(53,WordScreen.getValue("FRANCES"));}

    @Test
    public void checkWordValue_PAEONIC() throws Exception{assertEquals(51,WordScreen.getValue("PAEONIC"));}

    @Test
    public void checkWordValue_SERPENT() throws Exception{assertEquals(44,WordScreen.getValue("SERPENT"));}

    /**
     * Checking if a word is valid or not using different inputs.
     * Case 1: Valid word upper case, return true
     * Case 2: Word longer than 7 characters, return false
     * Case 3: Numbers, return false
     * Case 4: Valid word lower case, return true
     * Case 5: Mix of letters and numbers, return false
     * Case 6: Same as case 1 but lower case, return true
     */

    @Test
    public void checkWordValid_AARONIC() throws Exception{
        ArrayList<String> dict = getDict();
        assertTrue(WordScreen.checkWordValidity("AARONIC",dict));
    }

    @Test
    public void checkWordValid_AARONICA() throws Exception{
        ArrayList<String> dict = getDict();
        assertFalse(WordScreen.checkWordValidity("AARONICA",dict));
    }

    @Test
    public void checkWordValid_11() throws Exception{
        ArrayList<String> dict = getDict();
        assertFalse(WordScreen.checkWordValidity("11",dict));
    }

    @Test
    public void checkWordValid_tarwood() throws Exception{
        ArrayList<String> dict = getDict();
        assertTrue(WordScreen.checkWordValidity("tarwood",dict));
    }

    @Test
    public void checkWordValid_EE1WSS2() throws Exception{
        ArrayList<String> dict = getDict();
        assertFalse(WordScreen.checkWordValidity("EE1WSS2",dict));
    }

    @Test
    public void checkWordValid_aaronic() throws Exception{
        ArrayList<String> dict = getDict();
        assertTrue(WordScreen.checkWordValidity("aaronic",dict));
    }

    private ArrayList<String> getDict() throws Exception{
        ArrayList<String> dictionary = new ArrayList<>();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("dictionary.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String word;
        while((word = br.readLine()) != null ){
            String wordFinal = word.toLowerCase();
            dictionary.add(wordFinal);
        }
        return dictionary;
    }

    /**
     * Test that the right week day is used for a given day (this ensures the right data)
     * is loaded for the map. Also one test to check if it gets the correct day.
     * Note the set method has the months starting at 0.
     */

    @Test
    public void calendartest_Monday() throws Exception{
        String correctDate = "monday";
        CalendarManager calendarManager = new CalendarManager();
        calendarManager.cal.set(2016,11,5); //December 5th, 2016
        String testDate = calendarManager.getDayOfWeek();
        assertEquals(correctDate,testDate);
    }

    @Test
    public void calendartest_Tuesday() throws Exception{
        String correctDate = "tuesday";
        CalendarManager calendarManager = new CalendarManager();
        calendarManager.cal.set(2016,8,6); //Septermber 6th, 2016
        String testDate = calendarManager.getDayOfWeek();
        assertEquals(correctDate,testDate);
    }

    @Test
    public void calendartest_Wednesday() throws Exception{
        String correctDate = "wednesday";
        CalendarManager calendarManager = new CalendarManager();
        calendarManager.cal.set(2017,5,28); //June 28th, 2017
        String testDate = calendarManager.getDayOfWeek();
        assertEquals(correctDate,testDate);
    }


    @Test
    public void calendartest_Thursday() throws Exception{
        String correctDate = "thursday";
        CalendarManager calendarManager = new CalendarManager();
        calendarManager.cal.set(2017,5,29); //June 29th, 2017
        String testDate = calendarManager.getDayOfWeek();
        assertEquals(correctDate,testDate);
    }

    @Test
    public void calendartest_Friday() throws Exception{
        String correctDate = "friday";
        CalendarManager calendarManager = new CalendarManager();
        calendarManager.cal.set(2010,1,19); //February 19th, 2010
        String testDate = calendarManager.getDayOfWeek();
        assertEquals(correctDate,testDate);
    }

    @Test
    public void calendartest_Saturday() throws Exception{
        String correctDate = "saturday";
        CalendarManager calendarManager = new CalendarManager();
        calendarManager.cal.set(2022,9,1); //October 1st, 2022
        String testDate = calendarManager.getDayOfWeek();
        assertEquals(correctDate,testDate);
    }

    @Test
    public void calendartest_Sunday() throws Exception{
        String correctDate = "sunday";
        CalendarManager calendarManager = new CalendarManager();
        calendarManager.cal.set(2016,10,6); //November 6th, 2016
        String testDate = calendarManager.getDayOfWeek();
        assertEquals(correctDate,testDate);
    }

    @Test
    public void calendartest_Day() throws Exception{
        String correctDay = "03122016";
        CalendarManager calendarManager = new CalendarManager();
        calendarManager.cal.set(2016,11,3); //June 28th, 2017
        String testDay = calendarManager.getCurrentDay();
        assertEquals(correctDay,testDay);
    }
}
